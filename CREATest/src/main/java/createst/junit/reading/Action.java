package createst.junit.reading;

/**
 * The Class Action.
 */
public class Action {

	private String enter;
	private String exit;
	private String event;
	private String eventValue;
	private String state;
	private String isActive;
	private String isFinal;
	private String not;
	private String proceed;
	private String timeValue;
	private String timeUnit;
	private String triggerWithoutEvent;

	/**
	 * Instantiates a new action. The Strings that are not needed for the acton must
	 * be null
	 *
	 * @param enter               true if the action is "enter", false otherwise
	 * @param exit                true if the action is "exit", false otherwise
	 * @param event               the event to be raised
	 * @param eventValue          the value associated with the raised typed event
	 * @param state               the state to assert if it is active
	 * @param isActive            true if the action is "assert is_active", false
	 *                            otherwise
	 * @param isFinal             true if the action is "assert is_final", false
	 *                            otherwise
	 * @param assertTrue          true if the truth is wanted to be asserted, false
	 *                            if the falseness is wanted to be asserted
	 * @param proceed             true if the action is "proceed 1 cycle", false
	 *                            otherwise
	 * @param timeValue           the value associated with the proceed time event
	 * @param timeUnit            the time unit associated with the proceed time
	 *                            event
	 * @param triggerWithoutEvent true if the action is "triggerWithoutEvent", false
	 *                            otherwise
	 */
	public Action(boolean enter, boolean exit, String event, String eventValue, String state, boolean isActive, boolean isFinal,
			boolean assertTrue, boolean proceed, String timeValue, String timeUnit, boolean triggerWithoutEvent) {
		this.enter = enter ? "" : null;
		this.exit = exit ? "" : null;
		this.event = event;
		this.eventValue = eventValue;
		this.state = state;
		this.isActive = isActive ? "" : null;
		this.isFinal = isFinal ? "" : null;
		this.not = assertTrue ? null : "";
		this.proceed = proceed ? "" : null;
		this.timeValue = timeValue;
		this.timeUnit = timeUnit;
		this.triggerWithoutEvent = triggerWithoutEvent ? "" : null;
	}

	/**
	 * Gets the enter string.
	 *
	 * @return the string
	 */
	public String getEnter() {
		return enter;
	}
	
	/**
	 * Gets the exit string.
	 *
	 * @return the string
	 */
	public String getExit() {
		return exit;
	}

	/**
	 * Gets the event string.
	 *
	 * @return the string
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * Gets the string representing the value of the event.
	 *
	 * @return the string
	 */
	public String getEventValue() {
		return eventValue;
	}

	/**
	 * Gets the state string.
	 *
	 * @return the string
	 */
	public String getState() {
		return state;
	}

	/**
	 * Gets the is active string.
	 *
	 * @return the string
	 */
	public String getIsActive() {
		return isActive;
	}

	/**
	 * Gets the is final string.
	 *
	 * @return the string
	 */
	public String getIsFinal() {
		return isFinal;
	}

	/**
	 * Gets the not string.
	 *
	 * @return the string
	 */
	public String getNot() {
		return not;
	}

	/**
	 * Gets the proceed string.
	 *
	 * @return the string
	 */
	public String getProceed() {
		return proceed;
	}

	/**
	 * Gets the time value string.
	 *
	 * @return the string
	 */
	public String getTimeValue() {
		return timeValue;
	}

	/**
	 * Gets the time unit string.
	 *
	 * @return the string
	 */
	public String getTimeUnit() {
		return timeUnit;
	}

	/**
	 * Gets the trigger without event string.
	 *
	 * @return the string
	 */
	public String getTriggerWithoutEvent() {
		return triggerWithoutEvent;
	}

}
