package createst.junit.writing;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class JunitWriter.
 */
public class JunitWriter implements IJunitWriter {

	/**
	 * {@inheritDoc}
	 */
	public void callEvosuite(String evoClass, String evoProjectCP, String DtestDir, String DreportDir,
			boolean hasSearchBudget, int searchBudget) {
		// Set necessary arguments
		List<String> evoArgs = new ArrayList<>();
		evoArgs.addAll(Arrays.asList(evoClass.split(" ")));
		evoArgs.addAll(Arrays.asList(evoProjectCP.split(" ")));
		evoArgs.add(DtestDir);
		evoArgs.add(DreportDir);
		// Default setting, use DynaMOSA algorithm
		evoArgs.add("-generateMOSuite");
		// Allow minimization task to run without limitations for at most 10min
		//evoArgs.add("-Dassertion_minimization_fallback_time=1.0");
		//evoArgs.add("-Dminimization_timeout=600");
		// Impose which coverage criterion to use
		evoArgs.add("-criterion"); evoArgs.add("BRANCH:METHODNOEXCEPTION");
		// Impose a search budget
		if (hasSearchBudget)
			evoArgs.add("-Dsearch_budget=" + searchBudget);
		// This option is necessary for the jar to execute without warnings, it should
		// never throw the URISyntaxException
		try {
			String jarRunningDir = new File(
					JunitWriter.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
					.getPath();
			String evosuiteCP = "-evosuiteCP " + jarRunningDir + File.separator + "libs" + File.separator + "evosuite-1.2.0.jar";
			evoArgs.addAll(Arrays.asList(evosuiteCP.split(" ")));
		} catch (URISyntaxException e) {
			System.out.println("An unexpected error occurred: some warnings may be generated,"
					+ " but the process should finish succesfully.");
		}
		// Run Evosuite
		org.evosuite.EvoSuite evosuite = new org.evosuite.EvoSuite();
		evosuite.parseCommandLine(evoArgs.toArray(new String[evoArgs.size()]));
	}
}
