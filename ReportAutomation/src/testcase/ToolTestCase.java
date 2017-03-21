package testcase;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import com.mysql.jdbc.Driver;
import java.util.Properties;

import javax.mail.Session;
import com.mysql.jdbc.Driver;
import org.junit.Ignore;
import org.junit.Test;

import basic.config.Report;
import basic.util.Tool;
import net.lingala.zip4j.exception.ZipException;

public class ToolTestCase {
	
	
	@Test
	@Ignore
	public void testFileNameByFormat() {

		System.out.println("sile name is : "+Tool.getOutputFilePathWithFormat("psteam[yyyy-MM-dd hh:mm:ss]_test", "tetsreport yyyy", "c:\\tmp", "csv"));
	}
	
	@Test
	@Ignore
	public void testSMTP() {

		Tool.sendEmailByTWMSmtp("jhung@totalwine.com", "jhung@totalwine.com,jerry110160@gmail.com",
				"jhung@totalwine.com,jerry110160@gmail.com", "XXX", "c:\\tmp\\testyaml");
	}

	@Test
	@Ignore
	public void testSaveAsCSV() {
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = null;

			conn = DriverManager.getConnection("jdbc:mysql://localhost/sakila?" + "user=jhung&password=hhj1101");
			Statement stmt = conn.createStatement();
		    rs = stmt.executeQuery("SELECT * FROM sakila.actor");
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		try {
			Tool.saveResultsetToCSV(rs, "c:\\tmp\\ddd.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test

	public void testCopyFiles() throws ZipException {
		try {
			Tool.copyFilesToFtpFolder("ftp.totalwine.com", "jhung", "Hhj050460@", "c:\\tmp\\psteam2017-03-03.csv", "psteam2017-03-03.csv",
					"/jerrytmp/");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void testPack() {
		try {
			System.out.println("file name is  " + Tool.packLocal("c:\\tmp\\QtyDiff.dat", "test"));
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void testDisableIrrelevantReports() {

		Map<String, Report> reports = new HashMap<String, Report>();

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
		System.out.println("--->" + reports.toString());

		System.out.println(Tool.disableIrrelevantReports(reports));
	}

	@Test
	@Ignore
	public void testAddStatusForRerun() {

		try {
			// Tool.addStatusForRerun("R001", "test1", true, "c:\\tmp\\t1.log");
			// Tool.addStatusForRerun("R002", "test2", true, "c:\\tmp\\t1.log");
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
			System.out.println("not done reports: " + Tool.getReportsForRerun("c:\\tmp\\auto.yaml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	@Ignore
	public void testGetFirstDayOfLastMonth() {
		System.out.println("getFirstDayOfLastMonth: " + Tool.getFirstDayOfLastMonth("YYYY-MM-dd"));
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetFirstDayOfThisMonth() {
		System.out.println("testGetFirstDayOfThisMonth: " + Tool.getFirstDayOfThisMonth("YYYY-MM-dd"));
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetOutputFilePath() {
		System.out
				.println("testGetFirstDayOfLastWeek: " + Tool.getOutputFilePath("R001", "Delivery types ", "c:\\tmp",null));
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetFirstDayOfLastWeek() {
		System.out.println("testGetFirstDayOfLastWeek: " + Tool.getFirstDayOfLastWeek("YYYY-MM-dd"));
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetFirstDayOfThisWeek() {
		System.out.println("testGetFirstDayOfThisWeek: " + Tool.getFirstDayOfThisWeek("YYYY-MM-dd"));
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testAddQueryInterval() {
		System.out.println("testAddQueryInterval: " + Tool.addQueryInterval(
				"select count(*) from {twmcustomer} where {creationtime} > '[from]' and {creationtime} < '[to]'",
				Tool.getFirstDayOfLastWeek("YYYY-MM-dd"), Tool.getFirstDayOfThisWeek("YYYY-MM-dd")));
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetFinalDayOfLastWeek() {
		System.out.println("testGetFinalDayOfLastWeek: " + Tool.getFinalDayOfLastWeek("YYYY-MM-dd"));
		fail("Not yet implemented");
	}

}
