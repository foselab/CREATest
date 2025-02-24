package createst.java.writing;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class JavaWriterCallICTest {
	// HARDCODED PATH TO scc.bta
	private static final String SCC = "C:/Users/nico.pellegrinelli/Desktop/Eclipse/itemis_CREATE/scc.bat";

	private static final String RESOURCES_DIR = "src" + File.separator + "test" + File.separator + "resources";
	private static final String STATECHART_NAME = "Statechart";
	private static final String NAMESPACE = "my_ns";
	private static final String PROJECT_NAME = "Javawriting_project";
	private static final String SOURCE_DIR = "src";
	private static final String MODELS_DIR = "model";
	private static final String PACKAGE_NAME = "statechart";

	private static IJavaWriter writer;

	private static String projectPath;
	private static String srcDir;
	private static String javaPath;

	@BeforeClass
	public static void initTempFolder() throws IOException {
		writer = new JavaWriter();
		projectPath = RESOURCES_DIR + File.separator + PROJECT_NAME;
		srcDir = projectPath + File.separator + SOURCE_DIR;
		javaPath = srcDir + File.separator + PACKAGE_NAME + File.separator + NAMESPACE + File.separator
				+ STATECHART_NAME + ".java";
	}

	@After
	public void deleteFiles() throws IOException {
		if (new File(srcDir).exists()) {
			Files.walk(Paths.get(srcDir)).sorted(Comparator.reverseOrder()) // Delete files before directories
					.forEach(p -> {
						try {
							Files.delete(p);
						} catch (IOException e) {
							System.err.println("Failed to delete: " + p);
						}
					});
		}
	}
	
	@Test(expected = IOException.class)
	@Ignore
	public void testNullProject() throws IOException {
		writer.callICGenerator(null, SCC, MODELS_DIR);
	}
	
	@Test(expected = IOException.class)
	@Ignore
	public void testWrongProject() throws IOException {
		writer.callICGenerator("wrongProject", SCC, MODELS_DIR);
	}

	@Test(expected = NullPointerException.class)
	@Ignore
	public void testNullScc() throws IOException {
		writer.callICGenerator(projectPath, null, MODELS_DIR);
	}
	
	@Test(expected = IOException.class)
	@Ignore
	public void testWrongScc() throws IOException {
		writer.callICGenerator(projectPath, "wrongSCC", MODELS_DIR);
	}
	
	@Test(expected = IOException.class)
	@Ignore
	public void testNullModelsDir() throws IOException {
		writer.callICGenerator(projectPath, SCC, null);
	}
	
	@Test(expected = IOException.class)
	@Ignore
	public void testWrongModelsDir() throws IOException {
		writer.callICGenerator(projectPath, SCC, "wrongModels");
	}

	@Test
	@Ignore
	public void testCorrectPath() throws IOException {
		assertFalse(Files.exists(Paths.get(javaPath)));
		writer.callICGenerator(projectPath, SCC, MODELS_DIR);
		assertTrue(Files.exists(Paths.get(javaPath)));
	}

}
