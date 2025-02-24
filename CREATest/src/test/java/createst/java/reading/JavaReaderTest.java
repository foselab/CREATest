package createst.java.reading;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class JavaReaderTest {
	private static final String RESOURCES_DIR = "src" + File.separator + "test" + File.separator + "resources";
	private static final String FILE_NAME = "Javareading_statechart.java";
	
	private static IJavaReader reader = new JavaReader();
	
	@Test(expected = NullPointerException.class)
	public void testNullInput() throws FileNotFoundException, IOException{
		reader.getProceedTimes(null);
	}

	@Test(expected = FileNotFoundException.class)
	public void testEmptyInput() throws FileNotFoundException, IOException{
		reader.getProceedTimes("");
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testNotExistingInput() throws FileNotFoundException, IOException{
		reader.getProceedTimes(RESOURCES_DIR + File.separator + "NotExistingStatechart.java");
	}

	@Test
	public void testCorrectInput() throws FileNotFoundException, IOException {
		Map<Integer, ProceedTime> actual = reader.getProceedTimes(RESOURCES_DIR + File.separator + FILE_NAME);
		Map<Integer, ProceedTime> expected = new HashMap<Integer, ProceedTime>();
		expected.put(0, new ProceedTime(100, TimeUnit.MILLISECONDS));
		expected.put(1, new ProceedTime(3, TimeUnit.SECONDS));
		expected.put(2, new ProceedTime(1000, TimeUnit.NANOSECONDS));
		expected.put(3, new ProceedTime(500, TimeUnit.MICROSECONDS));
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

}
