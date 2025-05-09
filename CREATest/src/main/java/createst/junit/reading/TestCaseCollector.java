package createst.junit.reading;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import createst.java.reading.ProceedTime;

/**
 * The Class TestCaseCollector.
 */
public class TestCaseCollector extends VoidVisitorAdapter<List<TestCase>> {

	/** The name of the statechart */
	String statechartName;

	/**
	 * The dictionary containing all the full names of states in the statechart. The
	 * key is the string representing the corresponding enum
	 */
	Map<String, String> statesNames;

	/**
	 * The dictionary containing all the names of events in the statechart. The key
	 * is the string representing the corresponding method
	 */
	Map<String, String> eventsNames;

	/**
	 * The dictionary containing all the names of interfaces in the statechart. The
	 * key is the string representing the corresponding class name
	 */
	Map<String, String> interfacesNames;

	/**
	 * The dictionary containing all the names of operations in the statechart. The
	 * key is the string representing the corresponding method
	 */
	Map<String, String> operationsNames;

	/**
	 * The dictionary containing all the time events used in the statechart. The key
	 * is the id
	 */
	Map<Integer, ProceedTime> proceedTimes;

	private boolean timeEvents;

	/**
	 * Instantiates a new TestCaseCollector regarding to a statechart.
	 *
	 * @param statechartName  the name of the statechart
	 * @param statesName      the dictionary of the states names with the
	 *                        corresponding enum as key
	 * @param eventsNames     the dictionary of the events names with the
	 *                        corresponding method as key
	 * @param interfacesNames the dictionary of the interfaces names with the
	 *                        corresponding class name as key
	 * @param operationNames  the dictionary of the operations names with the
	 *                        corresponding method as key
	 * @param proceedTimes    the dictionary of the time events with the
	 *                        corresponding id key
	 */
	public TestCaseCollector(String statechartName, Map<String, String> statesNames, Map<String, String> eventsNames,
			Map<String, String> interfacesNames, Map<String, String> operationsNames,
			Map<Integer, ProceedTime> proceedTimes) {
		this.statechartName = statechartName;
		this.statesNames = statesNames;
		this.eventsNames = eventsNames;
		this.interfacesNames = interfacesNames;
		this.operationsNames = operationsNames;
		this.proceedTimes = proceedTimes;
		this.timeEvents = false;
	}

