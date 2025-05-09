package createst.sgen.writing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SgenWriterTest {
	private static final String STATECHART_NAME = "Statechart";
	private static final String PROJECT_NAME = "Project";
	private static final String TARGET_DIR = "TargetDir";
	private static final String TARGET_PACKAGE = "TargetPackage";
	private static final String NAMESPACE = "my_ns";
	
	private static ISgenWriter writer;

	private static String rootPath;
	private static String sgenPath;

	@ClassRule
	public static TemporaryFolder tmpFolder = new TemporaryFolder();

	@BeforeClass
	public static void initTempFolder() throws IOException {
		writer = new SgenWriter();
		rootPath = tmpFolder.getRoot().getCanonicalFile().toString();
		sgenPath = rootPath + File.separator + STATECHART_NAME + ".sgen";
	}
	
	@After
	public void deleteFile() {
		if (Files.exists(Paths.get(sgenPath)))
			new File(sgenPath).delete();
	}

	@Test(expected = NullPointerException.class)
	public void testNullPath() throws IOException {
		String wrongSgenPath = null;
		writer.writeSgen(PROJECT_NAME, NAMESPACE, STATECHART_NAME, wrongSgenPath, TARGET_DIR, TARGET_PACKAGE);
	}

	@Test(expected = IOException.class)
	public void testWrongPath() throws IOException {
		String wrongSgenPath = rootPath;
		writer.writeSgen(PROJECT_NAME, NAMESPACE, STATECHART_NAME, wrongSgenPath, TARGET_DIR, TARGET_PACKAGE);
	}

	@Test
	public void testCorrectParameters() throws IOException {
		assertFalse(Files.exists(Paths.get(sgenPath)));
		writer.writeSgen(PROJECT_NAME, NAMESPACE, STATECHART_NAME, sgenPath, TARGET_DIR, TARGET_PACKAGE);
		assertTrue(Files.exists(Paths.get(sgenPath)));

		String sgen = new String(Files.readAllBytes(Paths.get(sgenPath)), StandardCharsets.UTF_8);
		assertTrue(sgen.contains("const PROJECT : string = \""+ PROJECT_NAME + "\""));
		assertTrue(sgen.contains("const FOLDER : string = \""+ TARGET_DIR + "\""));
		assertTrue(sgen.contains("statechart " + NAMESPACE + "." + STATECHART_NAME + " {"));
		assertTrue(sgen.contains("basePackage = \"" + TARGET_PACKAGE +"\""));
	}
	
	@Test
	public void testNoNamespaceAndCorrectParameters() throws IOException {
		assertFalse(Files.exists(Paths.get(sgenPath)));
		writer.writeSgen(PROJECT_NAME, null, STATECHART_NAME, sgenPath, TARGET_DIR, TARGET_PACKAGE);
		assertTrue(Files.exists(Paths.get(sgenPath)));

		String sgen = new String(Files.readAllBytes(Paths.get(sgenPath)), StandardCharsets.UTF_8);
		assertTrue(sgen.contains("const PROJECT : string = \""+ PROJECT_NAME + "\""));
		assertTrue(sgen.contains("const FOLDER : string = \""+ TARGET_DIR + "\""));
		assertTrue(sgen.contains("statechart " + STATECHART_NAME + " {"));
		assertTrue(sgen.contains("basePackage = \"" + TARGET_PACKAGE +"\""));
	}

}
