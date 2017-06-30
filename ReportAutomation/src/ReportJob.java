
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.*;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Date;

import org.quartz.JobKey;

import basic.util.EmailUtils;
import basic.util.FileTransferUtils;
import basic.util.JDBCUtils;
import basic.util.Tool;

public class ReportJob implements Job {
	private static Logger accessLog = Logger.getLogger("ReportAutomationLog");
	// private static Logger _log = LoggerFactory.getLogger(SimpleJob.class);

	/**
	 * Quartz requires a public empty constructor so that the scheduler can
	 * instantiate the class whenever it needs.
	 */
	public ReportJob() {
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		
		Connection connection = null;

		Statement stmt = null;

		ResultSet rs = null;

		// This job simply prints out its job name and the
		// date and time that it is running
		JobKey jobKey = context.getJobDetail().getKey();
		JobDataMap data = context.getMergedJobDataMap();
		
		accessLog.info("SimpleJob says: " + jobKey + " executing at " + new Date() + data.getString("query"));

		// new ReportExecution().run(data.getString("query"));

		try {
			String HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";

			String HYBRIS_DRIVER_URL = JDBCUtils.getHybrisDriverURL(data.getString("hybris.env"));

			String HYBRIS_USER = data.getString("hybris.user");

			String HYBRIS_PASSWORD = data.getString("hybris.password");

			String query = null;
			DateTime dt = new DateTime();
			switch (data.getString("report.frequency").toUpperCase()) {
			case "DAILY":
				System.out.println("daily job");
				System.out.println("XXXXXX" + new DateTime(dt.getYear(), dt.getMonthOfYear(), 1, 0, 0, 0).minusDays(1)
						.toString(DateTimeFormat.forPattern("YYYY-MM-dd")));
				System.out.println("XXXXXX" + new DateTime(dt.getYear(), dt.getMonthOfYear(), 1, 0, 0, 0)
						.toString(DateTimeFormat.forPattern("YYYY-MM-dd")));

				query = JDBCUtils.addQueryInterval(data.getString("query"),
						new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), 0, 0, 0).minusDays(1)
								.toString(DateTimeFormat.forPattern("YYYY-MM-dd")),
						new DateTime(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth(), 0, 0, 0)
								.toString(DateTimeFormat.forPattern("YYYY-MM-dd")));
				break;
			case "MONTH":

				System.out.println("XXXXXX" + new DateTime(dt.getYear(), dt.getMonthOfYear(), 1, 0, 0, 0).minusMonths(1)
						.toString(DateTimeFormat.forPattern("YYYY-MM-dd")));
				System.out.println("XXXXXX" + new DateTime(dt.getYear(), dt.getMonthOfYear(), 1, 0, 0, 0)
						.toString(DateTimeFormat.forPattern("YYYY-MM-dd")));

				query = JDBCUtils.addQueryInterval(data.getString("query"),
						new DateTime(dt.getYear(), dt.getMonthOfYear(), 1, 0, 0, 0).minusMonths(1)
								.toString(DateTimeFormat.forPattern("YYYY-MM-dd")),
						new DateTime(dt.getYear(), dt.getMonthOfYear(), 1, 0, 0, 0)
								.toString(DateTimeFormat.forPattern("YYYY-MM-dd")));
				break;
			case "WEEK":
				query = JDBCUtils.addQueryInterval(data.getString("query"),
						new DateTime().minusDays(7).toString(DateTimeFormat.forPattern("YYYY-MM-dd")),
						new DateTime().toString(DateTimeFormat.forPattern("YYYY-MM-dd")));
				// need fix

				/*
				 * query = Tool.addQueryInterval(data.getString("query"),
				 * Tool.getFirstDayOfLastWeek("YYYY-MM-dd"),
				 * Tool.getFirstDayOfThisWeek("YYYY-MM-dd"));
				 * 
				 * 
				 */

				break;
			default:

				break;
			}

			accessLog.info("Fexiable query is : " + query);

			Class.forName(HYBRIS_DRIVER_CLASS).newInstance();

			connection = DriverManager.getConnection(HYBRIS_DRIVER_URL, HYBRIS_USER, HYBRIS_PASSWORD);

			stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				    ResultSet.CONCUR_READ_ONLY);

			rs = stmt.executeQuery(query);

			accessLog.info("Search is done ");

			// ***********************************************************
			// implement save file
			// ***********************************************************
			// generate output file name
			String filepath = "";
			System.out.println("output.filename is " + data.getString("output.filename"));
			boolean EMPTY_RESULTSET = Tool.isResultEmpty(rs);
			
			boolean PROCESS_DELIVERY= Tool.processDeliveryActions(EMPTY_RESULTSET, data.getBoolean("return.empty"));
			System.out.println(data.getBoolean("return.empty") +"///"+!EMPTY_RESULTSET+"///"+PROCESS_DELIVERY);

			if (PROCESS_DELIVERY) {

				if (StringUtils.isNotEmpty(data.getString("output.filename"))) {
					if (data.getString("output.format").equalsIgnoreCase("csv")) {
						filepath = Tool.getOutputFilePathWithFormat(data.getString("output.filename"),
								data.getString("name"), data.getString("temp.folder"), "csv");

					}
					if (data.getString("output.format").equalsIgnoreCase("excel")) {
						filepath = Tool.getOutputFilePathWithFormat(data.getString("output.filename"),
								data.getString("name"), data.getString("temp.folder"), "xlsx");

					}
				} else {
					filepath = Tool.getOutputFilePath(data.getString("report.key"), data.getString("report.name"),
							data.getString("temp.folder"), data.getString("output.format"));
				}
				// save as excel or csv
				switch (data.getString("output.format").toUpperCase()) {
				case "EXCEL": {

					if (data.getBooleanValue("password.protected")) {
						JDBCUtils.saveResultsetToExcelWithPassword(data.getString("report.name"), rs, filepath,
								data.getString("password"));
						accessLog.info("Saved output as " + filepath);

					} else {
						JDBCUtils.saveResultsetToExcel(data.getString("report.name"), rs, filepath);
						accessLog.info("No password--Saved output as " + filepath);
					}
					break;
				}

				case "CSV": {

					JDBCUtils.saveResultsetToCSV(rs, filepath);
					accessLog.info("Save AS CSV--Saved output as " + filepath);

					break;
				}
				default:

					break;
				}

				// send email?

				if (data.getBooleanValue("email.notification")) {
					EmailUtils.sendEmailByTWMSmtp(data.getString("smtp.user"), data.getString("email.to"),
							data.getString("email.cc"), data.getString("email.extracontent"),data.getString("report.name"), filepath);

					accessLog.info("Send email is done ");
				}

				// ftp file?

				if (data.getBooleanValue("ftp.notification")) {
					System.out.println("before ftp: " + data.getString("ftp.host") + "--" + filepath);
					accessLog.info("ftp is starting ");
					Path p = Paths.get(filepath);
					String remotefile = p.getFileName().toString();
					System.out.println("host:" + data.getString("ftp.host") + "//filepath:" + filepath + "//remotefile:"
							+ remotefile + "//remotefolder:" + data.getString("ftp.folder"));

					if (data.getString("ftp.port").equalsIgnoreCase("22")) {
						// ftp over ssh
						FileTransferUtils.copyFilesToFtpFolderOverSSH(data.getString("ftp.host"), data.getString("ftp.username"),
								data.getString("ftp.password"), filepath, remotefile, data.getString("ftp.folder"));

					} else {
						// normal ftp
						FileTransferUtils.copyFilesToFtpFolder(data.getString("ftp.host"), data.getString("ftp.username"),
								data.getString("ftp.password"), filepath, remotefile, data.getString("ftp.folder"));

					}

					accessLog.info("ftp is done ");
				}

				// backup file?

				if (data.getBooleanValue("backup.notification")) {
					System.out.println("before backup: " + data.getString("ftp.host") + "--" + filepath);
					accessLog.info("backup is starting ");
					File source = new File(filepath);

					Path p = Paths.get(filepath);
					String remotefile = p.getFileName().toString();
					System.out.println("--->" + data.getString("backup.folder") + "\\" + remotefile);
					File dest = new File(data.getString("backup.folder") + "\\" + remotefile);
					FileUtils.copyFile(source, dest);

					accessLog.info("backup is done ");
				}

			}

		} catch (Exception e) {
			// implement log job status is not done

			// implement log job status is not done
			e.printStackTrace();
		}

		finally {
			// implement log job status is done

			// implement log job status is done
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}

		}
	}

}
