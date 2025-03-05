package createst.junit.reading;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import createst.java.reading.ProceedTime;

public interface IJunitReader {
	
	/**
	 * Reads a junit file that tests a java implementation of a statechart
	 *
	 * @param junitPath       the path of the .java file containing the junit test
	 *                        cases
	 * @param statechartName  the names of the statechart
	 * @param statesNames     the dictionary of the states names with the
	 *                        corresponding enum as key
	 * @param eventsNames     the dictionary of the events names with the
	 *                        corresponding method as key
	 * @param interfacesNames the dictionary of the interfaces names with the
	 *                        corresponding class name as key
	 * @param operationsNames the dictionary of the operations names with the
	 *                        method as key
	 * @return the list of the test cases
	 * @throws IOException if any IO errors occur.
	 */
	public List<TestCase> getTestCases(String junitPath, String statechartName, Map<String, String> statesNames,
			Map<String, String> eventsNames, Map<String, String> interfacesNames, Map<String, String> operationsNames,
			Map<Integer, ProceedTime> proceedTimes) throws IOException;
	
}
