import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import org.apache.log4j.RollingFileAppender;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.yaml.snakeyaml.Yaml;

import basic.config.MapBean;
import basic.config.Report;
import basic.util.ExecutionStrategy;
import basic.util.Tool;

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
	static final String progressfilename = "progress.txt";



	public void run() throws Exception {

		accessLog.info("------- Initializing -------------------");

		// First we must get a reference to a scheduler
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();

		//accessLog.info("------- Initialization Complete --------");

		accessLog.info("------- Scheduling Jobs ----------------");

		// jobs can be scheduled before sched.start() has been called

		// job 1 will run every 20 seconds

		int numberOfReport = 0;
		Calendar now = Calendar.getInstance();

		accessLog.info("Now is " + now.getTime().toString());

		// *********************************************************************************
		if (production_mode.equalsIgnoreCase("enabled")) {

			System.out.println("Run with Production Mode");
			accessLog.info("Run with Production Mode");

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
					Trigger trigger = null;
					/* disable cron scheduling
					if (configs.get("exec.strategy").equalsIgnoreCase(ExecutionStrategy.CRON_EXPRESSION.toString())) {

						trigger = (CronTrigger) newTrigger().withIdentity("trigger" + numberOfReport, "group1")
								.withSchedule(cronSchedule(reports.get(entry).getSchedule())).build();
						sched.scheduleJob(job, trigger);
						accessLog.info(job.getKey() + " has been scheduled to run at and repeat based on expression: "
								+ ((CronTrigger) trigger).getCronExpression());
					}
					*/

					if (configs.get("exec.strategy").equalsIgnoreCase(ExecutionStrategy.FIXED_INTERVAL.toString())) {
						System.out.println("Before adding interval: Time is " + now.getTime().toString());

						now.add(Calendar.SECOND, (Integer.parseInt(configs.get("exec.interval")) ));

						System.out.println("Before generate Trigger: Time is " + now.getTime().toString());
						trigger = (SimpleTrigger) newTrigger().withIdentity("trigger" + numberOfReport, "group1")
								.startAt(now.getTime()).build();
						sched.scheduleJob(job, trigger);
						accessLog.info(job.getKey() + " has been scheduled to run at  " + trigger.getStartTime());
					}

				} else {
					System.out.println(reports.get(entry).getName() + " is disabled");
					accessLog.info(reports.get(entry).getName() + " is disabled");
				}

			}
		} else {

			System.out.println("Run with Test Mode");
			accessLog.info("Run with Test Mode");

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

					now.add(Calendar.SECOND, 30);
					SimpleTrigger trigger = (SimpleTrigger) newTrigger()
							.withIdentity("trigger" + numberOfReport, "group1").startAt(now.getTime()).build();
					sched.scheduleJob(job, trigger);
					accessLog.info(job.getKey() + " has been scheduled to run at " + trigger.getStartTime());

				} else {
					System.out.println(reports.get(entry).getName() + " is disabled");
					accessLog.info(reports.get(entry).getName() + " is disabled");
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

		accessLog.info("------- Waiting "+(Integer.parseInt(configs.get("exec.interval"))* numberOfReport +300)/60+" minutes... ------------");
		

		try

		{
			// wait five minutes to show jobs
			// Thread.sleep(3L * 60L * 1000L);

			if (production_mode.equalsIgnoreCase("enabled")) {

				Thread.sleep(
						Math.abs(now.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) + 5L * 60L * 1000L);

			} else {
				Thread.sleep((Integer.parseInt(configs.get("exec.interval"))* numberOfReport +300) * 60L * 1000L);
			}

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
		accessLog.setLevel(Level.INFO);



		// Disable reports not in right schedule
	
		if (production_mode.equalsIgnoreCase("enabled")) {
			reports = Tool.disableIrrelevantReports(reports);
		}
		boolean exeflag = false;
		for (String entry : reports.keySet()) {
			System.out.println("Key : " + entry + " Value : " + reports.get(entry).toString());
			if (reports.get(entry).isEnabled())
				exeflag = true;
		}

		accessLog.info("Disable reports not in right schedule is done");
        // When enabled report exists, run reportautomation
		if (exeflag) {
			accessLog.info("Starting report automation program");
			ReportAutomation ra = new ReportAutomation();
			ra.run();
		} else {
			accessLog.info("No report is enabled. Report automation program will be terminated");

		}
		/*
		 * int NumberOfRerun = 0; while (NumberOfRerun < 3) { NumberOfRerun++;
		 * accessLog.info("Re-run not done reports: " + NumberOfRerun +
		 * " time"); ra.rerun();
		 * accessLog.info("Re-run not done reports is done"); }
		 */

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
			Logger.getRootLogger().setLevel(Level.INFO);

			// Logger.getRootLogger().addAppender(cappender);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readConfig() throws FileNotFoundException {

		FileReader reader = new FileReader(new File(configfile));

		Yaml yaml = new Yaml();
		MapBean parsed = yaml.loadAs(reader, MapBean.class);

		configs = parsed.getConfigrations();

		for (String entry : configs.keySet()) {
			System.out.println("Key : " + entry + " Value : " + configs.get(entry));

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

		if (production_mode.equalsIgnoreCase("enabled")) {
			jd = newJob(ReportJob.class).withIdentity(entry, "group1")
					.usingJobData("name", reports.get(entry).getName())
					.usingJobData("query", reports.get(entry).getContent())
					
					.usingJobData("hybris.user", hybris_user)
					.usingJobData("hybris.password", hybris_pass)
					.usingJobData("smtp.user", smtp_user)
					.usingJobData("smtp.password", smtp_password)
					.usingJobData("output.format", reports.get(entry).getOutput_format())
					.usingJobData("output.filename", reports.get(entry).getOutput_filename())
					.usingJobData("password.protected", reports.get(entry).isPassword_protected())
					.usingJobData("password", reports.get(entry).getPassword())
					.usingJobData("email.notification", reports.get(entry).isEmail_notification())
					.usingJobData("email.to", reports.get(entry).getEmail_to())
					.usingJobData("email.cc", reports.get(entry).getEmail_cc())
					.usingJobData("ftp.notification", reports.get(entry).isFtp_notification())
					.usingJobData("ftp.host", reports.get(entry).getFtp_host())
					.usingJobData("ftp.port", reports.get(entry).getFtp_port())
					.usingJobData("ftp.folder", reports.get(entry).getFtp_folder())
					.usingJobData("ftp.username", reports.get(entry).getFtp_username())
					.usingJobData("ftp.password", reports.get(entry).getFtp_password())
					.usingJobData("backup.notification", reports.get(entry).isBackup_notification())
					.usingJobData("backup.folder", reports.get(entry).getBackup_folder())
					.usingJobData("report.name", reports.get(entry).getName()).usingJobData("report.key", entry)
					.usingJobData("temp.folder", configs.get("temp.folder"))
					.usingJobData("email.test", configs.get("email.test"))
					.usingJobData("hybris.env", configs.get("hybris.env"))
					.usingJobData("report.frequency", reports.get(entry).getFrequency()).build();
		} else {
			jd = newJob(ReportJob.class).withIdentity(entry, "group1")
					.usingJobData("name", reports.get(entry).getName())
					.usingJobData("query", reports.get(entry).getContent()).usingJobData("hybris.user", hybris_user)
					.usingJobData("hybris.password", hybris_pass)
					.usingJobData("smtp.user", smtp_user)
					.usingJobData("smtp.password", smtp_password)
					.usingJobData("output.format", reports.get(entry).getOutput_format())
					.usingJobData("output.filename", reports.get(entry).getOutput_filename())
					.usingJobData("email.to", configs.get("email.test"))
					.usingJobData("email.cc", configs.get("email.test"))
					.usingJobData("email.notification", "true")
					.usingJobData("ftp.notification", "false")
					.usingJobData("backup.notification", "false")
					.usingJobData("password.protected", reports.get(entry).isPassword_protected())
					.usingJobData("password", reports.get(entry).getPassword())
					.usingJobData("report.name", reports.get(entry).getName()).usingJobData("report.key", entry)
					.usingJobData("temp.folder", configs.get("temp.folder"))
					.usingJobData("hybris.env", configs.get("hybris.env"))
					.usingJobData("report.frequency", reports.get(entry).getFrequency()).build();

		}

		return jd;
	}

}