	/**
	 * Visit the method declaration and add a new TestCase instance representing the
	 * visited method declaration to the collector.
	 *
	 * @param node      the method declaration
	 * @param collector the collector
	 */
	@Override
	public void visit(MethodDeclaration node, List<TestCase> collector) {
		// To ensure child nodes of the current node are also visited
		super.visit(node, collector);

		// Discards methods dealing with exceptions (i.e. using try catch statements)
		if (!node.findAll(TryStmt.class).isEmpty()) {
			System.out.println(
					node.getNameAsString() + ": the test case contains a try catch statement, the test is skipped.");
			return;
		}

		// Gets all variable declarations expressions contained in the method
		List<VariableDeclarator> variableDeclarationList = node.findAll(VariableDeclarator.class);

		// Gets all method call expressions contained in the method
		List<MethodCallExpr> methodCallList = node.findAll(MethodCallExpr.class);

		// Produces two dictionaries for each variable declaration,
		// one for the assigned expression and the other for the type.
		Map<String, String> variableAssignments = new HashMap<String, String>();
		Map<String, String> variableTypes = new HashMap<String, String>();
		// The first String (key) for both dictionaries is the variable name
		// (left-hand-side of the assignment).
		// The second String is the assigned expression (right-hand-side of the
		// assignment) or the type of the variable.
		for (VariableDeclarator variableDecl : variableDeclarationList) {
			// The righ-hand-side is the last child of a VariableDeclarator node
			Node rigthHandSide = variableDecl.getChildNodes().get(variableDecl.getChildNodes().size() - 1);
			variableAssignments.put(variableDecl.getNameAsString(), rigthHandSide.toString());
			variableTypes.put(variableDecl.getNameAsString(), variableDecl.getTypeAsString());
		}

		// Creates and populate an instance of TestCase, adding an Action
		// for each method call expression of interest contained in the test method.
		TestCase testCase = new TestCase(node.getNameAsString());
		boolean allDoReturnValid = true;
		boolean allRaiseTimesValid = true;
		boolean allRaiseValid = true;
		for (MethodCallExpr methodCall : methodCallList) {
			String methodName = methodCall.getNameAsString();
			if (methodName.equals("doReturn")) {
				if (manageDoReturn(methodCall, methodCallList, variableTypes, testCase))
					continue;
				allDoReturnValid = false;
				break;
			}
			if (methodName.equals("enter")) {
				testCase.addEnter();
				continue;
			}
			if (methodName.equals("exit")) {
				testCase.addExit();
				continue;
			}
			if (methodName.equals("runCycle")) {
				testCase.addProceedCycles("1");
				continue;
			}
			if (methodName.equals("proceedCycles")) {
				manageProceedCycles(methodCall, testCase);
				continue;
			}
			if (methodName.equals("triggerWithoutEvent")) {
				testCase.addTriggerWithoutEvent();
				continue;
			}
			if (methodName.equals("raiseTimeEvent")) {
				if (manageRaiseTimeEvent(methodCall, testCase))
					continue;
				allRaiseTimesValid = false;
				break;
			}
			if (methodName.startsWith("raise")) {
				if (manageRaiseEvent(methodCall, testCase, variableTypes, node.getNameAsString()))
					continue;
				allRaiseValid = false;
				break;
			}
			if (methodName.equals("assertTrue") || methodName.equals("assertFalse")) {
				manageAssert(methodCall, testCase, variableAssignments, node.getNameAsString(),
						methodName.equals("assertTrue"));
			}
		}

		// Add the test case only if it has no errors (last case) or possible sources of
		// failure
		if (!allDoReturnValid)
			System.out.println(node.getNameAsString()
					+ ": it was not possible to correctly handle a doReturn method call, the test is skipped.");
		else if (!allRaiseTimesValid)
			System.out.println(node.getNameAsString()
					+ ": it was not possible to correctly handle a raiseTimeEvent method call, the test is skipped.");
		else if (!allRaiseValid)
			System.out.println(node.getNameAsString()
					+ ": it was not possible to correctly handle a raiseEvent method call, the test is skipped.");
		else if (hasErrors(testCase))
			System.out.println(node.getNameAsString()
					+ ": a runCycle or proceedCycles method call is called after an exit method call, the test is skipped.");
		else
			collector.add(testCase);
	}

	/**
	 * Checks for time events.
	 *
	 * @return true, if successful
	 */
	public boolean hasTimeEvents() {
		return timeEvents;
	}

	/**
	 * Adds an action representing the given doReturn (for mocking) method call to
	 * the test case, if possible
	 *
	 * @param methodCall     the doReturn method call
	 * @param methodCallList the list of method calls that contains the given
	 *                       doReturn method call
	 * @param variableTypes  the map with the type for each used variable
	 * @param testCase       the test case where to add the new action
	 * @return true if it was possible to add the action or if the argument is null,
	 *         false otherwise
	 */
	private boolean manageDoReturn(MethodCallExpr methodCall, List<MethodCallExpr> methodCallList,
			Map<String, String> variableTypes, TestCase testCase) {
		int methodCallIndex = methodCallList.indexOf(methodCall);
		// The doReturn() method should be followed by two cascade method calls:
		// doReturn().when().myOperation()
		MethodCallExpr whenCall = methodCallList.get(methodCallIndex - 1);
		MethodCallExpr operationCall = methodCallList.get(methodCallIndex - 2);
		// Check that the second method call is to when(), that the operation is in the
		// operations dictionary and that doReturn method call has at most one parameter
		if (!whenCall.getNameAsString().equals("when")
				|| !this.operationsNames.containsKey(operationCall.getNameAsString())
				|| methodCall.getArguments().size() != 1)
			return false;
		// Only mock methods with using parameters such as anyLong() or anyString() are
		// supported
		for (Expression expression : operationCall.getArguments()) {
			String argument = expression.toString();
			if (!argument.startsWith("any"))
				return false;
		}
		// Get the operation to be mocked in SCTUnit
		String mockOperation = this.operationsNames.get(operationCall.getNameAsString());
		// Get the interface if necessary
		String whenCallArgument = whenCall.getArgument(0).toString();
		// If the type of the argument without the final .OperationCallback contains a
		// dot, then it's from a nested class, generated by a named interface
		String whenCallInterface = variableTypes.get(whenCallArgument).replace(".OperationCallback", "");
		if (whenCallInterface.contains(".")) {
			// Obtain the nested class name, if is not in the dictionary, skip the test
			String javaClassName = whenCallInterface.substring(whenCallInterface.indexOf('.') + 1);
			if (interfacesNames.containsKey(javaClassName))
				mockOperation = interfacesNames.get(javaClassName) + "." + mockOperation;
			else
				return false;
		}
		// Get the value to be used for mocking
		String doReturnArgument = methodCall.getArgument(0).toString();
		String mockValue = getSctunitArg(doReturnArgument);
		// If doReturnArgument is not null, the mock is added, else, it is ignored
		if (!mockValue.isEmpty())
			testCase.addMock(mockOperation, mockValue);
		return true;
	}

