package createst.java.reading;

import java.io.FileNotFoundException;
import java.util.Map;

public interface IJavaReader {
	
	public Map<Integer, ProceedTime> getProceedTimes(String javaPath) throws FileNotFoundException;
	
}
