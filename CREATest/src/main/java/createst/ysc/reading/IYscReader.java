package createst.ysc.reading;

import java.util.Map;

public interface IYscReader {
	
	boolean hasNamespace();

	public String getStatechartName();

	public Map<String, String> getStatesNames();

	public Map<String, String> getEventsNames();

	public Map<String, String> getInterfacesNames();

}
