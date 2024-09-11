package createst.cli;

/**
 * The Class containing the parsed and adapted arguments from the CLI.
 */
public class InputValues {

	/** The absolute path of the scc script. */
	private String sccPath;

	/** The project name. */
	private String projectName;

	/** The workspace path. */
	private String workspacePath;

	/** The source directory (relative path). */
	private String sourceDir;

	/** The name of the source file */
	private String sourceFile;

	/** The target package */
	private String targetPackage;

	/** The target directory (relative path). */
	private String targetDir;

	/**
	 * The directory containing the binaries of the generated java classes (relative
	 * path).
	 */
	private String binaryDir;

	/** The directory in wich Evosuite will place JUnit tests (relative path). */
	private String evoTestDir;

	/** The (evosuite) search budget. */
	private int evoSearchBudget = 0;

	/** True if the parsed arguments have the option -searchBudget. */
	private boolean hasSearchBudget = false;

	/**
	 * Gets the absolute path of the scc script.
	 *
	 * @return the absolute path of the scc script
	 */
	public String getSccPath() {
		return sccPath;
	}

	/**
	 * Gets the project name.
	 *
	 * @return the project name
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Gets the workspace path.
	 *
	 * @return the workspace path
	 */
	public String getWorkspacePath() {
		return workspacePath;
	}

	/**
	 * Gets the source directory.
	 *
	 * @return the source directory
	 */
	public String getSourceDir() {
		return sourceDir;
	}

	/**
	 * Gets the source file name.
	 *
	 * @return the source file name
	 */
	public String getSourceFile() {
		return sourceFile;
	}

	/**
	 * Gets the target package.
	 *
	 * @return the target package
	 */
	public String getTargetPackage() {
		return targetPackage;
	}

	/**
	 * Gets the target directory.
	 *
	 * @return the target directory
	 */
	public String getTargetDir() {
		return targetDir;
	}

	/**
	 * Gets the directory containing the binaries.
	 *
	 * @return the directory containing the binaries
	 */
	public String getBinaryDir() {
		return binaryDir;
	}

	/**
	 * Gets the directory in wich Evosuite will place JUnit tests.
	 *
	 * @return the directory in wich Evosuite will place JUnit tests
	 */
	public String getEvoTestDir() {
		return evoTestDir;
	}

	/**
	 * Gets the (evosuite) search budget, 0 if not present.
	 *
	 * @return the (evosuite) search budget
	 */
	public int getEvoSearchBudget() {
		return evoSearchBudget;
	}

	/**
	 * Checks for the option -searchBudget.
	 *
	 * @return true if the parsed arguments have the option -searchBudget, false
	 *         otherwise
	 */
	public boolean hasSearchBudget() {
		return hasSearchBudget;
	}

	/**
	 * Sets the absolute path of the scc script.
	 *
	 * @param sccPath the absolute path of the scc script
	 */
	public void setSccPath(String sccPath) {
		this.sccPath = sccPath;
	}

	/**
	 * Sets the project name.
	 *
	 * @param projectName the project name
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * Sets the workspace path.
	 *
	 * @param workspacePath the workspace path
	 */
	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	/**
	 * Sets the source dir.
	 *
	 * @param sourceDir the source dir
	 */
	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	/**
	 * Sets the source file name.
	 *
	 * @param sourceFile the source file name
	 */
	public void setSourceFile(String sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * Sets the target package.
	 *
	 * @param targetPackage the target package
	 */
	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	/**
	 * Sets the target dir.
	 *
	 * @param targetDir the target dir
	 */
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}

	/**
	 * Sets the directory containing the binaries.
	 *
	 * @param targetDir the directory containing the binaries
	 */
	public void setBinaryDir(String binaryDir) {
		this.binaryDir = binaryDir;
	}

	/**
	 * Sets the directory in wich Evosuite will place JUnit tests.
	 *
	 * @param targetDir the directory in wich Evosuite will place JUnit tests
	 */
	public void setEvoTestDir(String evoTestDir) {
		this.evoTestDir = evoTestDir;
	}

	/**
	 * Sets the (evosuite) search budget.
	 *
	 * @param searchBudget the (evosuite) search budget
	 */
	public void setEvoSearchBudget(int evoSearchBudget) {
		this.hasSearchBudget = true;
		this.evoSearchBudget = evoSearchBudget;
	}

}
