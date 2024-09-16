package createst.cli;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

public class CliManagerTest {
	private static final String SCC = "scc.bat";
	private static final String YSC = "Statechart.ysc";
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
		tmpFolder.newFile(YSC);
		tmpFolder.newFile(YSC.replace(".ysc", ".txt"));
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
					"-s", rootPath + "\\" + SCC,
					"-y", rootPath + "\\" + YSC,
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
					"--sccPath", rootPath + "\\" + SCC,
					"-b", EVO_SEARCH_BUDGET,
					"-g",
				};
		InputValues expected = null;
		InputValues actual = cli.parse(args);
		assertEquals(expected, actual);
		
		args = new String[] 
				{
					"--yscPath", rootPath + "\\" + YSC,
					"-b", EVO_SEARCH_BUDGET,
					"-g",
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testNonExistingOption() throws IOException {
		String[] args = new String[] 
				{
					"--scc", rootPath + "\\" + SCC,
					"-y", rootPath + "\\" + YSC,
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
					"-s", "WrongSccPath",
					"-y", rootPath + "\\" + YSC,
				};
		InputValues expected = null;
		InputValues actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// SCC is not a file
		args = new String[] 
				{
					"-s", rootPath,
					"-y", rootPath + "\\" + YSC,
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// YSC does not exist
		args = new String[] 
				{
					"-s", rootPath + "\\" + SCC,
					"-y", "WrongYscPath",
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// YSC is not a .ysc file
		args = new String[] 
				{
					"-s", rootPath + "\\" + SCC,
					"-y", rootPath + "\\" + YSC.replace(".ysc", ".txt"),
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		
		// EvoSearchBudget is negative
		args = new String[] 
				{
					"-s", rootPath + "\\" + SCC,
					"-y", rootPath + "\\" + YSC,
					"-b", "-10"
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
		
		// EvoSearchBudget is not a number
		args = new String[] 
				{
					"-s", rootPath + "\\" + SCC,
					"-y", rootPath + "\\" + YSC,
					"--evoSearchBudget", "-ten"
				};
		actual = cli.parse(args);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAllCorrectOption() throws IOException {
		String[] args = new String[] 
				{
					"-sccPath", rootPath + "\\" + SCC,
					"-yscPath", rootPath + "\\" + YSC,
					"-evoSearchBudget", EVO_SEARCH_BUDGET,
					"--genArtifacts"
				};
		InputValues expected = new InputValues();
		expected.setSccPath(rootPath + "\\" + SCC);
		expected.setYscPath(rootPath + "\\" + YSC);
		expected.setEvoSearchBudget(Integer.parseInt(EVO_SEARCH_BUDGET));
		expected.setGenArtifacts();
		
		InputValues actual = cli.parse(args);
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
		
		assertTrue(actual.hasSearchBudget());
		assertTrue(actual.hasGenArtifacts());
		
		args = new String[] 
				{
					"-s", rootPath + "\\" + SCC,
					"-y", rootPath + "\\" + YSC,
				};
		expected = new InputValues();
		expected.setSccPath(rootPath + "\\" + SCC);
		expected.setYscPath(rootPath + "\\" + YSC);
		
		actual = cli.parse(args);
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
		
		assertFalse(actual.hasSearchBudget());
		assertFalse(actual.hasGenArtifacts());
	}

}
