package createst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.zeroturnaround.zip.ZipUtil;

import createst.sgen.writing.ISgenWriter;
import createst.sgen.writing.SgenWriter;
import createst.ysc.reading.IYscReader;
import createst.ysc.reading.YscReader;
import createst.ysc.writing.IYscWriter;
import createst.ysc.writing.YscWriter;

/**
 * Contains static methods for managing the temporary workspace and project used
 * by the application
 * 
 */
public abstract class TempWsUtils {

	// Note: if any of this strings is modified, edit
	// src/main/resources/classpath.txt and src/main/resources/project.txt
	// accordingly
	public static final String WORKSPACE_NAME = "ws_";
	public static final String PROJECT_NAME = "CREATestProject";
	public static final String SOURCE_DIR = "src";
	public static final String BASE_PACKAGE = "statechart";
	public static final String BINARY_DIR = "bin";
	public static final String TEST_DIR = "test";
	public static final String MODELS_DIR = "models";
	
	/**
	 * Wait the file to exist. Try for 15 seconds max.
	 * 
	 * @param path the path of the file
	 * @throws FileNotFoundException if after 15 seconds the file still not exists
	 */
	public static void waitFileToBeReady(String path) throws FileNotFoundException {
		int attempts = 15;
        File file = new File(path);
        try {
	        while (!file.exists() && attempts > 0) {
	            System.out.println("Waiting for file to be ready...");
				Thread.sleep(1000);
	            attempts--;
	        }
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        if (!file.exists())
        	throw new FileNotFoundException("The file " + path + " does not exists");
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
	public static void compile(String compilerD, String compilerClasspath, String javaPath) {
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
	public static String initProject(String yscPath) throws IOException, URISyntaxException {
		// Create the structure of the temporary workspace
		File workspace = Files.createTempDirectory(WORKSPACE_NAME).toFile();
		File projectDir = new File(workspace.getAbsolutePath() + File.separator + PROJECT_NAME);
		File binDir = new File(projectDir.getAbsolutePath() + File.separator + BINARY_DIR);
		File srcDir = new File(projectDir.getAbsolutePath() + File.separator + SOURCE_DIR);
		File testDir = new File(projectDir.getAbsolutePath() + File.separator + TEST_DIR);
		File modelsDir = new File(projectDir.getAbsolutePath() + File.separator + MODELS_DIR);

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
		File dest = new File(projectDir.getAbsolutePath() + File.separator + ".classpath");
		Files.copy(sourceStream, dest.toPath());
		sourceStream.close();

		sourceStream = classLoader.getResourceAsStream("project.txt");
		dest = new File(projectDir.getAbsolutePath() + File.separator + ".project");
		Files.copy(sourceStream, dest.toPath());
		sourceStream.close();

		String sourceFile = yscPath.substring(yscPath.lastIndexOf(File.separator) + 1);
		File source = new File(yscPath);
		dest = new File(modelsDir.getAbsolutePath() + File.separator + sourceFile);
		Files.copy(source.toPath(), dest.toPath());

		return workspace.getAbsolutePath();
	}

	/**
	 * Add the sub-machines to the workspace/model directory recursively and change
	 * the imports to math their new position
	 * 
	 * @param workspacePath       the path of the workspace
	 * @param statechartFilePath  the path of the starting statechart
	 * @param importedSubmachines the list of the sub-machines
	 * @throws IOException                  if any IO errors occur.
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created
	 *                                      which satisfies the configuration
	 *                                      requested.
	 * @throws SAXException                 if any parse errors occur.
	 */
	public static void addSubmachines(String workspacePath, String statechartFilePath, List<String> importedSubmachines)
			throws IOException, ParserConfigurationException, SAXException {
		IYscWriter yscRwiter = new YscWriter();
		ISgenWriter sgenWriter = new SgenWriter();
		IYscReader yscReader;
		// Initialize project and model files objects
		File projectDir = new File(workspacePath + File.separator + PROJECT_NAME);
		File modelsDir = new File(projectDir.getAbsolutePath() + File.separator + MODELS_DIR);
		// Obtain the the statechart file name with extension and its parent directory
		String statechartPath = new File(statechartFilePath).getAbsolutePath();
		String statechartFile = statechartPath.substring(statechartPath.lastIndexOf(File.separator) + 1);
		String statechartParentPath = statechartPath.substring(0, statechartPath.lastIndexOf(File.separator));
		// Make the imports statements as the submachines are in the same directory
		yscRwiter.changeImports(modelsDir + File.separator + statechartFile);
		// Copy the sub-machines in the temporary directory
		for (String subMachine : importedSubmachines) {
			// Copy the submachine in the same directory of the importing statechart
			String subMachinePath = new File(subMachine).getAbsolutePath();
			String subMachineFile = subMachinePath.substring(subMachinePath.lastIndexOf(File.separator) + 1);
			File source = new File(statechartParentPath + File.separator + subMachine);
			File dest = new File(modelsDir + File.separator + subMachineFile);
			Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			// Imported statechart may imports other sub-machines
			yscReader = new YscReader(dest.toString());
			addSubmachines(workspacePath, source.toString(), yscReader.getImportedSubMachines());
			// Make the imports statements as the submachines are in the same directory,
			// if it was already done nothing should change
			yscRwiter.changeImports(dest.toString());
			// Create the .sgen file
			String statechartName = yscReader.getStatechartName();
			String namespace = yscReader.getNamespace();
			String firstUpperStatechartName = statechartName.substring(0, 1).toUpperCase()
					+ statechartName.substring(1);
			String sgenPath = projectDir + File.separator + MODELS_DIR + File.separator + firstUpperStatechartName
					+ ".sgen";
			sgenWriter.writeSgen(PROJECT_NAME, namespace, statechartName, sgenPath, SOURCE_DIR, BASE_PACKAGE);
		}
	}

	/**
	 * Copies the .sctunit file and eventually generate the .zip of the workspace.
	 * 
	 * @param workspacePath   the absolute path of the temporary workspace
	 * @param sctunitPath     the absolute path of the .sctunit file in the
	 *                        temporary workspace
	 * @param sourceFilePath  the absolute path of the .ysc file in the temporary
	 *                        workspace
	 * @param statechartName  the name of the input statechart
	 * @param hasGenArtifacts true if a .zip containing all the artifacts generated
	 *                        in the temporary workspace should be generated, false
	 *                        otherwise
	 * @param copyCsv         true if the .csv generated by EvoSuite should be
	 *                        copied outside the .zip, false otherwise
	 * @throws IOException        if any IO errors occur.
	 * @throws URISyntaxException if the execution location can not be parsed as a
	 *                            uri
	 */
	public static void copyAndCompress(String workspacePath, String sctunitPath, String sourceFilePath,
			String statechartName, boolean hasGenArtifacts, boolean copyCsv) throws IOException, URISyntaxException {
		// Get the location in which the JAR is executed
		String execLocationPath = new File(Createst.class.getProtectionDomain().getCodeSource().getLocation().toURI())
				.getParentFile().getPath();

		copySctunitFile(sctunitPath, statechartName, execLocationPath);
		String simplifiedSctunitPath = sctunitPath.replace("Test.sctunit", "SimplifiedTest.sctunit");
		if (Files.exists(Paths.get(simplifiedSctunitPath)))
			copySctunitFile(simplifiedSctunitPath, statechartName + "Simplified", execLocationPath);

		if (copyCsv)
			copyCsv(workspacePath, execLocationPath);

		// If the '-g' option is used, compress the temporary workspace into a .zip and
		// put it in the execution directory
		if (hasGenArtifacts) {
			System.out.println("*******************************************");
			System.out.println("Compressing the artifacts...");
			System.out.println("*******************************************");
			String workspaceName = workspacePath.substring(workspacePath.lastIndexOf(File.separator));
			File dest = new File(execLocationPath + workspaceName + ".zip");
			ZipUtil.pack(new File(workspacePath), dest);
			System.out.println(dest.getAbsolutePath() + " succesfully written");
		}
	}

	/**
	 * Copy the .sctunit file from the temporary workspace to the execution
	 * directory
	 * 
	 * @param sctunitPath      the path of the sctunit inside the temporary project
	 * @param statechartName   the name of the statechart
	 * @param execLocationPath the path where the Java application or the jar is
	 *                         being executed
	 * @throws IOException if any IO errors occur.
	 */
	private static void copySctunitFile(String sctunitPath, String statechartName, String execLocationPath)
			throws IOException {
		File source = new File(sctunitPath);
		File dest = new File(execLocationPath + File.separator + statechartName + "Test.sctunit");
		int sctunit_suffix = 0;
		while (dest.exists()) {
			sctunit_suffix++;
			dest = new File(execLocationPath + File.separator + statechartName + "Test_" + sctunit_suffix + ".sctunit");
		}
		Files.copy(source.toPath(), dest.toPath());
		System.out.println(dest.getAbsolutePath() + " succesfully written");
	}

	/**
	 * Copy the .csv file with evosuite stats from the temporary workspace to the
	 * execution directory (if it already exists append the new lines).
	 * 
	 * @param workspacePath    the absolute path of the temporary workspace
	 * @param execLocationPath the path where the Java application or the jar is
	 *                         being executed
	 * @throws IOException if any IO errors occur.
	 */
	private static void copyCsv(String workspacePath, String execLocationPath) throws IOException {
		String sourceReportPath = workspacePath + File.separator + PROJECT_NAME + File.separator + "evosuite-report" + File.separator + "statistics.csv";
		String destReportPath = execLocationPath + File.separator + "evosuite_stats.csv";
		if (Files.exists(Paths.get(destReportPath))) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(destReportPath, true));
			BufferedReader reader = new BufferedReader(new FileReader(sourceReportPath));
			reader.readLine(); // skip the header row
			while (true) {
				String row = reader.readLine();
				if (row == null)
					break;
				writer.append(row).append("\n");
			}
			reader.close();
			writer.close();
			System.out.println(new File (destReportPath).getAbsolutePath() + " succesfully updated");
		} else {
			File source = new File(sourceReportPath);
			File dest = new File(destReportPath);
			Files.copy(source.toPath(), dest.toPath());
			System.out.println(dest.getAbsolutePath() + " succesfully written");
		}
	}

}
