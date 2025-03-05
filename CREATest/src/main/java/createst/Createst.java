package createst;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import createst.cli.CliManager;
import createst.cli.ICliManager;
import createst.cli.InputValues;
import createst.java.reading.IJavaReader;
import createst.java.reading.JavaReader;
import createst.java.reading.ProceedTime;
import createst.java.writing.IJavaWriter;
import createst.java.writing.JavaWriter;
import createst.junit.reading.IJunitReader;
import createst.junit.reading.JunitReader;
import createst.junit.reading.TestCase;
import createst.junit.writing.IJunitWriter;
import createst.junit.writing.JunitWriter;
import createst.sctunit.writing.ISctunitWriter;
import createst.sctunit.writing.SctunitWriter;
import createst.sgen.writing.ISgenWriter;
import createst.sgen.writing.SgenWriter;
import createst.ysc.reading.IYscReader;
import createst.ysc.reading.YscReader;

/**
 * The Class Ysc2Sctunit, contains the main method.
 */
public class Createst {

	/**
	 * The main method, generate a .sctunit file (test suite for a statechart)
	 * starting from a .ysc file (a statechart).
	 *
	 * @param args the command line arguments.
	 * @throws ParserConfigurationException if a DocumentBuildercannot while
	 *                                      instantiating a Statechart object.
	 * @throws SAXException                 if there are any problems encountered
	 *                                      while parsing the xml file representing
	 *                                      the statechart.
	 * @throws IOException                  if any IO errors occur.
	 * @throws URISyntaxException           if the execution location can not be
	 *                                      parsed as a uri
	 */
	public static void main(String[] args)
			throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
		System.out.println("--------------------------------------------------------------");
		System.out.println("\t\t\tCREATest");
		System.out.println("--------------------------------------------------------------");
		// Parse the arguments
		ICliManager cli = new CliManager();
		InputValues input = cli.parse(args);
		if (input == null) {
			System.out.println("--------------------------------------------------------------");
			return;
		}
		String itemisScc = input.getSccPath();
		String yscPath = input.getYscPath();
		boolean hasSearchBudget = input.hasSearchBudget();
		int evoSearchBudget = input.getEvoSearchBudget();
		boolean hasGenArtifacts = input.hasGenArtifacts();

		// Initialize the temporary Eclipse Project
		System.out.println("*******************************************");
		System.out.println("Initializing temporary Eclipse project...");
		System.out.println("*******************************************");
		String workspacePath = TempWsUtils.initProject(yscPath);

		// Obtain the needed Strings
		String sourceFile = yscPath.substring(yscPath.lastIndexOf(File.separator) + 1);
		String projectPath = workspacePath + File.separator + TempWsUtils.PROJECT_NAME;
		String sourceFilePath = projectPath + File.separator + TempWsUtils.MODELS_DIR + File.separator + sourceFile;

		// Read the statechart
		System.out.println("*******************************************");
		System.out.println("Reading .ysc file...");
		System.out.println("*******************************************");
		IYscReader yscReader = new YscReader(sourceFilePath);
		String namespace = yscReader.getNamespace();
		String statechartName = yscReader.getStatechartName();
		Map<String, String> statesNames = yscReader.getStatesNames();
		Map<String, String> eventsNames = yscReader.getEventsNames();
		Map<String, String> interfacesNames = yscReader.getInterfacesNames();
		Map<String, String> operationsNames = yscReader.getOperationsNames();
		List<String> importedSubMachines = yscReader.getImportedSubMachines();
		
		// Initialize the temporary Eclipse Project
		if (!importedSubMachines.isEmpty()) {
			System.out.println("*******************************************");
			System.out.println("Adding imported sub-machines to the project...");
			System.out.println("*******************************************");
			TempWsUtils.addSubmachines(workspacePath, yscPath, importedSubMachines);
		}

		// Obtain the needed Strings
		String firstUpperStatechartName = statechartName.substring(0, 1).toUpperCase() + statechartName.substring(1);

		String sgenPath = projectPath + File.separator + TempWsUtils.MODELS_DIR + File.separator + firstUpperStatechartName + ".sgen";

		String actualPackage = TempWsUtils.BASE_PACKAGE + (yscReader.hasNamespace()? File.separator + namespace : "");
		
		String javaPath = projectPath + File.separator + TempWsUtils.SOURCE_DIR + File.separator + actualPackage + File.separator + firstUpperStatechartName + ".java";
		String simplifiedJavaPath = projectPath + File.separator + TempWsUtils.SOURCE_DIR + File.separator + actualPackage + File.separator + firstUpperStatechartName
				+ "Simplified.java";

		String compilerD = "-d " + projectPath + File.separator + TempWsUtils.BINARY_DIR;
		String compilerClasspath = "-classpath " + projectPath + File.separator + TempWsUtils.SOURCE_DIR;

