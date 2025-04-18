package createst.java.writing;

import java.io.IOException;

public interface IJavaWriter {

	/**
	 * Overwrites the input .java file substituting all replacement characters
	 * \ufffd with the string "__CREATEST_UNRECOGNIZED_CHARACTER__".
	 *
	 * @param javaPath the path of the .java file
	 * @throws IOException if any IO errors occur.
	 */
	public void overwriteReplacementCharacter(String javaPath) throws IOException;

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
	 * @param projectPath the path of the project
	 * @param itemisScc   the path of the scc.bat file
	 * @param sourceDir   the relative path (with respect to the projectPatg) of the
	 *                    source directory
	 * @throws IOException if any IO errors occur.
	 */
	public void callICGenerator(String projectPath, String itemisScc, String sourceDir) throws IOException;

}
