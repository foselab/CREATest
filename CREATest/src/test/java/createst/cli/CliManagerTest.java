package createst.cli;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import createst.cli.CliManager;
import createst.cli.ICliManager;
import createst.cli.InputValues;

import static org.assertj.core.api.Assertions.assertThat;

public class CliManagerTest {
	private static final String SCC = "scc.bat";
	private static final String WORKSPACE_PATH = "workspace";
	private static final String PROJECT_NAME = "project";
	private static final String PROJECT_PATH = WORKSPACE_PATH + "\\" + PROJECT_NAME;
	private static final String SOURCE_DIR = "model";
	private static final String SOURCE_FILE_NAME = "Statechart";
	private static final String SOURCE_FILE = SOURCE_FILE_NAME + ".ysc";
	private static final String TARGET_DIR = "src";
	private static final String TARGET_PACKAGE = "package";
	private static final String BINARY_DIR = "bin";
	private static final String EVO_TEST_DIR = "test";
	private static final String EVO_SEARCH_BUDGET = "10";
	
	private static ICliManager cli;
	private static String rootPath;
	
	@ClassRule
	public static TemporaryFolder tmpFolder = new TemporaryFolder();
	
	@BeforeClass
	public static void initTempFolder() throws IOException {
		cli = new CliManager();
		rootPath = tmpFolder.getRoot().getCanonicalFile().toString();
		tmpFolder.newFile(SCC);
		tmpFolder.newFolder(WORKSPACE_PATH, PROJECT_NAME, SOURCE_DIR, SOURCE_FILE).createNewFile();
		tmpFolder.newFolder(WORKSPACE_PATH, PROJECT_NAME, TARGET_DIR, TARGET_PACKAGE);
		tmpFolder.newFolder(WORKSPACE_PATH, PROJECT_NAME, BINARY_DIR);
		tmpFolder.newFolder(WORKSPACE_PATH, PROJECT_NAME, EVO_TEST_DIR);
	}
	
	@Test
	public void testNullArgs() throws IOException {
		String[] args = null;
		InputValues expected = null;
		InputValues actual = cli.parse(args);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEmptyArgs() throws IOException {
		String[] args = new String[] {};
		InputValues expected = null;
		InputValues actual = cli.parse(args);
		assertEquals(expected, actual);
	}


	@Test
	public void testHelpOption() throws IOException {
		String[] args = new String[] 
				{
					"-h",
					"-scc", rootPath + "\\" + SCC
				};
		InputValues expected = null;
		InputValues actual = cli.parse(args);
		assertEquals(expected, actual);
		
		args = new String[] 
				{
					"--help",
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMissingRequiredOption() throws IOException {
		String[] args = new String[] 
				{
					"-projectPath", rootPath + "\\" + PROJECT_PATH,
					"-sourceDir", SOURCE_DIR,
					"-sourceFile", SOURCE_FILE_NAME,
					"-targetDir", TARGET_DIR,
				};
		InputValues expected = null;
		InputValues actual = cli.parse(args);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testWrongOption() throws IOException {
		// SCC does not exist
		String[] args = new String[] 
				{
					"-scc", "WrongSccPath",
					"-projectPath", rootPath + "\\" + PROJECT_PATH,
					"-sourceDir", SOURCE_DIR,
					"-sourceFile", SOURCE_FILE_NAME,
					"-targetPackage", TARGET_PACKAGE,
				};
		InputValues expected = null;
		InputValues actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// SCC is not a file
		args = new String[] 
				{
					"-scc", rootPath,
					"-projectPath", rootPath + "\\" + PROJECT_PATH,
					"-sourceDir", SOURCE_DIR,
					"-sourceFile", SOURCE_FILE_NAME,
					"-targetPackage", TARGET_PACKAGE,
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// ProjectPath does not exist
		args = new String[] 
				{
					"-scc", rootPath + "\\" + SCC,
					"-projectPath", "WrongProjectPath",
					"-sourceDir", SOURCE_DIR,
					"-sourceFile", SOURCE_FILE_NAME,
					"-targetPackage", TARGET_PACKAGE,
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// SourceDir does not exist
		args = new String[] 
				{
					"-scc", rootPath + "\\" + SCC,
					"-projectPath", rootPath + "\\" + PROJECT_PATH,
					"-sourceDir", "WrongSourceDir",
					"-sourceFile", SOURCE_FILE_NAME,
					"-targetPackage", TARGET_PACKAGE,
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// SourceFile does not exist
		args = new String[] 
				{
					"-scc", rootPath + "\\" + SCC,
					"-projectPath", rootPath + "\\" + PROJECT_PATH,
					"-sourceDir", SOURCE_DIR,
					"-sourceFile", "WrongSourceFile",
					"-targetPackage", TARGET_PACKAGE,
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// BinaryDir does not exist
		args = new String[] 
				{
					"-scc", rootPath + "\\" + SCC,
					"-projectPath", rootPath + "\\" + PROJECT_PATH,
					"-sourceDir", SOURCE_DIR,
					"-sourceFile", SOURCE_FILE_NAME,
					"-targetPackage", TARGET_PACKAGE,
					"-binaryDir", "WrongBinaryDir",
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// EvoSearchBudget is negative
		args = new String[] 
				{
					"-scc", rootPath + "\\" + SCC,
					"-projectPath", rootPath + "\\" + PROJECT_PATH,
					"-sourceDir", SOURCE_DIR,
					"-sourceFile", SOURCE_FILE_NAME,
					"-targetPackage", TARGET_PACKAGE,
					"-evoSearchBudget", "-10",
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// EvoSearchBudget is not a number
		args = new String[] 
				{
					"-scc", rootPath + "\\" + SCC,
					"-projectPath", rootPath + "\\" + PROJECT_PATH,
					"-sourceDir", SOURCE_DIR,
					"-sourceFile", SOURCE_FILE_NAME,
					"-targetPackage", TARGET_PACKAGE,
					"-evoSearchBudget", "ten",
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAllCorrectOption() throws IOException {
		String[] args = new String[] 
				{
					"-scc", rootPath + "\\" + SCC,
					"-projectPath", rootPath + "\\" + PROJECT_PATH,
					"-sourceDir", SOURCE_DIR,
					"-sourceFile", SOURCE_FILE_NAME,
					"-targetDir", TARGET_DIR,
					"-targetPackage", TARGET_PACKAGE,
					"-binaryDir", BINARY_DIR,
					"-evoTestDir", EVO_TEST_DIR,
					"-evoSearchBudget", EVO_SEARCH_BUDGET,
				};
		InputValues expected = new InputValues();
		expected.setSccPath(rootPath + "\\" + SCC);
		expected.setWorkspacePath(rootPath + "\\" + WORKSPACE_PATH);
		expected.setProjectName(PROJECT_NAME);
		expected.setSourceDir(SOURCE_DIR);
		expected.setSourceFile(SOURCE_FILE);
		expected.setTargetDir(TARGET_DIR);
		expected.setTargetPackage(TARGET_PACKAGE);
		expected.setBinaryDir(BINARY_DIR);
		expected.setEvoTestDir(EVO_TEST_DIR);
		expected.setEvoSearchBudget(Integer.parseInt(EVO_SEARCH_BUDGET));
		
		InputValues actual = cli.parse(args);
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
		
		assertTrue(actual.hasSearchBudget());
	}

}
