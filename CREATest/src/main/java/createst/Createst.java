package createst;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.zeroturnaround.zip.ZipUtil;

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
import createst.ysc.writing.YscWriter;

/**
 * The Class Ysc2Sctunit, contains the main method.
 */
public class Createst {

	// Note: if any of this strings is modified, edit
	// src/main/resources/classpath.txt and src/main/resources/project.txt
	// accordingly
	private static final String WORKSPACE_NAME = "ws_";
	private static final String PROJECT_NAME = "Project";
	private static final String SOURCE_DIR = "src";
	private static final String PACKAGE = "statechart";
	private static final String BINARY_DIR = "bin";
	private static final String TEST_DIR = "test";
	private static final String MODELS_DIR = "models";

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
		String workspacePath = initProject(yscPath);

		// Obtain the needed Strings
		String sourceFile = yscPath.substring(yscPath.lastIndexOf('\\') + 1);
		String projectPath = workspacePath + "\\" + PROJECT_NAME;
		String sourceFilePath = projectPath + "\\" + MODELS_DIR + "\\" + sourceFile;

		// Read the statechart
		System.out.println("*******************************************");
		System.out.println("Reading .ysc file...");
		System.out.println("*******************************************");
		IYscReader yscReader = new YscReader(sourceFilePath);
		String statechartName = yscReader.getStatechartName();
		Map<String, String> statesNames = yscReader.getStatesNames();
		Map<String, String> eventsNames = yscReader.getEventsNames();
		Map<String, String> interfacesNames = yscReader.getInterfacesNames();

		// If necessary, create a new .ysc file without the definition of the namespace
		if (yscReader.hasNamespace())
			sourceFile = YscWriter.writeWithoutNSVersion(sourceFilePath);

		// Obtain the needed Strings
		String firstUpperStatechartName = statechartName.substring(0, 1).toUpperCase() + statechartName.substring(1);

		String sgenPath = projectPath + "\\" + MODELS_DIR + "\\" + firstUpperStatechartName + ".sgen";

		String javaPath = projectPath + "\\" + SOURCE_DIR + "\\" + PACKAGE + "\\" + firstUpperStatechartName + ".java";
		String simplifiedJavaPath = projectPath + "\\" + SOURCE_DIR + "\\" + PACKAGE + "\\" + firstUpperStatechartName
				+ "Simplified.java";

		String compilerD = "-d " + projectPath + "\\" + BINARY_DIR;
		String compilerClasspath = "-classpath " + projectPath + "\\" + SOURCE_DIR;

		String evoSimplifiedClass = "-class " + PACKAGE + "." + firstUpperStatechartName + "Simplified";
		String evoProjectCP = "-projectCP " + projectPath + "\\" + BINARY_DIR;
		String evoDTestDir = "-Dtest_dir=" + projectPath + "\\" + TEST_DIR;
		String evoDReportDir = "-Dreport_dir=" + projectPath + "\\evosuite-report";

		String simplifiedJunitPath = projectPath + "\\" + TEST_DIR + "\\" + PACKAGE + "\\" + firstUpperStatechartName
				+ "Simplified_ESTest.java";
		String sctunitPath = projectPath + "\\" + MODELS_DIR + "\\" + firstUpperStatechartName + "Test.sctunit";

		// Generate the .sgen file needed by itemis CREATE to generate the java code
		System.out.println("*******************************************");
		System.out.println("Generating .sgen file...");
		System.out.println("*******************************************");
		ISgenWriter sgenWriter = new SgenWriter();
		sgenWriter.writeSgen(PROJECT_NAME, statechartName, sgenPath, SOURCE_DIR, PACKAGE);

		// Call the itemis CREATE generators
		System.out.println("*******************************************");
		System.out.println("Calling Itemis Create code generator...");
		System.out.println("*******************************************");
		IJavaWriter javaWriter = new JavaWriter();
		javaWriter.callICGenerator(projectPath, itemisScc, MODELS_DIR, sourceFile, statechartName);

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
		compile(compilerD, compilerClasspath, javaPath);

