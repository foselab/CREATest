package createst.junit.reading;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import createst.java.reading.ProceedTime;

/**
 * The Class JunitReader.
 */
public class JunitReader implements IJunitReader {

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
	 * @return the list of the test cases
	 * @throws IOException if any IO errors occur.
	 */
	@Override
	public List<TestCase> getTestCases(String junitPath, String statechartName, Map<String, String> statesNames,
			Map<String, String> eventsNames, Map<String, String> interfacesNames,
			Map<Integer, ProceedTime> proceedTimes) throws IOException {
		// Get the compilation unit of the (test) class
		CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(junitPath));
		// Visit all the (test) methods contained in the compilation unit.
		// Each visit of a method produces a TestCase object
		List<TestCase> testCaseList = new ArrayList<TestCase>();
		TestCaseCollector testCaseCollector = new TestCaseCollector(statechartName, statesNames, eventsNames,
				interfacesNames, proceedTimes);
		testCaseCollector.visit(cu, testCaseList);
		if (testCaseCollector.hasTimeEvents()) {
			System.out.println("\nWARNING: the generated test suite uses time events,\n"
					+ "it is not guaranteed that all the test methods will pass,\n"
					+ "especially  if the execution is Cycle-based.");
		}
		return testCaseList;
	}

}
