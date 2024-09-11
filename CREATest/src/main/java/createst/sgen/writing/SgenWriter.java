package createst.sgen.writing;

import java.io.File;
import java.io.IOException;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

/**
 * The Class SgenWriter.
 */
public class SgenWriter implements ISgenWriter {

	/**
	 * Generate the .sgen file needed by Itemis Create to generate java code from a
	 * .ysc file (a statechart).
	 *
	 * @param projectName    the name of the project
	 * @param statechartName the name of the statechart
	 * @param sgenPath       the absolute path (with also the file) of the .sgen
	 *                       file that will be generated
	 * @param targetDir      the target directory
	 * @param targetPackage  the target package
	 * @throws IOException if any IO errors occur.
	 */
	@Override
	public void writeSgen(String projectName, String statechartName, String sgenPath, String targetDir,
			String targetPackage) throws IOException {
		STGroupFile group = new STGroupFile("sgen_template.stg");
		ST st = group.getInstanceOf("generator");
		// Put the generated file in projectPath\targetDir\targetPackage
		st.add("project_name", projectName);
		// For the directory, the.sgen file expect "\\" as separator instaed of "\"
		st.add("directory", targetDir.replace("\\", "\\\\"));
		st.add("package_name", targetPackage);
		st.add("statechart_name", statechartName);
		// Create the file in the same directory of the statechart
		File genFile = new File(sgenPath);
		st.write(genFile, null);
	}
}
