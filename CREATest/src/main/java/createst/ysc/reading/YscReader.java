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

	/** True if the statechart has a namespace, false otherwise. */
	private boolean hasNamespace;

	/** The statechart namespace. */
	private String namespace;

	/** The statechart name. */
	private String statechartName;

	/** A list with the relative path of the imported sub-machines */
	private List<String> importedSubMachines;

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
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNamespace() {
		return this.hasNamespace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNamespace() {
		return this.namespace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStatechartName() {
		return this.statechartName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getImportedSubMachines() {
		return this.importedSubMachines;
	}

	/**
	 * {@inheritDoc}
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
	 * {@inheritDoc}
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
	 * {@inheritDoc}
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
		this.namespace = this.hasNamespace
				? this.statechartNode.getAttributes().getNamedItem("namespace").getNodeValue()
				: null;

		// Obtain the name of the statechart
		this.statechartName = this.statechartNode.getAttributes().getNamedItem("name").getNodeValue();

		// Obtain the string representing the specification attribute
		String specAttribute = this.statechartNode.getAttributes().getNamedItem("specification").getNodeValue();

		// Obtain the list of imported sub-machines
		this.importedSubMachines = new ArrayList<>();
		Pattern importPattern = Pattern.compile("import\\s*:\\s*\"(.*)\"");
		Matcher importMatcher = importPattern.matcher(specAttribute);
		while (importMatcher.find()) {
			this.importedSubMachines.add(importMatcher.group(1));
		}

		// Obtain the list of events full names (i.e. with their interface, if any)
		this.interfacesNames = new ArrayList<String>();
		this.eventsNames = new ArrayList<String>();
		String idRegex = "[a-zA-Z0-9_-]*"; // also empty string
		Pattern interfacePattern = Pattern.compile(
				"interface\\s*(" + idRegex + "):\\s*(.*?)(?=(?:interface|import|internal)\\b|$)", Pattern.DOTALL);
		Matcher interfaceMatcher = interfacePattern.matcher(specAttribute);
		while (interfaceMatcher.find()) {
			String interfaceName = interfaceMatcher.group(1);
			if (!interfaceName.isEmpty())
				interfacesNames.add(interfaceName);
			String interfaceScope = interfaceMatcher.group(2);
			Pattern inEventPattern = Pattern.compile("in event\\s*(" + idRegex + ")");
			Matcher inEventMathcer = inEventPattern.matcher(interfaceScope);
			while (inEventMathcer.find()) {
				String eventName = inEventMathcer.group(1);
				eventsNames.add(eventName);
			}
		}

		// Search the nodes representing the starting regions of the statechart
		this.statesNames = new ArrayList<String>();
		NodeList nodeList = this.statechartNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals("regions")) {
				// Start the visit of the subtree from the node representing the region
				this.visitNode(child, this.statesNames);
			}
		}
	}

	/**
	 * Visit a node, recursively visit all its element child nodes.
	 *
	 * @param node            the node to visit
	 * @param statesNames     the list of all the names of the states visited so far
	 */
	private void visitNode(Node node, List<String> statesNames) {
		// If the node is a region, it may contain a final state
		// else, it is a vertex and it may have outgoing transitions
		if (node.getNodeName().equals("regions")) {
			this.checkForFinalState(node, statesNames);
		} else {
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
				visitNode(child, statesNames);
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
