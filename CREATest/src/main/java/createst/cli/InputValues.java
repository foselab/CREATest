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

	/** True if the parsed arguments have the option -e. */
	private boolean hasRunExperiments = false;

	/**
	 * Get the absolute path of the scc script.
	 *
	 * @return the absolute path of the scc script
	 */
	public String getSccPath() {
		return sccPath;
	}

	/**
	 * Get the absolute path of the ysc file.
	 *
	 * @return the absolute path of the ysc file
	 */
	public String getYscPath() {
		return yscPath;
	}

	/**
	 * Get the (evosuite) search budget, 0 if not present.
	 *
	 * @return the (evosuite) search budget
	 */
	public int getEvoSearchBudget() {
		return evoSearchBudget;
	}

	/**
	 * Check for the option -b.
	 *
	 * @return true if the parsed arguments have the option -b, false
	 *         otherwise
	 */
	public boolean hasSearchBudget() {
		return hasSearchBudget;
	}
	
	/**
	 * Check for the option -g.
	 *
	 * @return true if the parsed arguments have the option -g, false
	 *         otherwise
	 */
	public boolean hasGenArtifacts() {
		return hasGenArtifacts;
	}
	
	/**
	 * Check for the option -e.
	 *
	 * @return true if the parsed arguments have the option -e, false
	 *         otherwise
	 */
	public boolean hasRunExperiments() {
		return hasRunExperiments;
	}

	/**
	 * Set the absolute path of the scc script.
	 *
	 * @param sccPath the absolute path of the scc script
	 */
	public void setSccPath(String sccPath) {
		this.sccPath = sccPath;
	}
	
	/**
	 * Set the absolute path of the ysc file.
	 *
	 * @param yscPath the absolute path of the ysc file
	 */
	public void setYscPath(String yscPath) {
		this.yscPath = yscPath;
	}

	/**
	 * Set the (evosuite) search budget.
	 *
	 * @param searchBudget the (evosuite) search budget
	 */
	public void setEvoSearchBudget(int evoSearchBudget) {
		this.hasSearchBudget = true;
		this.evoSearchBudget = evoSearchBudget;
	}
	
	/**
	 * Set the artifacts generation.
	 *
	 */
	public void setGenArtifacts() {
		this.hasGenArtifacts = true;
	}

	/**
	 * Set has run experiments to true.
	 * 
	 */
	public void setRunExperiments() {
		this.hasRunExperiments  = true;
		
	}

}
