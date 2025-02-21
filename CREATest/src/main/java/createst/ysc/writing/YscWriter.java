package createst.ysc.writing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The Class YscWriter.
 */
public abstract class YscWriter {

	/**
	 * Generates a version of the source.ysc file removing the namespace
	 * definition
	 *
	 * @param sourceFilePath the path of the starting .ysc file
	 * @return the name of the generated file (with its extension)
	 * @throws IOException if any IO errors occur.
	 */
	public static String writeWithoutNSVersion(String sourceFilePath) throws IOException {
		// Get the content of the file and remove the namespace
		String oldYscContent = new String(Files.readAllBytes(Paths.get(sourceFilePath)), StandardCharsets.UTF_8);
		String newYscContent = oldYscContent.replaceAll("namespace=\"[^\"]*\" ", "")
				.replaceAll("namespace [a-zA-z0-9_.]*", "");
		// Obtain a new name for the new file
		sourceFilePath = sourceFilePath.replace(".ysc", "_without_ns.ysc");
		// Create the new file
		File newYsc = new File(sourceFilePath);
		BufferedWriter writer = new BufferedWriter(new FileWriter(newYsc));
		writer.write(newYscContent);
		writer.close();
		newYsc.createNewFile();
		System.out.println("The statechart defines a namespace. Namespaces are not supported.\n"
				+ "A new identical statechart without the namespace will be produced.\n"
				+ "The generated SCTUnit class should be executed over this new statehcart.");
		return sourceFilePath;
	}
	
	/**
	 * Overwrite the content of a .ysc file changing the import statements
	 * as if the imported sub-machines are located in the same directory
	 *
	 * @param sourceFilePath the path of the .ysc file
	 * @throws IOException if any IO errors occur.
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created
	 *                                      which satisfies the configuration
	 *                                      requested.
	 * @throws SAXException if any parse errors occur.
	 */
	public static void changeImports(String sourceFilePath) 
			throws IOException, ParserConfigurationException, SAXException {
		String yscContent = new String(Files.readAllBytes(Paths.get(sourceFilePath)), StandardCharsets.UTF_8);
		// Get a DOM representation to ease the replacement 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(sourceFilePath));
		// Obtain the list of imported sub-machines
		String specAttribute = document.getElementsByTagName("sgraph:Statechart").item(0)
				.getAttributes().getNamedItem("specification").getNodeValue();
		// Substitute the import statements
		Pattern pattern = Pattern.compile("import:\\s*\"(.*)\"");
        Matcher matcher = pattern.matcher(specAttribute);
        // Match on the string from the DOM and replace on the initial string
        while (matcher.find()) {
        	String match = matcher.group(1);
        	String path = new File(match).getCanonicalPath();
        	yscContent = yscContent.replace(match, path.substring(path.lastIndexOf("\\")+1));
        }
        // Write the new content
        BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFilePath));
		writer.write(yscContent);
		writer.close();
	}

}
