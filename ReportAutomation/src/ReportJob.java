import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

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

			String HYBRIS_DRIVER_URL = Tool.getHybrisDriverURL(data.getString("hybris.env"));

			String HYBRIS_USER = data.getString("hybris.user");

			String HYBRIS_PASSWORD = data.getString("hybris.password");

			String query = null;
			switch (data.getString("report.frequency")) {
			case "MONTH":
				query = Tool.addQueryInterval(data.getString("query"), Tool.getFirstDayOfLastMonth("YYYY-MM-dd"),
						Tool.getFirstDayOfThisMonth("YYYY-MM-dd"));
				break;
			case "WEEK":
				query = Tool.addQueryInterval(data.getString("query"), Tool.getFirstDayOfLastWeek("YYYY-MM-dd"),
						Tool.getFirstDayOfThisWeek("YYYY-MM-dd"));
				break;
			default:

				break;
			}

			accessLog.info("Fexiable query is : " + query);

			Class.forName(HYBRIS_DRIVER_CLASS).newInstance();

			connection = DriverManager.getConnection(HYBRIS_DRIVER_URL, HYBRIS_USER, HYBRIS_PASSWORD);

			stmt = connection.createStatement();

			rs = stmt.executeQuery(query);

			accessLog.info("Search is done ");

			// ***********************************************************
			// implement save file
			// ***********************************************************

			String filepath = Tool.getOutputFilePath(data.getString("report.key"), data.getString("report.name"),
					data.getString("temp.folder"));

			Tool.saveResultsetToExcel(data.getString("report.name"), rs, filepath);
			accessLog.info("Saved output as " + filepath);

			/*
			 * rs.close(); stmt.close(); connection.close();
			 */
			Tool.sendNotification(data.getString("smtp.user"), data.getString("smtp.password"),
					data.getString("email.to"), data.getString("email.cc"), data.getString("report.name"), filepath);

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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}
	}

}
