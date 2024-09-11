package createst.ysc.writing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import createst.ysc.writing.YscWriter;

public class YscWriterTest {
	private static final String RESOURCES_DIR = "src\\test\\resources";
	private static final String STATECHART_NAME = "Yscwriting_statechart";

	private static String yscPath;

	@ClassRule
	public static TemporaryFolder tmpFolder = new TemporaryFolder();

	@BeforeClass
	public static void initTempFolder() throws IOException {
		yscPath = RESOURCES_DIR + "\\" + STATECHART_NAME + ".ysc";
	}

	@Test(expected = NullPointerException.class)
	public void testNullPath() throws IOException {
		String wrongYscPath = null;
		YscWriter.writeWithoutNSVersion(wrongYscPath);
	}

	@Test(expected = IOException.class)
	public void testWrongPath() throws IOException {
		String wrongYscPath = RESOURCES_DIR;
		YscWriter.writeWithoutNSVersion(wrongYscPath);
	}

	@Test
	public void testCorrectParameters() throws IOException {
		String newYscPath = RESOURCES_DIR + "\\" + STATECHART_NAME + "_without_ns.ysc";
		String newYscPath2 = RESOURCES_DIR + "\\" + STATECHART_NAME + "_without_ns_.ysc";
		
		assertFalse(Files.exists(Paths.get(newYscPath)));
		YscWriter.writeWithoutNSVersion(yscPath);
		assertTrue(Files.exists(Paths.get(newYscPath)));

		String oldYsc = new String(Files.readAllBytes(Paths.get(yscPath)), StandardCharsets.UTF_8);
		String newYsc = new String(Files.readAllBytes(Paths.get(newYscPath)), StandardCharsets.UTF_8);
		
		assertTrue(oldYsc.contains("namespace"));
		assertFalse(newYsc.contains("namespace"));
		
		assertFalse(Files.exists(Paths.get(newYscPath2)));
		YscWriter.writeWithoutNSVersion(yscPath);
		assertTrue(Files.exists(Paths.get(newYscPath2)));
		
		new File(newYscPath).delete();
		new File(newYscPath2).delete();
	}

}
