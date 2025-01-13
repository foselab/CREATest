package createst.ysc.reading;

import java.util.Map;

public interface IYscReader {
	
	/**
	 * Checks if the statechart has a namespace.
	 *
	 * @return true, if the statechart has a namespace, false otherwise 
	 * 
	 */
	boolean hasNamespace();

	/**
	 * Gets the statechart name.
	 *
	 * @return the statechart name
	 */
	public String getStatechartName();

	/**
	 * Gets all states names.
	 *
	 * @return the dictionary containing all states names where the key is the
	 *         corresponding enum
	 */
	public Map<String, String> getStatesNames();

	/**
	 * Gets all events names.
	 *
	 * @return the list containing all events names where the key is the
	 *         corresponding method
	 */
	public Map<String, String> getEventsNames();

	/**
	 * Gets all interfaces names.
	 *
	 * @return the list containing all interfaces names where the key is the
	 *         corresponding class name
	 */
	public Map<String, String> getInterfacesNames();

}
