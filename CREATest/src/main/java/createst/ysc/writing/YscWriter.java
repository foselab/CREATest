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
public class YscWriter implements IYscWriter {
	
	/**
	 * {@inheritDoc}
	 */
	public void changeImports(String sourceFilePath) 
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
		Pattern pattern = Pattern.compile("import\\s*:\\s*\"(.*)\"");
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
