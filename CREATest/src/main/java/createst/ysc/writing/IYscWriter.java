package createst.ysc.writing;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public interface IYscWriter {
	
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
	public void changeImports(String sourceFilePath) 
			throws IOException, ParserConfigurationException, SAXException;
}