	/**
	 * Adds an action representing the given proceedCycles method call to the test
	 * case
	 *
	 * @param methodCall the proceedCycle method call
	 * @param testCase   the test case where to add the new action
	 */
	private void manageProceedCycles(MethodCallExpr methodCall, TestCase testCase) {
		// The method proceedCycles has only one argument and it's an int
		String arg = methodCall.getArgument(0).toString();
		// Only positive numbers are meaningful
		if (!arg.contains("-") && !arg.contains("(") && !arg.contains(")")) {
			testCase.addProceedCycles(arg);
		}
	}

	/**
	 * Adds an action representing the given raiseTimeEvent method call to the test
	 * case, if possible
	 *
	 * @param methodCall the raiseTimeEvent method call
	 * @param testCase   the test case where to add the new action
	 * @return true if it was possible to retrieve the actual time to be proceeded,
	 *         false otherwise
	 */
	private boolean manageRaiseTimeEvent(MethodCallExpr methodCall, TestCase testCase) {
		// The method raiseTimeEvent has only one argument and it's an int
		String arg = methodCall.getArgument(0).toString();
		// Only positive numbers in the dictionary are meaningful
		if (!arg.contains("-") && !arg.contains("(") && !arg.contains(")")
				&& proceedTimes.containsKey(Integer.parseInt(arg))) {
			ProceedTime pt = proceedTimes.get(Integer.parseInt(arg));
			String unit = "";
			switch (pt.getUnit()) {
			case SECONDS:
				unit = "s";
				break;
			case MILLISECONDS:
				unit = "ms";
				break;
			case MICROSECONDS:
				unit = "us";
				break;
			case NANOSECONDS:
				unit = "ns";
				break;
			}
			testCase.addProceedTime(String.valueOf(pt.getValue()), unit);
			this.timeEvents = true;
			return true;
		}
		return false;

	}

