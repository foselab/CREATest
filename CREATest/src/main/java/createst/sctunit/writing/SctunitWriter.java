package createst.sctunit.writing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import createst.junit.reading.TestCase;

/**
 * The Class SctunituWriter.
 */
public class SctunitWriter implements ISctunitWriter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeSctunit(String sctunitPath, String statechartName, List<TestCase> testCaseList, boolean isSimplified)
			throws IOException {
		// Print to video the SCTUnit file
		STGroupFile group = new STGroupFile("sctunit_template.stg");
		ST st = group.getInstanceOf("test_class");
		if (isSimplified)
			st.add("isSimplified", "");
		st.add("statechart_name", statechartName);
		st.add("test_suite", testCaseList);
		File genFile = new File(sctunitPath);
		st.write(genFile, null);
	}

}
