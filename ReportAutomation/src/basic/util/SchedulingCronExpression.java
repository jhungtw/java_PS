package basic.util;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.Trigger;

public class SchedulingCronExpression implements SchedulingStrategy {

	@Override
	public Trigger generateTriggger(String triggername,String group,String value) {
		// TODO Auto-generated method stub
		
		
		return newTrigger().withIdentity(triggername, group)
				.withSchedule(cronSchedule(value)).build();
	}

}
