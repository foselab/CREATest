package createst.ysc.writing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

public class YscWriterTest {
	private static final String RESOURCES_DIR = "src\\test\\resources";
	private static final String STATECHART_NAME = "Yscwriting_statechart";
	
	private static IYscWriter writer;

	private static String yscPath;
	private static String rootPath;
	private static String yscCopyPath;

	@ClassRule
	public static TemporaryFolder tmpFolder = new TemporaryFolder();

	@BeforeClass
	public static void initTempFolder() throws IOException{
		writer = new YscWriter();
		yscPath = RESOURCES_DIR + "\\" + STATECHART_NAME + ".ysc";
		rootPath = tmpFolder.getRoot().getCanonicalFile().toString();
	}
	
	@Before
	public void copyFile() throws IOException {
		yscCopyPath = rootPath + "\\" + STATECHART_NAME + ".ysc";
		Files.copy(new File(yscPath).toPath(), new File(yscCopyPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	@After
	public void deleteFile() {
		if (Files.exists(Paths.get(yscCopyPath)))
			new File(yscCopyPath).delete();
	}

	@Test(expected = NullPointerException.class)
	public void testNullPath() throws IOException, ParserConfigurationException, SAXException {
		String wrongYscPath = null;
		writer.changeImports(wrongYscPath);
	}

	@Test(expected = IOException.class)
	public void testWrongPath() throws IOException, ParserConfigurationException, SAXException {
		String wrongYscPath = RESOURCES_DIR;
		writer.changeImports(wrongYscPath);
	}

	@Test
	public void testCorrectParameters() throws IOException, ParserConfigurationException, SAXException {
		writer.changeImports(yscCopyPath);

		String oldYsc = new String(Files.readAllBytes(Paths.get(yscPath)), StandardCharsets.UTF_8);
		String newYsc = new String(Files.readAllBytes(Paths.get(yscCopyPath)), StandardCharsets.UTF_8);

		assertTrue(oldYsc.contains("import &#xA;:&quot;statechart3.ysc&quot;"));
		assertTrue(oldYsc.contains("import:&#x9;&#x9;&quot;path/to/statechart.ysc&quot;"));
		assertTrue(oldYsc.contains("import:&#xA; &quot;another\\\\path\\\\to\\\\statechart2.ysc&quot;"));
		assertTrue(newYsc.contains("import &#xA;:&quot;statechart3.ysc&quot;"));
		assertTrue(newYsc.contains("import:&#x9;&#x9;&quot;statechart.ysc&quot;"));
		assertTrue(newYsc.contains("import:&#xA; &quot;statechart2.ysc&quot;"));
	}

}
