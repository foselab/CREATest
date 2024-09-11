package createst.java.reading;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

/**
 * The Class JavaReader.
 */
public class JavaReader implements IJavaReader {

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
	@Override
	public Map<Integer, ProceedTime> getProceedTimes(String javaPath) throws FileNotFoundException {
		// Get the compilation unit of the java class
		CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(javaPath));
		// Create a dictionary for the time events, the key is the ID, the value is the
		// associated time
		Map<Integer, ProceedTime> proceedTimes = new HashMap<Integer, ProceedTime>();
		for (MethodCallExpr m : cu.findAll(MethodCallExpr.class)) {
			// At each time event correspond a setTimer() method call with 4 arguments
			if (m.getNameAsString().equals("setTimer") && m.getArguments().size() == 4) {
				// The second argument is the integer representing the ID
				int id = Integer.parseInt(m.getArgument(1).toString());
				// The third argument represents the proceed time in milliseconds associated with
				// the event, e.g: (5l * 1000l), (500l / 1000000l), 500l
				String expr = m.getArgument(2).toString().replaceAll("[()]", "");
				try {
					long value = Long.parseLong(expr.substring(0, expr.indexOf('l')));
					TimeUnit unit;
					if (expr.contains("* 1000l")) {
						unit = TimeUnit.SECONDS;
					} else if (expr.contains("/ 1000l")) {
						unit = TimeUnit.MICROSECONDS;
					} else if (expr.contains("/ 1000000l")) {
						unit = TimeUnit.NANOSECONDS;
					} else {
						unit = TimeUnit.MILLISECONDS;
					}
					proceedTimes.put(id, new ProceedTime(value, unit));
				} catch (Exception e) {
					System.out.println("Unable to read time event with ID " + id + ", this may result in failing test methods.");
				}
			}
		}
		return proceedTimes;
	}

}
