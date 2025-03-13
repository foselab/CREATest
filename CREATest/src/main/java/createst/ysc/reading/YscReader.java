package createst.ysc.reading;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

	/** The list containing all the names of the operations in the statechart. */
	private List<String> operationsNames;

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
			String enumName = name.toUpperCase().replace('.', '_').replace("^", "");
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
			eventsNames.put(methodName, addCircumflex(name));
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
			interfacesNames.put(className, addCircumflex(name));
		}
		return interfacesNames;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getOperationsNames() {
		// Create a dictionary for the operations names with the method name
		Map<String, String> operationsNames = new HashMap<String, String>();
		for (String name : this.operationsNames) {
			String methodName = name.substring(0, 1).toLowerCase() + name.substring(1);
			operationsNames.put(addID(methodName), addCircumflex(name));
		}
		return operationsNames;
	}

	public static final Set<String> SCTUNIT_KEYWORDS = Set.of("testclass", "for", "statechart", "operation", "return",
			"assert", "message", "called", "times", "proceed", "s", "ms", "us", "ns", "cycle", "if", "else", "while",
			"mock", "returns", "testsuite");

	public static final Set<String> JAVA_KEYWORDS = Set.of("abstract", "continue", "for", "new", "switch", "assert",
			"default", "goto", "package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double",
			"implements", "protected", "throw", "byte", "else", "import", "public", "throws", "case", "enum",
			"instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char", "final",
			"interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float",
			"native", "super", "while");

	/**
	 * If the input string is a SCTUnit keyword, add the circumflex '^' as suffix
	 * 
	 * @param input the input string
	 * @return the input string with the circumflex '^' as suffix if the input
	 *         string is a SCTUnit keyword
	 */
	private String addCircumflex(String input) {
		return (SCTUNIT_KEYWORDS.contains(input) ? "^" : "") + input;
	}

	/**
	 * If the input string is a Java keyword, add "_ID" as prefix
	 * 
	 * @param input the input string
	 * @return the input string with "_ID" as prefix if the input string is a Java
	 *         keyword
	 */
	private String addID(String input) {
		return input + (JAVA_KEYWORDS.contains(input) ? "_ID" : "");
	}

	/**
	 * Inits the statechart obtaining statechart name, states names end events
	 * names.
	 */
	private void initStatechart() {
		// Check if the statechart has a namespace
		this.hasNamespace = this.statechartNode.getAttributes().getNamedItem("namespace") != null;
		// Retrieve the namespace and add circumflex where needed
		if (this.hasNamespace) {
			String namespace = this.statechartNode.getAttributes().getNamedItem("namespace").getNodeValue();
			String[] subNamespaces = namespace.split("\\.");
			for (int i = 0; i < subNamespaces.length; i++)
				subNamespaces[i] = addCircumflex(subNamespaces[i]);
			this.namespace = String.join(".", subNamespaces);
		} else {
			this.namespace = null;
		}

		// Obtain the name of the statechart
		this.statechartName = addCircumflex(this.statechartNode.getAttributes().getNamedItem("name").getNodeValue());

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
		this.operationsNames = new ArrayList<String>();
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
			Pattern operationPattern = Pattern.compile("operation\\s*(" + idRegex + ")\\(");
			Matcher operationMathcer = operationPattern.matcher(interfaceScope);
			while (operationMathcer.find()) {
				String operationsName = operationMathcer.group(1);
				operationsNames.add(operationsName);
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
	 * @param node        the node to visit
	 * @param statesNames the list of all the names of the states visited so far
	 */
	private void visitNode(Node node, List<String> statesNames) {
		// If the node is a region, it may contain a final state
		// else, it is a vertex and it may have outgoing transitions
		if (node.getNodeName().equals("regions")) {
			this.checkForFinalState(node, statesNames);
		} else {
			// A "normal" state is a node with name "vertices" and the attribute "xsi:type"
			// equals to "sgraph:State", for that kind of node, the name is of interest
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
	 * Check if the node (region) contains a final state.
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
	 * @param node    the node for which it must be obtained the full name
	 * @param oldName the full name obtained before the call of this method, it must
	 *                contain a dot at the start if it is not the first call
	 * @return the full name obtained at the end of the call of this method
	 */
	private String getFullName(Node node, String oldName) {
		String name;
		// If the node is a region without or with empty name, check its position as
		// child node to determine its name
		if (node.getNodeName().equals("regions") && (node.getAttributes().getNamedItem("name") == null
				|| node.getAttributes().getNamedItem("name").getNodeValue().isEmpty())) {
			Node parent = node.getParentNode();
			int regionPosition = 0;
			for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
				Node child = parent.getChildNodes().item(i);
				if (child.getNodeName().equals("regions")) {
					if (child == node) // == because it must be the same instance
						break;
					else
						regionPosition++;
				}
			}
			name = "_region" + regionPosition;
		} else {
			name = node.getAttributes().getNamedItem("name").getNodeValue();
		}
		// Note that non alphanumeric characters must be substituted with '_' to be
		// compliant with SCTUnit (the name must be an ID), except for '^' that must be
		// kept
		String newName = addCircumflex(name.replaceAll("[^a-zA-Z0-9\\^]", "_")) + oldName;
		Node parent = node.getParentNode();
		if (parent.getNodeName().equals("sgraph:Statechart"))
			// If the "root" of the statechart is reached, the final full name is returned
			return newName;
		else
			// The name of the parent node must be added (recursively) to the full name
			return this.getFullName(parent, "." + newName);
	}

}