	/**
	 * Adds an action representing the given raiseEvent method call to the test
	 * case, if possible
	 *
	 * @param methodCall    the raiseEvent method call
	 * @param testCase      the test case where to add the new action
	 * @param variableTypes the map with the type for each used variable
	 * @param testName      the name of the test case
	 * @return true if it was possible to add the action or if the argument is null,
	 *         false otherwise
	 */
	private boolean manageRaiseEvent(MethodCallExpr methodCall, TestCase testCase, Map<String, String> variableTypes,
			String testName) {
		String methodName = methodCall.getNameAsString();
		// Ignore events not in the dictionary
		if (eventsNames.containsKey(methodName)) {
			String event = eventsNames.get(methodName);
			// Get the type of the object whose method is called
			String variableType = variableTypes.get(methodCall.getChildNodes().get(0).toString());
			// If it was not possible to determine the type of the variable, return false
			if (variableType == null)
				return false;
			// If the type contain a dot, then it's from a nested class, generated by a
			// named interface or a sub-machine, in the latter case the test is skipped
			if (variableType.contains(".")) {
				// Obtain the nested class name, if is not in the dictionary, skip the test
				String javaClassName = variableType.substring(variableType.indexOf('.') + 1);
				if (interfacesNames.containsKey(javaClassName))
					event = interfacesNames.get(javaClassName) + "." + event;
				else
					return false;
			}
			// If the raise method call has an argument, then associated event has a type
			// and the argument must be used as the value of the event
			if (methodCall.getArguments().size() > 0) {
				String arg = methodCall.getArgument(0).toString();
				String value = getSctunitArg(arg);
				// If it contains null, the raise event is ignored because null is not supported
				// in sctunit
				if (value.isEmpty())
					return true;
				testCase.addTypedEvent(event, value);
			} else {
				testCase.addEvent(event);
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Given a String representing an argument used in Java, obtain the
	 * corresponding argument to be used in SCTUnit. This method is supposed to
	 * extract ONLY arguments of default SCTUnit types (boolean, strings, long,
	 * double)
	 * 
	 * @param javaArg the java argument
	 * @return the SCTUnit argument, empty if it is not a string and contains null
	 */
	private String getSctunitArg(String javaArg) {
		// If the argument is not a string (something starting and ending with double
		// quotes) or the boolean values true or false, it may need some modifications
		if (!(javaArg.startsWith("\"") && javaArg.endsWith("\"")
				&& !(javaArg.equals("true") || javaArg.equals("false")))) {
			// If it contains null, return the empty string
			if (javaArg.contains("null"))
				return "";
			// Integers are substituted with long in java, the final L must be dropped to
			// be compliant with sctunit, also the external parenthesis are dropped to
			// increase readability
			javaArg = javaArg.replaceAll("[L()]", "");
		}
		return javaArg;
	}

	/**
	 * Adds an action representing the given assertTrue or assertFalse methodCall to
	 * the test case, if possible
	 *
	 * @param methodCall          the assert method call
	 * @param testCase            the test case where to add the new action
	 * @param variableAssignments the map with the assignment for each used variable
	 * @param testName            the name of the test case
	 * @param assertTrue          true if the method call is assertTrue, false if it
	 *                            is assertFalse
	 */
	private void manageAssert(MethodCallExpr methodCall, TestCase testCase, Map<String, String> variableAssignments,
			String testName, boolean assertTrue) {
		// The method has an argument, that is retrieved.
		String assertArgument = methodCall.getArgument(0).toString();
		// The statement that must be checked if it is true (or false) is retrieved.
		// If the argument is a known variable, its assigned expression (right-hand-side
		// of the assignment) is used,
		// else, directly the argument is used.
		String statementToCheck;
		if (variableAssignments.containsKey(assertArgument)) {
			statementToCheck = variableAssignments.get(assertArgument);
		} else {
			statementToCheck = assertArgument;
		}
		if (statementToCheck.contains(".isActive()")) {
			testCase.addIsActive(assertTrue);
			return;
		}
		if (statementToCheck.contains(".isFinal()")) {
			testCase.addIsFinal(assertTrue);
			return;
		}
		if (statementToCheck.contains(".isStateActive")) {
			// The method has an argument, that is retrieved.
			String isStateActiveArgument = statementToCheck.substring(statementToCheck.indexOf('(') + 1,
					statementToCheck.lastIndexOf(')'));
			// The string representing the name of the state in Java is retrieved.
			// If the argument is a known variable, its assigned expression (right-hand-side
			// of the assignment) is used,
			// else, directly the argument is used.
			String javaStateName;
			if (variableAssignments.containsKey(isStateActiveArgument)) {
				javaStateName = variableAssignments.get(isStateActiveArgument);
			} else {
				javaStateName = isStateActiveArgument;
			}
			// If the stateName is obtained using valueof, only the argument of valueof must
			// be used as state name
			if (javaStateName.contains("valueof")) {
				javaStateName = javaStateName.substring(javaStateName.indexOf('(') + 1, javaStateName.indexOf(')'));
			}
			// The string representing the enum is obtained
			javaStateName = javaStateName.substring(javaStateName.lastIndexOf('.') + 1);
			// The nullstate has no equivalent in SCTUnit
			if (javaStateName.contains("$NULLSTATE$"))
				return;
			// Ignores states not in the dictionary
			if (statesNames.containsKey(javaStateName)) {
				// The string representing the name of the state in SCTUnit is retrieved.
				String sctunitStateName = statechartName + "." + statesNames.get(javaStateName);
				testCase.addAssertState(sctunitStateName, assertTrue);
			}
		}
	}

	/**
	 * Check if the test case has errors, i.e. if a proceed statement is called
	 * after an exit statement
	 *
	 * @param testCase the test case to check
	 * @return true if there is an error, false otherwise
	 */
	private boolean hasErrors(TestCase testCase) {
		boolean afterExit = false;
		for (Action a : testCase.getActions()) {
			if (a.getExit() != null)
				afterExit = true;
			else if (a.getEnter() != null)
				afterExit = false;
			else if ((a.getProceed() != null || a.getTimeUnit() != null) && afterExit) {
				return true;
			}
		}
		return false;
	}

}
