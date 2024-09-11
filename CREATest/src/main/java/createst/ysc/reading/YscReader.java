package createst.ysc.reading;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The Class YscReader, contains the information of a statechart read from a ysc
 * file.
 */
public class YscReader implements IYscReader {

	/** The node in the DOM representing the statechart. */
	private Node statechartNode;
	
	/** True if the statechart has a namespace, false otherwise.*/
	private boolean hasNamespace;

	/** The statechart name. */
	private String statechartName;

	/** The list containing all the full names of states in the statechart. */
	private List<String> statesNames;

	/** The list containing all the names of events in the statechart. */
	private List<String> eventsNames;

	/** The list containing all the names of interfaces in the statechart. */
	private List<String> interfacesNames;

	/**
	 * Instantiates a new statechart.
	 *
	 * @param path the path of the .ysc file
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created
	 *                                      which satisfies the configuration
	 *                                      requested.
	 * @throws SAXException                 if any parse errors occur.
	 * @throws IOException                  if any IO errors occur.
	 * @throws NullPointerException         if the input string is null.
	 */
	public YscReader(String path) throws ParserConfigurationException, SAXException, IOException, NullPointerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(path));

		this.statechartNode = document.getElementsByTagName("sgraph:Statechart").item(0);

		this.initStatechart();
	}
	
	/**
	 * Checks if the statechart has a namespace.
	 *
	 * @return true, if the statechart has a namespace, false otherwise */
	@Override
	public boolean hasNamespace() {
		return this.hasNamespace;
	}

	/**
	 * Gets the statechart name.
	 *
	 * @return the statechart name
	 */
	@Override
	public String getStatechartName() {
		return this.statechartName;
	}

	/**
	 * Gets all states names.
	 *
	 * @return the dictionary containing all states names where the key is the
	 *         corresponding enum
	 */
	@Override
	public Map<String, String> getStatesNames() {
		// Create a dictionary for the states names with the corresponding enum as key
		Map<String, String> statesNames = new HashMap<String, String>();
		for (String name : this.statesNames) {
			String enumName = name.toUpperCase().replace('.', '_');
			statesNames.put(enumName, name);
		}
		return statesNames;
	}

	/**
	 * Gets all events names.
	 *
	 * @return the list containing all events names where the key is the
	 *         corresponding method
	 */
	@Override
	public Map<String, String> getEventsNames() {
		// Create a dictionary for the events names with the corresponding method as key
		Map<String, String> eventsNames = new HashMap<String, String>();
		for (String name : this.eventsNames) {
			String methodName = "raise" + name.substring(0, 1).toUpperCase() + name.substring(1);
			eventsNames.put(methodName, name);
		}
		return eventsNames;
	}

	/**
	 * Gets all interfaces names.
	 *
	 * @return the list containing all interfaces names where the key is the
	 *         corresponding class name
	 */
	@Override
	public Map<String, String> getInterfacesNames() {
		// Create a dictionary for the interfaces names with the corresponding class
		// name as key
		Map<String, String> interfacesNames = new HashMap<String, String>();
		for (String name : this.interfacesNames) {
			String className = name.substring(0, 1).toUpperCase() + name.substring(1);
			interfacesNames.put(className, name);
		}
		return interfacesNames;
	}

	/**
	 * Inits the statechart obtaining statechart name, states names end events
	 * names.
	 */
	private void initStatechart() {
		// Check if the statechart has a namespace
		this.hasNamespace = this.statechartNode.getAttributes().getNamedItem("namespace") != null;
		
		// Obtain the name of the statechart
		Node attribute = this.statechartNode.getAttributes().getNamedItem("name");
		this.statechartName = attribute.getNodeValue();

		// Initialize data structures
		this.statesNames = new ArrayList<String>();
		this.eventsNames = new ArrayList<String>();
		this.interfacesNames = new ArrayList<String>();

		// Search the nodes representing the starting regions of the statechart
		NodeList nodeList = this.statechartNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals("regions")) {
				// Start the visit of the subtree from the node representing the region
				this.visitNode(child, this.statesNames, this.eventsNames, this.interfacesNames);
			}
		}
	}

	/**
	 * Visit a node, recursively visit all its element child nodes.
	 *
	 * @param node            the node to visit
	 * @param statesNames     the list of all the names of the states visited so far
	 * @param eventsNames     the list of all the names of the events used in
	 *                        tranistions visited so far
	 * @param interfacesNames the list of all the names of the interfaces used in
	 *                        tranistions visited so far
	 */
	private void visitNode(Node node, List<String> statesNames, List<String> eventsNames,
			List<String> interfacesNames) {
		// If the node is a region, it may contain a final state
		// else, it is a vertex and it may have outgoing transitions
		if (node.getNodeName().equals("regions")) {
			this.checkForFinalState(node, statesNames);
		} else {
			this.checkForTransitions(node, eventsNames, interfacesNames);
			// A "normal" state is a node with name "vertices" and the attribute "xsi:type"
			// equals to "sgraph:State",
			// for that kind of node, the name is of interest
			Node attribute = node.getAttributes().getNamedItem("xsi:type");
			if (attribute.getNodeValue().equals("sgraph:State")) {
				statesNames.add(this.getFullName(node, ""));
			}
		}
		// All child element nodes that are regions or vertices are visited
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& (child.getNodeName().equals("regions") || child.getNodeName().equals("vertices")))
				visitNode(child, statesNames, eventsNames, interfacesNames);
		}
	}

	/**
	 * Check if the node (region) contatins a final state.
	 *
	 * @param node        the node representing the region
	 * @param statesNames the list of all the names of the states visited so far
	 */
	private void checkForFinalState(Node node, List<String> statesNames) {
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			// A final state is a node with name "vertices" and the attribute "xsi:type"
			// equals to "sgraph:FinalState"
			if (child.getNodeName().equals("vertices")) {
				Node attribute = child.getAttributes().getNamedItem("xsi:type");
				if (attribute.getNodeValue().equals("sgraph:FinalState")) {
					statesNames.add(this.getFullName(node, "._final_"));
					return;
				}
			}
		}
	}

	/**
	 * Check if the node (state) contatins outgoing transitions.
	 *
	 * @param node            the node representing the state
	 * @param eventsNames     the list of all the names of the events used in
	 *                        tranistions visited so far
	 * @param interfacesNames the list of all the names of the interfaces used in
	 *                        tranistions visited so far
	 */
	private void checkForTransitions(Node node, List<String> eventsNames, List<String> interfacesNames) {
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			// The nodes of interest are outgoingTransitions with a non empty name
			if (child.getNodeName().equals("outgoingTransitions")) {
				Node attribute = child.getAttributes().getNamedItem("specification");
				if (attribute != null) {
					String specificationAttr = attribute.getNodeValue();
					// Replace all sequences of withe spaces with just one space
					specificationAttr = specificationAttr.replaceAll("\\s+", " ");
					List<String> triggers = new ArrayList<String>();
					String trigger;
					// Triggers could be separated by commas, scan the string retrieving single
					// triggers
					while (specificationAttr.contains(",")) {
						// Add the trigger and remove the relative substring
						addTrigger(triggers, specificationAttr.substring(0, specificationAttr.indexOf(",")));
						specificationAttr = specificationAttr.substring(specificationAttr.indexOf(",") + 1);
					}
					// After the last comma is removed, there is still a trigger to manage
					Matcher matcher = Pattern.compile("\\[|/|#").matcher(specificationAttr);
					if (matcher.find())
						trigger = specificationAttr.substring(0, matcher.start());
					else
						trigger = specificationAttr;
					addTrigger(triggers, trigger);
					// Scan all triggers, adding the new one to the relative dictionaries
					for (String t : triggers) {
						// Transitions containing a dot are relative to events from a named interface
						if (t.contains(".")) {
							String interfaceName = t.substring(0, t.indexOf('.'));
							if (!interfacesNames.contains(interfaceName))
								interfacesNames.add(interfaceName);
							String eventName = t.substring(t.indexOf('.') + 1);
							if (!eventsNames.contains(eventName))
								eventsNames.add(eventName);
						} else {
							if (!eventsNames.contains(t))
								eventsNames.add(t);
						}
					}
				}
			}
		}
	}

	/**
	 * Add the string representing the trigger to the list. All spaces will be
	 * removed and triggers relative to time events or conicident to other keywords
	 * will not be added.
	 *
	 * @param triggers the list where to put the new one
	 * @param trigger  the string representing the trigger to add
	 */
	private void addTrigger(List<String> triggers, String trigger) {
		// Triggers relative to time events or conicident to other keywords are useless.
		if (trigger.contains("after ") || trigger.contains("every ")
				|| trigger.replace(" ", "").matches("always|oncycle|else|default"))
			return;
		// Remove the remainings spaces from the string
		String trg = trigger.replace(" ", "");
		if (!trg.isEmpty()) {
			triggers.add(trg);
		}
	}

	/**
	 * Gets the full name of the node recursively, going up in the DOM tree.
	 *
	 * @param node the node for which it must be obtained the full name
	 * @param name the full name obtained before the call of this method, it must
	 *             contain a dot at the start if it is not the first call
	 * @return the full name obtained at the end of the call of this method
	 */
	private String getFullName(Node node, String name) {
		// Note that non alphanumeric characters must be substitued with '_' to be
		// compliant with SCTUnit (the name must be an ID)
		String newName = node.getAttributes().getNamedItem("name").getNodeValue().replaceAll("[^a-zA-Z0-9]", "_")
				+ name;
		Node parent = node.getParentNode();
		if (parent.getNodeName().equals("sgraph:Statechart"))
			// If the "root" of the statechart is reached, the final full name is returned
			return newName;
		else
			// The name of the parent node must be added (recursively) to the full name
			return this.getFullName(parent, "." + newName);
	}

}
