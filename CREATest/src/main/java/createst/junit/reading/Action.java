package createst.junit.reading;

import java.util.Objects;

/**
 * The Class Action.
 */
public class Action {

	private String mockOperation;
	private String mockValue;
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
	 * Instantiates a new action. The Strings that are not needed for the action must
	 * be null
	 *
	 * @param mockOperation       the operation to be mocked
	 * @param mockValue		      the value used for mocking
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
	 * @param proceed             the number of cycles to be proceeded associated
	 *                            with the proceed cycle event
	 * @param timeValue           the value associated with the proceed time event
	 * @param timeUnit            the time unit associated with the proceed time
	 *                            event
	 * @param triggerWithoutEvent true if the action is "triggerWithoutEvent", false
	 *                            otherwise
	 */
	public Action(String mockOperation, String mockValue, boolean enter, boolean exit, String event, String eventValue, String state, boolean isActive,
			boolean isFinal, boolean assertTrue, String proceed, String timeValue, String timeUnit,
			boolean triggerWithoutEvent) {
		this.mockOperation = mockOperation;
		this.mockValue = mockValue;
		this.enter = enter ? "" : null;
		this.exit = exit ? "" : null;
		this.event = event;
		this.eventValue = eventValue;
		this.state = state;
		this.isActive = isActive ? "" : null;
		this.isFinal = isFinal ? "" : null;
		this.not = assertTrue ? null : "";
		this.proceed = proceed;
		this.timeValue = timeValue;
		this.timeUnit = timeUnit;
		this.triggerWithoutEvent = triggerWithoutEvent ? "" : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		return Objects.equals(mockOperation, other.mockOperation) && Objects.equals(mockValue, other.mockValue)
				&& Objects.equals(enter, other.enter) && Objects.equals(event, other.event)
				&& Objects.equals(eventValue, other.eventValue) && Objects.equals(exit, other.exit)
				&& Objects.equals(isActive, other.isActive) && Objects.equals(isFinal, other.isFinal)
				&& Objects.equals(not, other.not) && Objects.equals(proceed, other.proceed)
				&& Objects.equals(state, other.state) && Objects.equals(timeUnit, other.timeUnit)
				&& Objects.equals(timeValue, other.timeValue)
				&& Objects.equals(triggerWithoutEvent, other.triggerWithoutEvent);
	}
	
	/**
	 * Gets the mock operation string.
	 *
	 * @return the string
	 */
	public String getMockOperation() {
		return mockOperation;
	}

	/**
	 * Gets the mock value string.
	 *
	 * @return the string
	 */
	public String getMockValue() {
		return mockValue;
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
