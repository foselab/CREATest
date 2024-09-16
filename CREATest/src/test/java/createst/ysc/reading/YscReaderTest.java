package createst.ysc.reading;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class YscReaderTest {
	private static final String RESOURCES_DIR = "src\\test\\resources";
	private static final String FILE_NAME = "Yscreading_Statechart.ysc";
	private static final String STATECHART_NAME = "SctName";
	
	private static IYscReader reader;
	
	@Test(expected = NullPointerException.class)
	public void testNullInput() throws ParserConfigurationException, SAXException, IOException {
		reader = new YscReader(null);
	}

	@Test(expected = SAXException.class)
	public void testEmptyInput() throws ParserConfigurationException, SAXException, IOException {
		reader = new YscReader("");
	}
	
	@Test(expected = IOException.class)
	public void testNotExistingInput() throws ParserConfigurationException, SAXException, IOException {
		reader = new YscReader(RESOURCES_DIR + "\\" + "NotExistingStatechart.ysc");
	}
	
	@Test
	public void testCorrectInput() throws ParserConfigurationException, SAXException, IOException {
		reader = new YscReader(RESOURCES_DIR + "\\" + FILE_NAME);
		
		String expectedStatechartName = STATECHART_NAME;
		assertEquals(expectedStatechartName, reader.getStatechartName());
		
		Map<String, String> expectedStatesNames = new HashMap<String, String>();
		expectedStatesNames.put("MAIN_REGION_STATEA", "main_region.StateA");
		expectedStatesNames.put("MAIN_REGION_STATEB", "main_region.stateB");
		expectedStatesNames.put("MAIN_REGION_STATEB_R1_STATE_C", "main_region.stateB.r1.state_C");
		expectedStatesNames.put("MAIN_REGION_STATEB_R2_STATE_E", "main_region.stateB.r2.state_e");
		expectedStatesNames.put("MAIN_REGION_STATEB_R2_STATE_E_EMPTY_REGION_STATEF", "main_region.stateB.r2.state_e.empty_region.STATEF");
		expectedStatesNames.put("MAIN_REGION_STATEB_R2_STATE_D", "main_region.stateB.r2.state_d");
		expectedStatesNames.put("MAIN_REGION__FINAL", "main_region._Final");
		expectedStatesNames.put("SECONDARY_REGION_STATE1", "Secondary_Region.state1");
		expectedStatesNames.put("SECONDARY_REGION_STATE1_IN_REGION_STATE2", "Secondary_Region.state1.in_region.state2");
		expectedStatesNames.put("SECONDARY_REGION__FINAL_", "Secondary_Region._final_");
		assertEquals(expectedStatesNames, reader.getStatesNames());
		
		Map<String, String> expectedInterfacesNames = new HashMap<String, String>();
		expectedInterfacesNames.put("Interface1", "Interface1");
		expectedInterfacesNames.put("Interface_2", "interface_2");
		assertEquals(expectedInterfacesNames, reader.getInterfacesNames());
		
		Map<String, String> expectedEventsNames = new HashMap<String, String>();
		expectedEventsNames.put("raiseEventA", "eventA");
		expectedEventsNames.put("raiseEventB", "EventB");
		expectedEventsNames.put("raiseEvent_C", "Event_C");
		expectedEventsNames.put("raiseEventD", "EventD");
		expectedEventsNames.put("raiseEventE", "EventE");
		assertEquals(expectedEventsNames, reader.getEventsNames());
	}

}
