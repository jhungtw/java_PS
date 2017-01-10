package testcase;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import basic.config.Report;
import basic.util.Tool;

public class ToolTestCase {
	
@Test
	
	public void testDisableIrrelevantReports(){
	
	Map<String, Report> reports = new HashMap<String, Report> ();
	
	Report r1 = new Report();
	r1.setName("r1");
	r1.setEnabled(true);
	r1.setFrequency("MONTH");
	
	Report r2 = new Report();
	r2.setName("r1");
	r2.setEnabled(true);
	r2.setFrequency("WEEK");
	
	reports.put("r1", r1);
	reports.put("r2", r2);
	System.out.println("--->"+ reports.toString());
	
	System.out.println(Tool.disableIrrelevantReports(reports));
}
	
@Test
@Ignore
	public void testAddStatusForRerun() {
	
	try {
		//Tool.addStatusForRerun("R001", "test1", true, "c:\\tmp\\t1.log");
		//Tool.addStatusForRerun("R002", "test2", true, "c:\\tmp\\t1.log");
		Tool.addStatusForRerun("R003", "test3", true, "c:\\tmp\\t1.log");
		Tool.addStatusForRerun("R004", "test4", true, "c:\\tmp\\t1.log");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}
	
	
	@Test
	@Ignore
	public void testGetReportsForRerun() {
		try {
			System.out.println("not done reports: "+Tool.getReportsForRerun("c:\\tmp\\auto.yaml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	
	@Test
	@Ignore
	public void testGetFirstDayOfLastMonth() {
		System.out.println("getFirstDayOfLastMonth: "+Tool.getFirstDayOfLastMonth("YYYY-MM-dd"));
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public void testGetFirstDayOfThisMonth() {
		System.out.println("testGetFirstDayOfThisMonth: "+Tool.getFirstDayOfThisMonth("YYYY-MM-dd"));
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetOutputFilePath() {
		System.out.println("testGetFirstDayOfLastWeek: "+Tool.getOutputFilePath("R001","Delivery types ","c:\\tmp"));
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public void testGetFirstDayOfLastWeek() {
		System.out.println("testGetFirstDayOfLastWeek: "+Tool.getFirstDayOfLastWeek("YYYY-MM-dd"));
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public void testGetFirstDayOfThisWeek() {
		System.out.println("testGetFirstDayOfThisWeek: "+Tool.getFirstDayOfThisWeek("YYYY-MM-dd"));
		fail("Not yet implemented");
	}
	
	@Test
	@Ignore
	public void testAddQueryInterval() {
		System.out.println(
				"testAddQueryInterval: "+
		Tool.addQueryInterval("select count(*) from {twmcustomer} where {creationtime} > '[from]' and {creationtime} < '[to]'",
				Tool.getFirstDayOfLastWeek("YYYY-MM-dd"), Tool.getFirstDayOfThisWeek("YYYY-MM-dd")));
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetFinalDayOfLastWeek() {
		System.out.println("testGetFinalDayOfLastWeek: "+Tool.getFinalDayOfLastWeek("YYYY-MM-dd"));
		fail("Not yet implemented");
	}

}
