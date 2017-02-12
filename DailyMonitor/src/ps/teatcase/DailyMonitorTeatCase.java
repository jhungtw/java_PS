package ps.teatcase;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import ps.util.Tool;

public class DailyMonitorTeatCase {

	@Test
	@Ignore
	public void testIsCompletedToday() {
		try {
			Tool.AddControlFileforFulfillment();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	
	public void testAddControlFileforFulfillment() {
		System.out.println(Tool.isCompletedToday()); // TODO
	}

}
