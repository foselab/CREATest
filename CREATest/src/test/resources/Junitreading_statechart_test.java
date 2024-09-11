package statechart;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.junit.runner.RunWith;
import statechart.ExamplStcSimplified;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true, useJEE = true) 
public class Junitreading_statechart_test {

  @Test(timeout = 4000)
  public void test00()  throws Throwable  {
      Statechart statechart = new Statechart();
      Statechart.Intrfc intrfc = Statechart.intrfc();
      Statechart.Intrfc2 intrfc2 = Statechart.intrfc2();
      statechart.enter();
      statechart.raiseEvent1();
      intrfc.raiseEvent2();
      intrfc2.raiseEvent();
      statechart.triggerWithoutEvent();
      statechart.exit();
      statechart.State statechart_State0 = statechart.State.MAIN_REGION_STATEA;
      boolean boolean0 = statechart.isStateActive(statechart_State0);
      assertTrue(statechart.isActive());
      assertTrue(boolean0);
      assertFalse(statechart.isFinal());
  }

  @Test(timeout = 4000)
  public void test01()  throws Throwable  {
	  Statechart statechart = new Statechart();
      try { 
    	  statechart.isStateActive((statechart.State) null);
        fail("Expecting exception: NullPointerException");
      } catch(NullPointerException e) {
         verifyException("statechart.ExamplStcSimplified", e);
      }
  }
  
  @Test(timeout = 4000)
  public void test02()  throws Throwable  {
	  Statechart statechart = new Statechart();
      statechart.enter();
      statechart.raiseLongE(10L);
      statechart.raiseLongE((-1337L));
      statechart.raiseStringE((String) null);
      statechart.raiseStringE("str");
      statechart.raiseBoolE(true);
      statechart.raiseDoubleE(95.48);
      statechart.runCycle();
      assertTrue(statechart.isStateActive(statechart.State.MAIN_REGION_STATEB));
  }
  
  @Test(timeout = 4000)
  public void test03()  throws Throwable  {
	  Statechart statechart = new Statechart();
      ITimerService iTimerService0 = mock(ITimerService.class, new ViolatedAssumptionAnswer());
      statechart.setTimerService(iTimerService0);
      statechart.enter();
      statechart.raiseTimeEvent(1);
      statechart.raiseTimeEvent(2);
      statechart.raiseTimeEvent(3);
      statechart.raiseTimeEvent(4);
      statechart.raiseTimeEvent(5);
      statechart.triggerWithoutEvent();
      assertFalse(statechart.isStateActive(statechart.State.valueof(MAIN_REGION_STATEC)));
  }
  
  
}
