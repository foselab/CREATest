package createst.junit.reading;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import createst.java.reading.ProceedTime;
import createst.java.reading.TimeUnit;
import createst.junit.reading.IJunitReader;
import createst.junit.reading.JunitReader;
import createst.junit.reading.TestCase;

public class JunitReaderTest {
	private static final String RESOURCES_DIR = "src\\test\\resources";
	private static final String FILE_NAME = "Junitreading_statechart_test.java";
	private static final String STATECHART_NAME = "Statechart";
	
	private static IJunitReader reader = new JunitReader();
	
	@Test(expected = NullPointerException.class)
	public void testNullInput() throws IOException {
		reader.getTestCases(null, null, null, null, null, null);
	}

	@Test(expected = IOException.class)
	public void testEmptyPath() throws IOException {
		String junitPath = "";
		reader.getTestCases(junitPath, null, null, null, null, null);
	}

	@Test
	public void testCorrectInput() throws IOException {
		String junitPath = RESOURCES_DIR + "\\" + FILE_NAME;
		String statechartName = STATECHART_NAME;
		
		Map<String, String> statesNames = new HashMap<String, String>();
		String state_a = "main_region.StateA";
		String state_b = "main_region.StateB";
		String state_d = "main_region.StateD";
		statesNames.put("MAIN_REGION_STATEA", state_a);
		statesNames.put("MAIN_REGION_STATEB", state_b);
		statesNames.put("MAIN_REGION_STATED", state_d);
		
		Map<String, String> eventsNames = new HashMap<String, String>();
		String event_1 = "event1";
		String event_2 = "event2";
		String long_e = "longE";
		String string_e = "stringE";
		String bool_e = "boolE";
		String double_e = "doubleE";
		eventsNames.put("raiseEvent1", event_1);
		eventsNames.put("raiseEvent2", event_2);
		eventsNames.put("raiseLongE", long_e);
		eventsNames.put("raiseStringE", string_e);
		eventsNames.put("raiseBoolE", bool_e);
		eventsNames.put("raiseDoubleE", double_e);
		
		Map<String, String> interfacesNames = new HashMap<String, String>();
		String intrfc = "intrfc";
		interfacesNames.put("Intrfc", intrfc);
		
		Map<Integer, ProceedTime> proceedTimes = new HashMap<Integer, ProceedTime>();
		String time_1 = "1";
		String time_2 = "5";
		String time_3 = "10";
		String time_4 = "50";
		String time_6 = "100";
		proceedTimes.put(1, new ProceedTime(Integer.parseInt(time_1), TimeUnit.SECONDS));
		proceedTimes.put(2, new ProceedTime(Integer.parseInt(time_2), TimeUnit.MILLISECONDS));
		proceedTimes.put(3, new ProceedTime(Integer.parseInt(time_3), TimeUnit.MICROSECONDS));
		proceedTimes.put(4, new ProceedTime(Integer.parseInt(time_4), TimeUnit.NANOSECONDS));
		proceedTimes.put(6, new ProceedTime(Integer.parseInt(time_6), TimeUnit.MILLISECONDS));
		
		List<TestCase> actual = reader.getTestCases(junitPath, statechartName, statesNames, eventsNames,
				interfacesNames, proceedTimes);
		
		List<TestCase> expected = new ArrayList<TestCase>();
		
		TestCase t0 = new TestCase("test00");
		t0.addEnter();
		t0.addEvent(event_1);
		t0.addEvent(intrfc + "." + event_2);
		t0.addTriggerWithoutEvent();
		t0.addExit();
		t0.addIsActive(true);
		t0.addAssertState(statechartName + "." + state_a, true);
		t0.addIsFinal(false);
		
		TestCase t2 = new TestCase("test02");
		t2.addEnter();
		t2.addTypedEvent(long_e, "10");
		t2.addTypedEvent(long_e, "-1337");
		t2.addTypedEvent(string_e, "\"str\"");
		t2.addTypedEvent(bool_e, "true");
		t2.addTypedEvent(double_e, "95.48");
		t2.addProceedCycle();
		t2.addAssertState(statechartName + "." + state_b, true);
		
		TestCase t3 = new TestCase("test03");
		t3.addEnter();
		t3.addProceedTime(time_1, "s");
		t3.addProceedTime(time_2, "ms");
		t3.addProceedTime(time_3, "us");
		t3.addProceedTime(time_4, "ns");
		t3.addTriggerWithoutEvent();
		
		expected.add(t0);
		expected.add(t2);
		expected.add(t3);
		
		assertThat(actual)
			.usingRecursiveComparison()
			.isEqualTo(expected);
	}

}
