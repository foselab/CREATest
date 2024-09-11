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
	 * Generates a version of the source file (.ysc or .sct) removing the namespace
	 * definition
	 *
	 * @param sourceFilePath the path of the starting .ysc or .sct file
	 * @return the name of the generated file (with its extension)
	 * @throws IOException if any IO errors occur.
	 */
	public static String writeWithoutNSVersion(String sourceFilePath) throws IOException {
		// Get the file name
		String sourceFile = sourceFilePath.substring(sourceFilePath.lastIndexOf("\\"));
		// Get the content of the file and remove the namespace
		String oldYscContent = new String(Files.readAllBytes(Paths.get(sourceFilePath)), StandardCharsets.UTF_8);
		String newYscContent = oldYscContent.replaceAll("namespace=\"[^\"]*\" ", "")
				.replaceAll("namespace [a-zA-z0-9_.]*", "");
		// Obtain a new unused name for the new file
		String extension = sourceFilePath.endsWith(".ysc") ? ".ysc" : ".sct";
		sourceFilePath = sourceFilePath.replace(extension, "");
		String suffix = "_without_ns";
		while (Files.exists(Paths.get(sourceFilePath + suffix + extension))) {
			suffix = suffix.concat("_");
		}
		sourceFilePath = sourceFilePath + suffix + extension;
		// Create the new file
		File newYsc = new File(sourceFilePath);
		BufferedWriter writer = new BufferedWriter(new FileWriter(newYsc));
		writer.write(newYscContent);
		writer.close();
		newYsc.createNewFile();
		System.out.println("A namespace has been defined in the statechart. Namespaces are not supported.");
		System.out.println("A new identical statechart without the namespace definition has been created:");
		System.out.println("\'" + sourceFilePath + "\' ");
		System.out.println("It will be used as the source file of the execution.");
		return sourceFile.replace(extension, "") + suffix + extension;
	}

}
