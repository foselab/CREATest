package createst.sctunit.writing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import createst.junit.reading.TestCase;

public class SctunitWriterTest {
	private static final String STATECHART_NAME = "Statechart";
	private static final String NAMESPACE = "my_ns";
	
	private static ISctunitWriter writer;

	private static String rootPath;
	private static String sctunitPath;

	@ClassRule
	public static TemporaryFolder tmpFolder = new TemporaryFolder();

	@BeforeClass
	public static void initTempFolder() throws IOException {
		writer = new SctunitWriter();
		rootPath = tmpFolder.getRoot().getCanonicalFile().toString();
		sctunitPath = rootPath + File.separator + STATECHART_NAME + ".sctunit";
	}
	
	@After
	public void deleteFile() {
		if (Files.exists(Paths.get(sctunitPath)))
			new File(sctunitPath).delete();
	}
	
	@Test(expected = NullPointerException.class)
	public void testNullPath() throws IOException {
		List<TestCase> testCases = new ArrayList<TestCase>();
		TestCase t = new TestCase("test");
		t.addEnter();
		testCases.add(t);
		
		String wrongSctunitPath = null;
		
		writer.writeSctunit(wrongSctunitPath, NAMESPACE, STATECHART_NAME, new ArrayList<TestCase>(), false);
	}
	
	@Test(expected = IOException.class)
	public void testWrongPath() throws IOException {
		List<TestCase> testCases = new ArrayList<TestCase>();
		TestCase t = new TestCase("test");
		t.addEnter();
		testCases.add(t);
		
		String wrongSctunitPath = rootPath;
		
		writer.writeSctunit(wrongSctunitPath, NAMESPACE, STATECHART_NAME, new ArrayList<TestCase>(), false);
	}
	
	@Test
	public void testNullTestCasesAndNamespace() throws IOException {
		assertFalse(Files.exists(Paths.get(sctunitPath)));
		writer.writeSctunit(sctunitPath, NAMESPACE, STATECHART_NAME, null, false);
		assertTrue(Files.exists(Paths.get(sctunitPath)));
		String sctunit = new String(Files.readAllBytes(Paths.get(sctunitPath)), StandardCharsets.UTF_8);
		assertTrue(sctunit.contains("testclass " + STATECHART_NAME + "Test for statechart " + NAMESPACE + "." + STATECHART_NAME + " {"));
	}
	
	@Test
	public void testCorrectParameters() throws IOException {
		List<TestCase> testCases = new ArrayList<TestCase>();
		
		String testName = "test1";
		String time = "10";
		String timeUnit = "ms";
		String event = "Event";
		String typedEvent = "tEvent";
		String value = "3.14";
		String state = "StateA";
		
		TestCase t = new TestCase(testName);
		t.addEnter();
		t.addProceedCycles("1");
		t.addProceedTime(time, timeUnit);
		t.addTriggerWithoutEvent();
		t.addEvent(event);
		t.addTypedEvent(typedEvent, value);
		t.addIsActive(true);
		t.addIsFinal(false);
		t.addAssertState(state, true);
		t.addExit();
		testCases.add(t);
		
		assertFalse(Files.exists(Paths.get(sctunitPath)));
		writer.writeSctunit(sctunitPath, null, STATECHART_NAME, testCases, true);
		assertTrue(Files.exists(Paths.get(sctunitPath)));

		String sctunit = new String(Files.readAllBytes(Paths.get(sctunitPath)), StandardCharsets.UTF_8);
		assertTrue(sctunit.contains("testclass " + STATECHART_NAME + "Simplified" + "Test for statechart " + STATECHART_NAME + " {"));
		assertTrue(sctunit.contains("operation " + testName + " () {"));
		assertTrue(sctunit.contains("enter"));
		assertTrue(sctunit.contains("proceed 1 cycle"));
		assertTrue(sctunit.contains("proceed " + time + timeUnit));
		assertTrue(sctunit.contains("triggerWithoutEvent"));
		assertTrue(sctunit.contains("raise " + event));
		assertTrue(sctunit.contains("raise " + typedEvent + ": " + value));
		assertTrue(sctunit.contains("assert is_active"));
		assertTrue(sctunit.contains("assert !is_final"));
		assertTrue(sctunit.contains("assert active (" + state + ")"));
		assertTrue(sctunit.contains("exit"));
	}

}
