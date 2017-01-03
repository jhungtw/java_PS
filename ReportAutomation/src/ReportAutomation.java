import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import org.apache.log4j.RollingFileAppender;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.yaml.snakeyaml.Yaml;

import basic.config.MapBean;
import basic.config.Report;
import basic.util.ReportFrequency;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class ReportAutomation {
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	static Map<String, String> configs;
	static Map<String, Report> reports;
	static String configfile;
	static Logger accessLog;
	static String hybris_user;
	static String hybris_pass;
	static String smtp_user;
	static String smtp_password;
	static String production_mode;

	public void run() throws Exception {

		accessLog.info("------- Initializing -------------------");

		// First we must get a reference to a scheduler
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();

		accessLog.info("------- Initialization Complete --------");

		accessLog.info("------- Scheduling Jobs ----------------");

		// jobs can be scheduled before sched.start() has been called

		// job 1 will run every 20 seconds

		int numberOfReport = 0;

		// *********************************************************************************
		if (production_mode == "enabled") {

			System.out.println("Run with Production Mode");
			for (String entry : reports.keySet()) {
				// System.out.println("Key : " + entry + " Value : " +
				// reports.get(entry).toString());
				// ***********************************************************
				// need implementation: enabled/disable
				// ***********************************************************
				JobDetail job;
				numberOfReport++;
				if (reports.get(entry).isEnabled()) {
					System.out.println(reports.get(entry).getName() + " is enabled");
					job = setJobDetail(entry, reports.get(entry));
					CronTrigger trigger = newTrigger().withIdentity("trigger" + numberOfReport, "group1")
							.withSchedule(cronSchedule(reports.get(entry).getSchedule())).build();
					sched.scheduleJob(job, trigger);
					accessLog.info(job.getKey() + " has been scheduled to run at and repeat based on expression: "
							+ ((CronTrigger) trigger).getCronExpression());

				} else {
					System.out.println(reports.get(entry).getName() + " is disabled");
				}

			}
		} else {

			System.out.println("Run with Test Mode");
			for (String entry : reports.keySet()) {
				// System.out.println("Key : " + entry + " Value : " +
				// reports.get(entry).toString());
				// ***********************************************************
				// need implementation: enabled/disable
				// ***********************************************************
				JobDetail job;
				numberOfReport++;
				if (reports.get(entry).isEnabled()) {
					System.out.println(reports.get(entry).getName() + " is enabled");
					job = setJobDetail(entry, reports.get(entry));
					Calendar now = Calendar.getInstance();
					now.add(Calendar.SECOND, (30 * numberOfReport));
					SimpleTrigger trigger = (SimpleTrigger) newTrigger()
							.withIdentity("trigger" + numberOfReport, "group1").startAt(now.getTime()).build();
					sched.scheduleJob(job, trigger);
					accessLog.info(job.getKey() + " has been scheduled to run at and repeat based on expression: "
							+ trigger.getStartTime());

				} else {
					System.out.println(reports.get(entry).getName() + " is disabled");
				}

			}
		}

		// *********************************************************************************

		accessLog.info("------- Starting Scheduler ----------------");

		// All of the jobs have been added to the scheduler, but none of the
		// jobs
		// will run until the scheduler has been started
		sched.start();

		accessLog.info("------- Started Scheduler -----------------");

		accessLog.info("------- Waiting five minutes... ------------");
		try

		{
			// wait five minutes to show jobs
			Thread.sleep(100L * 1000L);
			// executing...
		} catch (Exception e) {
			//
		}

		accessLog.info("------- Shutting Down ---------------------");

		sched.shutdown(true);

		accessLog.info("------- Shutdown Complete -----------------");

		SchedulerMetaData metaData = sched.getMetaData();
		accessLog.info("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");

	}

	public static void main(String[] args) throws Exception {

		System.out.println(args[0]);
		configfile = args[0];
		readConfig();
		initLogger();
		accessLog = Logger.getLogger("ReportAutomationLog");

		ReportAutomation ra = new ReportAutomation();
		ra.run();

	}


	public static void initLogger() {
		try {
			String filePath = configs.get("log.path").toString().trim();
			PatternLayout layout = new PatternLayout("%-5p %d %m%n");
			RollingFileAppender appender = new RollingFileAppender(layout, filePath);

			// ConsoleAppender cappender = new ConsoleAppender();

			appender.setName("ReportAutomationLog");
			appender.setMaxFileSize("1MB");
			appender.activateOptions();
			Logger.getRootLogger().addAppender(appender);
			// Logger.getRootLogger().addAppender(cappender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readConfig() throws FileNotFoundException {

		// FileReader reader = new FileReader(new
		// File("c:\\psalets\\stuckorder.properties"));
		FileReader reader = new FileReader(new File(configfile));

		Yaml yaml = new Yaml();
		MapBean parsed = yaml.loadAs(reader, MapBean.class);

		configs = parsed.getConfigrations();

		for (String entry : configs.keySet()) {
			System.out.println("Key : " + entry + " Value : " + configs.get(entry).toString());

		}

		hybris_user = configs.get("hybris.user");
		hybris_pass = configs.get("hybris.password");
		smtp_user = configs.get("smtp.user");
		smtp_password = configs.get("smtp.password");
		production_mode = configs.get("productionmode").toLowerCase();

		reports = parsed.getReports();

		for (String entry : reports.keySet()) {
			System.out.println("Key : " + entry + " Value : " + reports.get(entry).toString());

		}

	}

	private static JobDetail setJobDetail(String entry, Report report) {
		JobDetail jd = null;
		
		

		if (production_mode == "enabled") {
			jd = newJob(ReportJob.class).withIdentity(entry, "group1")
					.usingJobData("query", reports.get(entry).getContent()).usingJobData("hybris.user", hybris_user)
					.usingJobData("hybris.password", hybris_pass).usingJobData("smtp.user", smtp_user)
					.usingJobData("smtp.password", smtp_password)
					.usingJobData("email.to", reports.get(entry).getEmail_to())
					.usingJobData("email.cc", reports.get(entry).getEmail_cc())
					.usingJobData("report.name", reports.get(entry).getName()).usingJobData("report.key", entry)
					.usingJobData("temp.folder", configs.get("temp.folder"))
					.usingJobData("email.test", configs.get("email.test"))
					.usingJobData("hybris.env", configs.get("hybris.env"))
					.usingJobData("report.frequency", reports.get(entry).getFrequency()).build();
		} else {
			jd = newJob(ReportJob.class).withIdentity(entry, "group1")
					.usingJobData("query", reports.get(entry).getContent()).usingJobData("hybris.user", hybris_user)
					.usingJobData("hybris.password", hybris_pass).usingJobData("smtp.user", smtp_user)
					.usingJobData("smtp.password", smtp_password).usingJobData("email.to", configs.get("email.test"))
					.usingJobData("email.cc", configs.get("email.test"))
					.usingJobData("report.name", reports.get(entry).getName()).usingJobData("report.key", entry)
					.usingJobData("temp.folder", configs.get("temp.folder"))
					.usingJobData("hybris.env", configs.get("hybris.env"))
					.usingJobData("report.frequency", reports.get(entry).getFrequency())
					.build();
			                                                  
		}

		return jd;
	}

}