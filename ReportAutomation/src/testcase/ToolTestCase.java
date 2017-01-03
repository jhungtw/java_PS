package testcase;

import static org.junit.Assert.*;

import org.junit.Test;

import basic.util.Tool;

public class ToolTestCase {
	@Test
	public void testGetFirstDayOfLastMonth() {
		System.out.println("getFirstDayOfLastMonth: "+Tool.getFirstDayOfLastMonth("YYYY-MM-dd"));
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetFirstDayOfThisMonth() {
		System.out.println("testGetFirstDayOfThisMonth: "+Tool.getFirstDayOfThisMonth("YYYY-MM-dd"));
		fail("Not yet implemented");
	}

	@Test
	public void testGetOutputFilePath() {
		System.out.println("testGetFirstDayOfLastWeek: "+Tool.getOutputFilePath("R001","Delivery types ","c:\\tmp"));
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetFirstDayOfLastWeek() {
		System.out.println("testGetFirstDayOfLastWeek: "+Tool.getFirstDayOfLastWeek("YYYY-MM-dd"));
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetFirstDayOfThisWeek() {
		System.out.println("testGetFirstDayOfThisWeek: "+Tool.getFirstDayOfThisWeek("YYYY-MM-dd"));
		fail("Not yet implemented");
	}
	
	@Test
	public void testAddQueryInterval() {
		System.out.println(
				"testAddQueryInterval: "+
		Tool.addQueryInterval("select count(*) from {twmcustomer} where {creationtime} > '[from]' and {creationtime} < '[to]'",
				Tool.getFirstDayOfLastWeek("YYYY-MM-dd"), Tool.getFirstDayOfThisWeek("YYYY-MM-dd")));
		fail("Not yet implemented");
	}

	@Test
	public void testGetFinalDayOfLastWeek() {
		System.out.println("testGetFinalDayOfLastWeek: "+Tool.getFinalDayOfLastWeek("YYYY-MM-dd"));
		fail("Not yet implemented");
	}

}
