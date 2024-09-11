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
			boolean hasSearchBudget, int searchBudget) {
		// Set necessary arguments
		List<String> evoArgs = new ArrayList<>();
		evoArgs.addAll(Arrays.asList(evoClass.split(" ")));
		evoArgs.addAll(Arrays.asList(evoProjectCP.split(" ")));
		evoArgs.add(DtestDir);
		evoArgs.add(DreportDir);
		// Default setting, use whole suite generation
		evoArgs.add("-generateSuite");
		// Allow minimization task to run without limitations for at most 10min
		evoArgs.add("-Dassertion_minimization_fallback_time=1.0");
		evoArgs.add("-Dminimization_timeout=600");
		// Impose a search budget
		if (hasSearchBudget)
			evoArgs.add("-Dsearch_budget=" + searchBudget);
		// This option is necessary for the jar to execute without warnings, it should
		// never trow the URISyntaxException
		try {
			String jarRunningDir = new File(
					JunitWriter.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()
					.getPath();
			String evosuiteCP = "-evosuiteCP " + jarRunningDir + "\\libs\\evosuite-1.0.6.jar";
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