		// Modify the generated Java code to create a simplified version
		System.out.println("*******************************************");
		System.out.println("Generating simplified java class...");
		System.out.println("*******************************************");
		javaWriter.writeSimplifiedVersion(javaPath, simplifiedJavaPath);

		// Compile the new simplified class
		System.out.println("*******************************************");
		System.out.println("Compiling...");
		System.out.println("*******************************************");
		compile(compilerD, compilerClasspath, simplifiedJavaPath);

		// Delete the VirtualTimer.class file to hide it to Evosuite
		String virtualTimerPath = projectPath + "//" + BINARY_DIR + "//com//yakindu//core//VirtualTimer.class";
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
				eventsNames, interfacesNames, proceedTimes);

		// Generate the .sctunit file
		System.out.println("*******************************************");
		System.out.println("Generating .sctunit file...");
		System.out.println("*******************************************");
		ISctunitWriter sctunitWriter = new SctunitWriter();
		sctunitWriter.writeSctunit(sctunitPath, statechartName, testCaseList, true);

		// Copy the final .sctunit file and eventually generate the .zip of the
		// temporary workspace
		System.out.println("*******************************************");
		System.out.println("Copying .sctunit file...");
		System.out.println("*******************************************");
		copyAndCompress(workspacePath, sctunitPath, sourceFilePath, statechartName, hasGenArtifacts);

		// End the execution
		System.out.println("*******************************************");
		System.out.println("Finished.");
		System.out.println("*******************************************");
		System.out.println("--------------------------------------------------------------");
		System.exit(0);
	}

	/**
	 * Compile.
	 *
	 * @param compilerD         the string "-d ProjectPath", where ProjectPath is
	 *                          the path of the directory where the .class files
	 *                          will be put
	 * @param compilerClasspath the string "-classpath ClassPath", where ClassPath
	 *                          is the classpath of the compilation
	 * @param javaPath          the path of the .java file to compile
	 */
	private static void compile(String compilerD, String compilerClasspath, String javaPath) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		List<String> compilationArgs = new ArrayList<String>();
		compilationArgs.addAll(Arrays.asList(compilerD.split(" ")));
		compilationArgs.addAll(Arrays.asList(compilerClasspath.split(" ")));
		compilationArgs.add("-implicit:class");
		StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, null, null);
		File f = new File(javaPath);
		Iterable<? extends JavaFileObject> compilationUnits = stdFileManager
				.getJavaFileObjectsFromFiles(Arrays.asList(f));
		compiler.getTask(null, null, null, compilationArgs, null, compilationUnits).call();
	}

	/**
	 * Initialize a temporary Eclipse Project.
	 * 
	 * @param yscPath the absolute path of the input .ysc file
	 * 
	 * @return the absolute path of the temporary workspace containing the Eclipse
	 *         project.
	 * 
	 * @throws IOException        if any IO errors occur.
	 * @throws URISyntaxException if the location of the evosuite jar can not be
	 *                            parsed as a uri
	 */
	private static String initProject(String yscPath) throws IOException, URISyntaxException {
		// Create the structure of the temporary workspace
		File workspace = Files.createTempDirectory(WORKSPACE_NAME).toFile();
		File projectDir = new File(workspace.getAbsolutePath() + "\\" + PROJECT_NAME);
		File binDir = new File(projectDir.getAbsolutePath() + "\\" + BINARY_DIR);
		File srcDir = new File(projectDir.getAbsolutePath() + "\\" + SOURCE_DIR);
		File testDir = new File(projectDir.getAbsolutePath() + "\\" + TEST_DIR);
		File modelsDir = new File(projectDir.getAbsolutePath() + "\\" + MODELS_DIR);

		// Create the actual directories
		projectDir.mkdir();
		binDir.mkdir();
		srcDir.mkdir();
		testDir.mkdir();
		modelsDir.mkdir();

		// Populate the temporary workspace with the files necessary to make it an Xtext
		// project and with the input .ysc file
		ClassLoader classLoader = Createst.class.getClassLoader();
		InputStream sourceStream;

		sourceStream = classLoader.getResourceAsStream("classpath.txt");
		File dest = new File(projectDir.getAbsolutePath() + "\\.classpath");
		Files.copy(sourceStream, dest.toPath());
		sourceStream.close();

		sourceStream = classLoader.getResourceAsStream("project.txt");
		dest = new File(projectDir.getAbsolutePath() + "\\.project");
		Files.copy(sourceStream, dest.toPath());
		sourceStream.close();

		String sourceFile = yscPath.substring(yscPath.lastIndexOf('\\') + 1);
		File source = new File(yscPath);
		dest = new File(modelsDir.getAbsolutePath() + "\\" + sourceFile);
		Files.copy(source.toPath(), dest.toPath());

		return workspace.getAbsolutePath();
	}

	/**
	 * Copies the .sctunit file and eventually generate the .zip of the workspace.
	 * 
	 * @param workspacePath         the absolute path of the temporary workspace
	 * @param simplifiedSctunitPath the absolute path of the .sctunit file in the
	 *                              temporary workspace
	 * @param sourceFilePath        the absolute path of the .ysc file in the
	 *                              temporary workspace
	 * @param statechartName        the name of the input statechart
	 * @param hasGenArtifacts       true if a .zip containing all the artifacts
	 *                              generated in the temporary workspace should be
	 *                              generated, false otherwise
	 * 
	 * @throws IOException        if any IO errors occur.
	 * @throws URISyntaxException if the execution location can not be parsed as a
	 *                            uri
	 */
	private static void copyAndCompress(String workspacePath, String simplifiedSctunitPath, String sourceFilePath,
			String statechartName, boolean hasGenArtifacts) throws IOException, URISyntaxException {
		// Get the location in which the JAR is executed
		String execLocationPath = new File(Createst.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParentFile().getPath();

		// Copy the .sctunit file from the temporary workspace to the execution
		// directory
		File source = new File(simplifiedSctunitPath);
		File dest = new File(execLocationPath + "\\" + statechartName + "Test.sctunit");
		int sctunit_suffix = 0;
		while (dest.exists()) {
			sctunit_suffix++;
			dest = new File(execLocationPath + "\\" + statechartName + "Test_" + sctunit_suffix + ".sctunit");
		}
		Files.copy(source.toPath(), dest.toPath());
		System.out.println(dest.getAbsolutePath() + " succesfully written");

		// If the .ysc without namespace has been generated, copy it from the temporary
		// workspace to the execution directory
		String ysc_suffix = "_without_ns";
		source = new File(sourceFilePath.replace(".ysc", ysc_suffix + ".ysc"));
		if (source.exists()) {
			String sourceFileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("\\"),
					sourceFilePath.lastIndexOf("."));
			dest = new File(execLocationPath + "\\" + sourceFileName + ysc_suffix + ".ysc");
			while (dest.exists()) {
				ysc_suffix = ysc_suffix.concat("_");
				dest = new File(execLocationPath + "\\" + sourceFileName + ysc_suffix + ".ysc");
			}
			Files.copy(source.toPath(), dest.toPath());
			System.out.println(dest.getAbsolutePath() + " succesfully written");
		}

		// If the '-g' option is used, compress the temporary workspace into a .zip and
		// put it in the execution directory
		if (hasGenArtifacts) {
			System.out.println("*******************************************");
			System.out.println("Compressing the artifacts...");
			System.out.println("*******************************************");
			String workspaceName = workspacePath.substring(workspacePath.lastIndexOf('\\'));
			dest = new File(execLocationPath + workspaceName + ".zip");
			ZipUtil.pack(new File(workspacePath), dest);
			System.out.println(dest.getAbsolutePath() + " succesfully written");
		}
	}

}
