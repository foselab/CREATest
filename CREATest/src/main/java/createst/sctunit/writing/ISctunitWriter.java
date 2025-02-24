package createst.sctunit.writing;

import java.io.IOException;
import java.util.List;

import createst.junit.reading.TestCase;

public interface ISctunitWriter {

	/**
	 * Generate a .sctunit file for a statechart
	 *
	 * @param sctunitPath    the path were the .sctunit file will be put
	 * @param namespace		 the namespace, null if not defined
	 * @param statechartName the names of the statechart
	 * @param testCaseList   the list of the test cases to write in the sctunit file
	 * @param isSimplified	 true to add 'Simplified' to the name of the test class
	 * @throws IOException if any IO errors occur.
	 */
	public void writeSctunit(String sctunitPath, String namespace, String statechartName, List<TestCase> testCaseList, boolean isSimplified) throws IOException;

}
