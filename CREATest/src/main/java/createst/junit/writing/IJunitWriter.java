package createst.junit.writing;

public interface IJunitWriter {
	
	/**
	 * Call Evosuite to generate a .junit file.
	 *
	 * @param evoClass        the string "-class ClassName", where ClassName is the
	 *                        fully qualified class name
	 * @param evoProjectCP    the string "-projectCP ClassPath", where ClassPath is
	 *                        the class path for the test generation
	 * @param DtestDir        the string "-Dtest_dir=Directory", where Directory is
	 *                        the directory in which tests will be placed
	 * @param DreportDir      the string "-Dreport_dir=Directory", where Directory is
	 *                        the directory in which statistics will be placed
	 * @param hasSearchBudget true if a search budget must be imposed, false
	 *                        otherwise
	 * @param searchBudget    the search budget, not setted if hasSearchBudget is
	 *                        false
	 */
	public void callEvosuite(String evoClass, String evoProjectCP, String DtestDir, String DreportDir,
			boolean hasSearchBudget, int searchBudget);
	
}
