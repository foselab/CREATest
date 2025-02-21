package createst.ysc.reading;

import java.util.List;
import java.util.Map;

public interface IYscReader {
	
	/**
	 * Checks if the statechart has a namespace.
	 *
	 * @return true, if the statechart has a namespace, false otherwise 
	 * 
	 */
	public boolean hasNamespace();
	
	/**
	 * Gets the list of the imported sub-machines.
	 *
	 * @return the list of the imported sub-machines
	 */
	public List<String> getImportedSubMachines();

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
