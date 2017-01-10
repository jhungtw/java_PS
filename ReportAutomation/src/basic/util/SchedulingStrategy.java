package basic.util;

import org.quartz.Trigger;

public interface SchedulingStrategy {
	
	
	public Trigger generateTriggger(String triggername,String group,String value); 

}
