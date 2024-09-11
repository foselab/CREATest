package createst.sctunit.writing;

import java.io.IOException;
import java.util.List;

import createst.junit.reading.TestCase;

public interface ISctunitWriter {

	public void writeSctunit(String sctunitPath, String statechartName, List<TestCase> testCaseList, boolean isSimplified) throws IOException;

}
