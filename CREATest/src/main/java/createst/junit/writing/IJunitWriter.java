package createst.junit.writing;

public interface IJunitWriter {
	
	public void callEvosuite(String evoClass, String evoProjectCP, String DtestDir, String DreportDir,
			boolean hasSearchBudget, int searchBudget);
	
}
