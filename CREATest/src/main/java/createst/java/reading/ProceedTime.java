package createst.java.reading;

/**
 * The Class ProceedTime.
 */
public class ProceedTime {
		
	/** The value of the time event. */
	private long value;
	
	/** The time unit of the time event. */
	private TimeUnit unit;
	
	/**
	 * Instantiates a new proceed time.
	 *
	 * @param value the value
	 * @param unit the time unit
	 */
	public ProceedTime(long value, TimeUnit unit) {
		this.value = value;
		this.unit = unit;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Gets the time unit.
	 *
	 * @return the time unit
	 */
	public TimeUnit getUnit() {
		return unit;
	}
	
	

}
