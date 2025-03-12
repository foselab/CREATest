package createst.java.reading;

import java.io.IOException;
import java.util.Map;

public interface IJavaReader {
	
	/**
	 * Create and return dictionary for the time events in the specified java file
	 * with the corresponding id as key.
	 *
	 * @param javaPath the path of the .java file
	 * @return the dictionary
	 * @throws IOException if any IO errors occur.
	 */
	public Map<Integer, ProceedTime> getProceedTimes(String javaPath) throws IOException;
	
}
