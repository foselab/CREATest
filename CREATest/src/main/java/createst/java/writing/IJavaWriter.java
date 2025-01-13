package createst.java.writing;

import java.io.IOException;

public interface IJavaWriter {

	/**
	 * Generates a simplified version of the java class, changing names and
	 * visibilities
	 *
	 * @param javaPath           the path of the starting .java file
	 * @param simplifiedJavaPath the path where the .java file containing the
	 *                           simplified version is put
	 * @throws IOException if any IO errors occur.
	 */
	public void writeSimplifiedVersion(String javaPath, String simplifiedJavaPath) throws IOException;

	/**
	 * Calls the itemis CREATE code generator.
	 *
	 * @param projectPath    the path of the project
	 * @param itemisScc      the path of the scc.bat file
	 * @param sourceDir      the path of the source directory
	 * @param sourceFile     the name of the source file
	 * @param statechartName the name of the statechart
	 * @throws IOException if any IO errors occur.
	 */
	public void callICGenerator(String projectPath, String itemisScc, String sourceDir, String sourceFile,
			String statechartName) throws IOException;

}
