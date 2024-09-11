package createst.junit.reading;

import java.util.LinkedList;
import java.util.List;

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
	 * Add an "enter" action.
	 */
	public void addEnter() {
		actions.add(new Action(true, false, null, null, null, false, false, false, false, null, null, false));
	}
	
	/**
	 * Add an "exit" action.
	 */
	public void addExit() {
		actions.add(new Action(false, true, null, null, null, false, false, false, false, null, null, false));
	}

	/**
	 * Add a "raise event" action.
	 *
	 * @param event the event to be raised
	 */
	public void addEvent(String event) {
		actions.add(new Action(false, false, event, null, null, false, false, false, false, null, null, false));
	}

	/**
	 * Add a "raise event: value" action.
	 *
	 * @param event the event to be raised
	 * @param value the value of the raised typed event
	 */
	public void addTypedEvent(String event, String value) {
		actions.add(new Action(false, false, event, value, null, false, false, false, false, null, null, false));
	}

	/**
	 * Add an "assert active (state)" action.
	 *
	 * @param state      the state to assert if it is active
	 * @param assertTrue true if the truth is wanted to be asserted, false if the
	 *                   falseness is wanted to be asserted
	 */
	public void addAssertState(String state, boolean assertTrue) {
		actions.add(new Action(false, false, null, null, state, false, false, assertTrue, false, null, null, false));
	}

	/**
	 * Adds an "assert is_active" action.
	 *
	 * @param assertTrue true if the truth is wanted to be asserted, false if the
	 *                   falseness is wanted to be asserted
	 */
	public void addIsActive(boolean assertTrue) {
		actions.add(new Action(false, false, null, null, null, true, false, assertTrue, false, null, null, false));
	}

	/**
	 * Adds an "assert is_final" action.
	 *
	 * @param assertTrue true if the truth is wanted to be asserted, false if the
	 *                   falseness is wanted to be asserted
	 */
	public void addIsFinal(boolean assertTrue) {
		actions.add(new Action(false, false, null, null, null, false, true, assertTrue, false, null, null, false));
	}

	/**
	 * Adds a "proceed 1 cycle" action.
	 */
	public void addProceedCycle() {
		actions.add(new Action(false, false, null, null, null, false, false, false, true, null, null, false));
	}

	/**
	 * Adds a "proceed value timeUnit" action.
	 * 
	 * @param value    the value to proceed
	 * @param timeUnit the time unit (s, ms, us or ns)
	 */
	public void addProceedTime(String value, String unit) {
		actions.add(new Action(false, false, null, null, null, false, false, false, false, value, unit, false));
	}

	/**
	 * Add a "triggerWithoutEvent" action.
	 */
	public void addTriggerWithoutEvent() {
		actions.add(new Action(false, false, null, null, null, false, false, false, false, null, null, true));
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

}
