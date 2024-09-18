package createst.ysc.writing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The Class YscWriter.
 */
public abstract class YscWriter {

	/**
	 * Generates a version of the source file .ysc removing the namespace
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

}
