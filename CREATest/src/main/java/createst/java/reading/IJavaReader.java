package createst.java.reading;

import java.io.FileNotFoundException;
import java.util.Map;

public interface IJavaReader {
	
	/**
	 * Create and return dictionary for the time events in the specified java file
	 * with the corresponding id as key.
	 *
	 * @param javaPath the path of the .java file
	 * @return the dictionary
	 * @throws FileNotFoundException if the file does not exist,is a directory
	 *                               rather than a regular file,or for some other
	 *                               reason cannot be opened forreading.
	 */
	public Map<Integer, ProceedTime> getProceedTimes(String javaPath) throws FileNotFoundException;
	
}
