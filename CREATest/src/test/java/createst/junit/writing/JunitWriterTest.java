package createst.junit.writing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import createst.junit.writing.IJunitWriter;
import createst.junit.writing.JunitWriter;

public class JunitWriterTest {
	private static final String RESOURCES_DIR = "src\\test\\resources";
	private static final String TEST_DIR = "test";
	private static final String REPORT_DIR = "report";
	private static final String PROJECT_NAME = "Junitwriting_project";
	private static final String BINARY_DIR = "bin";
	private static final String PACKAGE_NAME = "junitwriting";
	private static final String FILE_NAME = "Statechart";
	
	private static IJunitWriter writer;

	private static String rootPath;
	private static String junitPath;
	private static String scaffoldingPath;
	private static String csvPath;

	@ClassRule
	public static TemporaryFolder tmpFolder = new TemporaryFolder();

	@BeforeClass
	public static void initTempFolder() throws IOException {
		writer = new JunitWriter();
		rootPath = tmpFolder.getRoot().getCanonicalFile().toString();
		junitPath = rootPath + "\\" + TEST_DIR + "\\" + PACKAGE_NAME + "\\" + FILE_NAME
				+ "_ESTest.java";
		scaffoldingPath = rootPath + "\\" + TEST_DIR + "\\" + PACKAGE_NAME + "\\" + FILE_NAME
				+ "_ESTest_scaffolding.java";
		csvPath = rootPath + "\\" + REPORT_DIR + "\\statistics.csv";
		tmpFolder.newFolder(TEST_DIR);
		tmpFolder.newFolder(REPORT_DIR);
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
		String projectCpOption = "-projectCP " + RESOURCES_DIR + "\\" + PROJECT_NAME + "\\" + BINARY_DIR;
		String testDirOption = "-Dtest_dir=" + rootPath + "\\" + TEST_DIR;
		String reportDirOption = "-Dreport_dir=" + rootPath + "\\" + REPORT_DIR;
		
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
		String projectCpOption = "-projectCP " + RESOURCES_DIR + "\\" + PROJECT_NAME + "\\" + "WrongBinaryDir";
		String testDirOption = "-Dtest_dir=" + rootPath + "\\" + TEST_DIR;
		String reportDirOption = "-Dreport_dir=" + rootPath + "\\" + REPORT_DIR;
		
		writer.callEvosuite(classOption, projectCpOption, testDirOption, reportDirOption, true, 1);
	}

	@Test
	public void testCorrectArgs() throws IOException, InterruptedException {
		assertFalse(Files.exists(Paths.get(junitPath)));
		assertFalse(Files.exists(Paths.get(scaffoldingPath)));
		assertFalse(Files.exists(Paths.get(csvPath)));
		
		String classOption = "-class " + PACKAGE_NAME + "." + FILE_NAME;
		String projectCpOption = "-projectCP " + RESOURCES_DIR + "\\" + PROJECT_NAME + "\\" + BINARY_DIR;
		String testDirOption = "-Dtest_dir=" + rootPath + "\\" + TEST_DIR;
		String reportDirOption = "-Dreport_dir=" + rootPath + "\\" + REPORT_DIR;
		
		writer.callEvosuite(classOption, projectCpOption, testDirOption, reportDirOption, true, 1);
		
		assertTrue(Files.exists(Paths.get(junitPath)));
		assertTrue(Files.exists(Paths.get(scaffoldingPath)));
		assertTrue(Files.exists(Paths.get(csvPath)));
	}

}