		String evoSimplifiedClass = "-class " + actualPackage.replace(File.separator, ".") + "." + firstUpperStatechartName + "Simplified";
		String evoProjectCP = "-projectCP " + projectPath + File.separator + TempWsUtils.BINARY_DIR;
		String evoDTestDir = "-Dtest_dir=" + projectPath + File.separator + TempWsUtils.TEST_DIR;
		String evoDReportDir = "-Dreport_dir=" + projectPath + File.separator + "evosuite-report";

		String simplifiedJunitPath = projectPath + File.separator + TempWsUtils.TEST_DIR + File.separator + actualPackage + File.separator + firstUpperStatechartName
				+ "Simplified_ESTest.java";
		String sctunitPath = projectPath + File.separator + TempWsUtils.MODELS_DIR + File.separator + firstUpperStatechartName + "Test.sctunit";

		// Generate the .sgen file needed by itemis CREATE to generate the java code
		System.out.println("*******************************************");
		System.out.println("Generating .sgen file...");
		System.out.println("*******************************************");
		ISgenWriter sgenWriter = new SgenWriter();
		sgenWriter.writeSgen(TempWsUtils.PROJECT_NAME, namespace, statechartName, sgenPath, TempWsUtils.SOURCE_DIR, TempWsUtils.BASE_PACKAGE);

		// Call the itemis CREATE generators
		System.out.println("*******************************************");
		System.out.println("Calling Itemis Create code generator...");
		System.out.println("*******************************************");
		IJavaWriter javaWriter = new JavaWriter();
		javaWriter.callICGenerator(projectPath, itemisScc, TempWsUtils.MODELS_DIR);

		// Read the java file
		System.out.println("*******************************************");
		System.out.println("Reading .java file...");
		System.out.println("*******************************************");
		IJavaReader javaReader = new JavaReader();
		Map<Integer, ProceedTime> proceedTimes = javaReader.getProceedTimes(javaPath);

		// Compile the generated classes
		System.out.println("*******************************************");
		System.out.println("Compiling...");
		System.out.println("*******************************************");
		TempWsUtils.compile(compilerD, compilerClasspath, javaPath);

		// Modify the generated Java code to create a simplified version
		System.out.println("*******************************************");
		System.out.println("Generating simplified java class...");
		System.out.println("*******************************************");
		javaWriter.writeSimplifiedVersion(javaPath, simplifiedJavaPath);

		// Compile the new simplified class
		System.out.println("*******************************************");
		System.out.println("Compiling...");
		System.out.println("*******************************************");
		TempWsUtils.compile(compilerD, compilerClasspath, simplifiedJavaPath);

		// Delete the VirtualTimer.class file to hide it to Evosuite
		String virtualTimerPath = projectPath + File.separator + TempWsUtils.BINARY_DIR + File.separator + "com" + File.separator + "yakindu" + File.separator + "core" + File.separator + "VirtualTimer.class";
		if (Files.exists(Paths.get(virtualTimerPath)))
			new File(virtualTimerPath).delete();

		// Call the Evosuite test generator
		System.out.println("*******************************************");
		System.out.println("Calling Evosuite test generator...");
		System.out.println("*******************************************");
		IJunitWriter junitWriter = new JunitWriter();
		junitWriter.callEvosuite(evoSimplifiedClass, evoProjectCP, evoDTestDir, evoDReportDir, hasSearchBudget,
				evoSearchBudget);

		// Read the junit file
		System.out.println("*******************************************");
		System.out.println("Reading .junit file...");
		System.out.println("*******************************************");
		IJunitReader junitReader = new JunitReader();
		List<TestCase> testCaseList = junitReader.getTestCases(simplifiedJunitPath, statechartName, statesNames,
				eventsNames, interfacesNames, operationsNames, proceedTimes);

		// Generate the .sctunit file
		System.out.println("*******************************************");
		System.out.println("Generating .sctunit file...");
		System.out.println("*******************************************");
		ISctunitWriter sctunitWriter = new SctunitWriter();
		sctunitWriter.writeSctunit(sctunitPath, namespace, statechartName, testCaseList, true);

		// Copy the final .sctunit file and eventually generate the .zip of the
		// temporary workspace
		System.out.println("*******************************************");
		System.out.println("Copying .sctunit file...");
		System.out.println("*******************************************");
		TempWsUtils.copyAndCompress(workspacePath, sctunitPath, sourceFilePath, statechartName, hasGenArtifacts);

		// End the execution
		System.out.println("*******************************************");
		System.out.println("Finished.");
		System.out.println("*******************************************");
		System.out.println("--------------------------------------------------------------");
		System.exit(0);
	}

}
