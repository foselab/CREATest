package createst.sgen.writing;

import java.io.IOException;

public interface ISgenWriter {
	
	/**
	 * Generate the .sgen file needed by Itemis Create to generate java code from a
	 * .ysc file (a statechart).
	 *
	 * @param projectName    the name of the project
	 * @param namespace 	 the namespace, null if not defined
	 * @param statechartName the name of the statechart
	 * @param sgenPath       the absolute path (with also the file) of the .sgen
	 *                       file that will be generated
	 * @param targetDir      the target directory
	 * @param targetPackage  the target package
	 * @throws IOException if any IO errors occur.
	 */
	public void writeSgen(String projectName, String namespace, String statechartName, String sgenPath, String targetDir,
			String targetPackage) throws IOException;
	
}
