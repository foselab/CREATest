package createst.junit.writing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JunitWriterTest {
	private static final String RESOURCES_DIR = "src" + File.separator + "test" + File.separator + "resources";
	private static final String TEST_DIR = "test";
	private static final String REPORT_DIR = "report";
	private static final String PROJECT_NAME = "Junitwriting_project";
	private static final String BINARY_DIR = "bin";
	private static final String SOURCE_DIR = "src";
	private static final String PACKAGE_NAME = "junitwriting";
	private static final String FILE_NAME = "Statechart";
	
	private static IJunitWriter writer;

	private static String rootPath;
	private static String junitPath;
	private static String scaffoldingPath;
	private static String csvPath;
	private static String srcDir;
	private static String binDir;

	@ClassRule
	public static TemporaryFolder tmpFolder = new TemporaryFolder();

	@BeforeClass
	public static void initTempFolder() throws IOException {
		srcDir = RESOURCES_DIR + File.separator + PROJECT_NAME + File.separator + SOURCE_DIR;
		binDir = RESOURCES_DIR + File.separator + PROJECT_NAME + File.separator + BINARY_DIR; 
		writer = new JunitWriter();
		rootPath = tmpFolder.getRoot().getCanonicalFile().toString();
		junitPath = rootPath + File.separator + TEST_DIR + File.separator + PACKAGE_NAME + File.separator + FILE_NAME
				+ "_ESTest.java";
		scaffoldingPath = rootPath + File.separator + TEST_DIR + File.separator + PACKAGE_NAME + File.separator + FILE_NAME
				+ "_ESTest_scaffolding.java";
		csvPath = rootPath + File.separator + REPORT_DIR + File.separator + "statistics.csv";
		tmpFolder.newFolder(TEST_DIR);
		tmpFolder.newFolder(REPORT_DIR);
	}
	
	@BeforeClass
	public static void compile() throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		File outputDir = new File(binDir);
        if (!outputDir.exists())
        	outputDir.mkdirs();
		List<String> compilationArgs = new ArrayList<String>();
		compilationArgs.addAll(Arrays.asList("-d", binDir));
		compilationArgs.addAll(Arrays.asList("-classpath", srcDir));
		compilationArgs.add("-implicit:class");
		StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, null, null);
		List<File> toBeCompiled = new ArrayList<>();
		toBeCompiled.add(new File(srcDir + File.separator + "com" + File.separator + "yakindu" + File.separator + "core" + File.separator + "IEventDriven.java"));
		toBeCompiled.add(new File(srcDir + File.separator + "com" + File.separator + "yakindu" + File.separator + "core" + File.separator + "IStatemachine.java"));
		toBeCompiled.add(new File(srcDir  + File.separator + "junitwriting" + File.separator + "Statechart.java"));
		Iterable<? extends JavaFileObject> compilationUnits = stdFileManager.getJavaFileObjectsFromFiles(toBeCompiled);
		compiler.getTask(null, null, null, compilationArgs, null, compilationUnits).call();
	}
	
	@After
	public void deleteFile() {
		if (Files.exists(Paths.get(junitPath)))
			new File(junitPath).delete();
		if (Files.exists(Paths.get(scaffoldingPath)))
			new File(scaffoldingPath).delete();
		if (Files.exists(Paths.get(csvPath)))
			new File(csvPath).delete();
	}
	
	@Test
	public void testWrongClassArg() throws IOException, InterruptedException {
		assertFalse(Files.exists(Paths.get(junitPath)));
		assertFalse(Files.exists(Paths.get(scaffoldingPath)));
		assertFalse(Files.exists(Paths.get(csvPath)));
		
		String classOption = "-class " + PACKAGE_NAME +  "." + "WrongFileName";
		String projectCpOption = "-projectCP " + RESOURCES_DIR + File.separator + PROJECT_NAME + File.separator + BINARY_DIR;
		String testDirOption = "-Dtest_dir=" + rootPath + File.separator + TEST_DIR;
		String reportDirOption = "-Dreport_dir=" + rootPath + File.separator + REPORT_DIR;
		
		writer.callEvosuite(classOption, projectCpOption, testDirOption, reportDirOption, true, 1);
		
		assertFalse(Files.exists(Paths.get(junitPath)));
		assertFalse(Files.exists(Paths.get(scaffoldingPath)));
		assertFalse(Files.exists(Paths.get(csvPath)));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testWrongProjectCPArg() throws IOException, InterruptedException {
		assertFalse(Files.exists(Paths.get(junitPath)));
		assertFalse(Files.exists(Paths.get(scaffoldingPath)));
		assertFalse(Files.exists(Paths.get(csvPath)));
		
		String classOption = "-class " + PACKAGE_NAME +  "." + FILE_NAME;
		String projectCpOption = "-projectCP " + RESOURCES_DIR + File.separator + PROJECT_NAME + File.separator + "WrongBinaryDir";
		String testDirOption = "-Dtest_dir=" + rootPath + File.separator + TEST_DIR;
		String reportDirOption = "-Dreport_dir=" + rootPath + File.separator + REPORT_DIR;
		
		writer.callEvosuite(classOption, projectCpOption, testDirOption, reportDirOption, true, 1);
	}

	@Test
	public void testCorrectArgs() throws IOException, InterruptedException {
		assertFalse(Files.exists(Paths.get(junitPath)));
		assertFalse(Files.exists(Paths.get(scaffoldingPath)));
		assertFalse(Files.exists(Paths.get(csvPath)));
		
		String classOption = "-class " + PACKAGE_NAME + "." + FILE_NAME;
		String projectCpOption = "-projectCP " + RESOURCES_DIR + File.separator + PROJECT_NAME + File.separator + BINARY_DIR;
		String testDirOption = "-Dtest_dir=" + rootPath + File.separator + TEST_DIR;
		String reportDirOption = "-Dreport_dir=" + rootPath + File.separator + REPORT_DIR;
		
		writer.callEvosuite(classOption, projectCpOption, testDirOption, reportDirOption, true, 1);
		
		assertTrue(Files.exists(Paths.get(junitPath)));
		assertTrue(Files.exists(Paths.get(scaffoldingPath)));
		assertTrue(Files.exists(Paths.get(csvPath)));
	}

}
