package createst.junit.reading;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The Class TestCase.
 */
public class TestCase {

	private String name;
	private List<Action> actions;

	/**
	 * Instantiate a new test case.
	 *
	 * @param name the name of the test case
	 */
	public TestCase(String name) {
		this.name = name;
		actions = new LinkedList<Action>();
	}
	
	/**
	 * Add a "mock operation: (value)" action.
	 *
	 * @param operation the operation to be mocked
	 * @param value the value for mocking
	 */
	public void addMock(String operation, String value) {
		actions.add(new Action(operation, value, true, false, null, null, null, false, false, false, null, null, null, false));
	}

	/**
	 * Add an "enter" action.
	 */
	public void addEnter() {
		actions.add(new Action(null, null, true, false, null, null, null, false, false, false, null, null, null, false));
	}
	
	/**
	 * Add an "exit" action.
	 */
	public void addExit() {
		actions.add(new Action(null, null, false, true, null, null, null, false, false, false, null, null, null, false));
	}

	/**
	 * Add a "raise event" action.
	 *
	 * @param event the event to be raised
	 */
	public void addEvent(String event) {
		actions.add(new Action(null, null, false, false, event, null, null, false, false, false, null, null, null, false));
	}

	/**
	 * Add a "raise event: value" action.
	 *
	 * @param event the event to be raised
	 * @param value the value of the raised typed event
	 */
	public void addTypedEvent(String event, String value) {
		actions.add(new Action(null, null, false, false, event, value, null, false, false, false, null, null, null, false));
	}

	/**
	 * Add an "assert active (state)" action.
	 *
	 * @param state      the state to assert if it is active
	 * @param assertTrue true if the truth is wanted to be asserted, false if the
	 *                   falseness is wanted to be asserted
	 */
	public void addAssertState(String state, boolean assertTrue) {
		actions.add(new Action(null, null, false, false, null, null, state, false, false, assertTrue, null, null, null, false));
	}

	/**
	 * Adds an "assert is_active" action.
	 *
	 * @param assertTrue true if the truth is wanted to be asserted, false if the
	 *                   falseness is wanted to be asserted
	 */
	public void addIsActive(boolean assertTrue) {
		actions.add(new Action(null, null, false, false, null, null, null, true, false, assertTrue, null, null, null, false));
	}

	/**
	 * Adds an "assert is_final" action.
	 *
	 * @param assertTrue true if the truth is wanted to be asserted, false if the
	 *                   falseness is wanted to be asserted
	 */
	public void addIsFinal(boolean assertTrue) {
		actions.add(new Action(null, null, false, false, null, null, null, false, true, assertTrue, null, null, null, false));
	}

	/**
	 * Adds a "proceed 1 cycle" action.
	 */
	public void addProceedCycles(String nCycles) {
		actions.add(new Action(null, null, false, false, null, null, null, false, false, false, nCycles, null, null, false));
	}

	/**
	 * Adds a "proceed value timeUnit" action.
	 * 
	 * @param value    the value to proceed
	 * @param timeUnit the time unit (s, ms, us or ns)
	 */
	public void addProceedTime(String value, String unit) {
		actions.add(new Action(null, null, false, false, null, null, null, false, false, false, null, value, unit, false));
	}

	/**
	 * Add a "triggerWithoutEvent" action.
	 */
	public void addTriggerWithoutEvent() {
		actions.add(new Action(null, null, false, false, null, null, null, false, false, false, null, null, null, true));
	}

	/**
	 * To string.
	 *
	 * @return the string representing the name of the test case
	 */
	public String toString() {
		return name;
	}

	/**
	 * Get the actions.
	 *
	 * @return the list of actions
	 */
	public List<Action> getActions() {
		return actions;
	}
	
	/**
	 * Returns true if the test case is empty
	 *
	 * @return true if the test case is empty, false otherwise
	 */
	public boolean isEmpty() {
		return actions.isEmpty();
	}
	
	/**
	 * Returns true if the test case has the same content of the input one <p>
	 * IT IS NOT MEANT TO BE THE OVERWRITTEN Object.equals(Object o) METHOD
	 *
	 * @return true if the test cases have the same content, false otherwise
	 */
	public boolean sameContentOf(TestCase other) {
	    return Objects.equals(this.actions, other.actions);
	}

}
