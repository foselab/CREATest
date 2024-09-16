package createst.cli;

/**
 * The Class containing the parsed and adapted arguments from the CLI.
 */
public class InputValues {

	/** The absolute path of the scc script. */
	private String sccPath;

	/** The absolute path of the ysc file. */
	private String yscPath;

	/** The (evosuite) search budget. */
	private int evoSearchBudget = 0;

	/** True if the parsed arguments have the option -b. */
	private boolean hasSearchBudget = false;
	
	/** True if the parsed arguments have the option -g. */
	private boolean hasGenArtifacts = false;

	/**
	 * Gets the absolute path of the scc script.
	 *
	 * @return the absolute path of the scc script
	 */
	public String getSccPath() {
		return sccPath;
	}

	/**
	 * Gets the absolute path of the ysc file.
	 *
	 * @return the absolute path of the ysc file
	 */
	public String getYscPath() {
		return yscPath;
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
	 * Checks for the option -b.
	 *
	 * @return true if the parsed arguments have the option -b, false
	 *         otherwise
	 */
	public boolean hasSearchBudget() {
		return hasSearchBudget;
	}
	
	/**
	 * Checks for the option -g.
	 *
	 * @return true if the parsed arguments have the option -g, false
	 *         otherwise
	 */
	public boolean hasGenArtifacts() {
		return hasGenArtifacts;
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
	 * Sets the absolute path of the ysc file.
	 *
	 * @param yscPath the absolute path of the ysc file
	 */
	public void setYscPath(String yscPath) {
		this.yscPath = yscPath;
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
	
	/**
	 * Sets the (evosuite) search budget.
	 *
	 * @param searchBudget the (evosuite) search budget
	 */
	public void setGenArtifacts() {
		this.hasGenArtifacts = true;
	}

}
