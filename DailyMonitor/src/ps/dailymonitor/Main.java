package ps.dailymonitor;

import java.awt.event.MouseAdapter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.Session;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.Yaml;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import ps.dao.SearchDAO;
import ps.util.Tool;
import ps.config.MapBean;
import ps.config.HybrisJob;
import ps.config.Job;
import ps.config.JobExeStatus;

public class Main {
	private static Logger accessLog;
	private static Map<Integer, Job> reports = new HashMap<Integer, Job>();
	private static Map<String, String> configs = new HashMap<String, String>();
	private static boolean isATSGetStuck;
	private static String dateFormat = "MM/dd/yyyy HH:mm:ss";
	private static String color_headerBG = "#1D72D1";
	private static String color_cellLight = "#cfdef7";
	private static String color_cellDark = "9dbdf2";
	private static String color_statusSuccess = "#bde8a9";
	private static String color_statusRunning = "#eff26a";
	private static String color_statusFail = "#e8a9b4";

	public static void main(String[] args) {

		try {
			readConfig();
			
			initLogger();
			
			accessLog = Logger.getLogger("DailyMonitorLog");
			accessLog.setLevel(Level.INFO);
			accessLog.info("Read Config is done");
			
			

			if (!Tool.isCompletedToday()) {

				System.out.println("Starting Daily monitor report");
				accessLog.info("Starting Daily monitor report");
                
				isATSGetStuck = isATSStuck();
				accessLog.info("Get ATS stuck status is done");
				
				getCloverJobStatus();
				accessLog.info("Get clover jobs's status is done");
				getHybrisJobStatus();
				accessLog.info("Get Hybris jobs's status is done");
				DateTime dt = new DateTime();

				sendEmailByTWMSmtp(configs.get("smtp.user"), configs.get("email.to"), configs.get("email.cc"),configs.get("email.to.success"),configs.get("email.cc.success")
						,"Daily Job Monitor Report " + dt.getMonthOfYear() + "-" + dt.getDayOfMonth() + "-"
								+ dt.getYear(),
						null);
				accessLog.info("Send email is done");
				Tool.AddControlFileforFulfillment();
				accessLog.info("Add control file is done");

			} else {
				accessLog.info("Daily monitor report was done");
				System.out.println("Daily monitor report was done");

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			accessLog.info("Exception detail: ", e);
			e.printStackTrace();

		}

	}

	public static void initLogger() throws IOException {
		System.out.println("11111");
		String filePath = configs.get("log.path").toString().trim();
		System.out.println(filePath);
		PatternLayout layout = new PatternLayout("%-5p %d %m%n");
		RollingFileAppender appender = new RollingFileAppender(layout, filePath);

		// ConsoleAppender cappender = new ConsoleAppender();

		appender.setName("DailyMonitorLog");
		appender.setMaxFileSize("1MB");
		appender.activateOptions();
		Logger.getRootLogger().addAppender(appender);
		Logger.getRootLogger().setLevel(Level.INFO);
		System.out.println("2222");

		// Logger.getRootLogger().addAppender(cappender);

	}

	private static void readConfig() throws FileNotFoundException {

		FileReader reader = new FileReader(new File("c:\\tmp\\dailymonitor22.properties"));
		Yaml yaml = new Yaml();
		MapBean parsed = yaml.loadAs(reader, MapBean.class);

		reports = parsed.getReports();
		configs = parsed.getConfigrations();

		for (String index : configs.keySet()) {
			System.out.println("Key : " + index + " Value : " + configs.get(index).toString());

		}
		for (Integer entry : reports.keySet()) {
			System.out.println("Key : " + entry + " Value : " + reports.get(entry).toString());

		}

	}

	private static void getHybrisJobStatus()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";

		String HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";

		DateTime now = new DateTime();
		DateTime dt;
		dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0, 0, 0);

		String query = "SELECT  {c.code} AS Jobcode, {j.code} AS Jobname, {s.code} AS 'Current Status', {c.starttime} AS 'Start Time', {c.endtime} AS 'End Time', {r.code} AS 'Last Result' "
				+ " FROM  {  Cronjob AS c    JOIN CronJobStatus AS s ON {s.pk} = {c.status}   JOIN CronJobResult AS r ON {r.pk} = {c.result}   JOIN Job AS j ON {j.pk} = {c.job} } "
				+ " WHERE ({c.starttime} >= '" + dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))

				+ "'  AND  (  {j.code} IN (  'twm-associateVideoAndProducerToBrandJob',   'twm-BrandCategoryAssociationJob',    'sync-twmContentCatalog-Staged->Online',  'sync-twmProductCatalog-Staged->Online' ) "
				+ "  OR {c.code} IN('catalogCompositeJob','TWM-Staged-IndexingCompositeJob')"
				+ " OR {c.code} IN ('staged_update-twmIndex-cronjob','staged_delete-twmIndex-cronjob')  ) ) "
				+ "order by {c.starttime} ,SUBSTR( {j.code},1,1)";

		System.out.println("query is " + query);

		StringBuilder FQSB = new StringBuilder();
		FQSB.append(query);
		Class.forName(HYBRIS_DRIVER_CLASS).newInstance();

		Connection connection = DriverManager.getConnection(HYBRIS_DRIVER_URL, "jhung", "hhj1101");

		ResultSet rs = new SearchDAO().FlexableQuery(connection, FQSB.toString());
		ArrayList<HybrisJob> jobruns = new ArrayList<HybrisJob>();
		while (rs.next()) {

			HybrisJob tmp = new HybrisJob();
			tmp.setCode(rs.getString(1));
			tmp.setName(rs.getString(2));
			if (rs.getString(3).equalsIgnoreCase("FINISHED") && rs.getString(6).equalsIgnoreCase("FAILURE")) {
				tmp.setResult(rs.getString(6));
			} else {
				tmp.setResult(rs.getString(3));
			}

			// fix date format
			// tmp.setStarttime(rs.getString(4));
			// tmp.setEndtime(rs.getString(5));
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.s");
			DateTime tmpdt;
			if (rs.getString(4) != null) {
				tmpdt = formatter.parseDateTime(rs.getString(4));
				tmp.setStarttime(tmpdt.toString(DateTimeFormat.forPattern(dateFormat)));
			} else {
				tmp.setStarttime(rs.getString(4));
			}

			if (rs.getString(5) != null) {
				tmpdt = formatter.parseDateTime(rs.getString(5));
				tmp.setEndtime(tmpdt.toString(DateTimeFormat.forPattern(dateFormat)));
			} else {
				tmp.setEndtime(rs.getString(5));
			}

			System.out.println(tmp.toString());
			jobruns.add(tmp);

		}
		rs.close();

		String onlineindexquery = "SELECT  {c.code} AS Jobcode, {j.code} AS Jobname, {s.code} AS 'Current Status', {c.starttime} AS 'Start Time', {c.endtime} AS 'End Time', {r.code} AS 'Last Result' "
				+ " FROM  {  Cronjob AS c    JOIN CronJobStatus AS s ON {s.pk} = {c.status}   JOIN CronJobResult AS r ON {r.pk} = {c.result}   JOIN Job AS j ON {j.pk} = {c.job} } "
				+ " WHERE ({c.starttime} >= '" + dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))
				+ " 5:55:00'  AND     {c.code} IN('online_delete-twmIndex-cronjob','online_update-twmIndex-cronjob','TWM-Online-IndexingCompositeJob')"
				+ "    ) " + "order by {c.starttime} ,SUBSTR( {j.code},1,1)";

		rs = new SearchDAO().FlexableQuery(connection, onlineindexquery);

		while (rs.next()) {

			HybrisJob tmp = new HybrisJob();
			tmp.setCode(rs.getString(1));
			tmp.setName(rs.getString(2));
			if (rs.getString(3).equalsIgnoreCase("FINISHED") && rs.getString(6).equalsIgnoreCase("FAILURE")) {
				tmp.setResult(rs.getString(6));
			} else {
				tmp.setResult(rs.getString(3));
			}
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.s");
			DateTime tmpdt;
			if (rs.getString(4) != null) {
				tmpdt = formatter.parseDateTime(rs.getString(4));
				tmp.setStarttime(tmpdt.toString(DateTimeFormat.forPattern(dateFormat)));
			} else {
				tmp.setStarttime(rs.getString(4));
			}

			if (rs.getString(5) != null) {
				tmpdt = formatter.parseDateTime(rs.getString(5));
				tmp.setEndtime(tmpdt.toString(DateTimeFormat.forPattern(dateFormat)));
			} else {
				tmp.setEndtime(rs.getString(5));
			}

			System.out.println(tmp.toString());
			jobruns.add(tmp);

		}
		connection.close();

		for (Integer entry : reports.keySet()) {
			if (reports.get(entry).getType().equalsIgnoreCase("Hybris")) {
				// boolean executed= false;

				for (HybrisJob hj : jobruns) {
					if (reports.get(entry).getName().equalsIgnoreCase(hj.getCode())
							|| reports.get(entry).getName().equalsIgnoreCase(hj.getName())) {
						reports.get(entry).setStartTime(hj.getStarttime());
						reports.get(entry).setEndTime(hj.getEndtime());
						reports.get(entry).setStatus(hj.getResult());

						break;
					}

				}

				if (reports.get(entry).getSubjobs() != null) {
					Map<Integer, Job> subjobs = reports.get(entry).getSubjobs();

					for (Integer jobindex : subjobs.keySet()) {
						for (HybrisJob hj : jobruns) {
							if (subjobs.get(jobindex).getName().equalsIgnoreCase(hj.getCode())
									|| subjobs.get(jobindex).getName().equalsIgnoreCase(hj.getName())) {
								subjobs.get(jobindex).setStartTime(hj.getStarttime());
								subjobs.get(jobindex).setEndTime(hj.getEndtime());
								subjobs.get(jobindex).setStatus(hj.getResult());

								break;
							}

						}

					}
				}

			}

		}

		getJobContent();

	}

	private static boolean isATSStuck() throws JSchException, IOException {

		String user = configs.get("hotfolder.ssh.user");
		String ip = configs.get("hotfolder.ip");
		String password = configs.get("hotfolder.ssh.password");

		boolean isStuck = false;

		JSch jsch = new JSch();
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("compression.s2c", "zlib,none");
		config.put("compression.c2s", "zlib,none");

		com.jcraft.jsch.Session session = jsch.getSession(user, ip);
		session.setConfig(config);
		session.setPort(22);
		session.setPassword(password);
		session.connect();
		// /opt/dataload/import
		StringBuilder outputBuffer = new StringBuilder();
		Channel channel = session.openChannel("exec");

		DateTime dt = new DateTime();
		String tmp1 = dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd"));
		String tmp2 = dt.minusDays(1).toString(DateTimeFormat.forPattern("yyyy-MM-dd"));

		System.out.println("cd  /opt/dataload/import && ls -ltr --full-time *ats* | grep -e '" + tmp1 + "' -e '" + tmp2
				+ "' | wc -l");

		((ChannelExec) channel).setCommand("cd  /opt/dataload/import && ls -ltr --full-time *ats* | grep -e '" + tmp1
				+ "' -e '" + tmp2 + "' | wc -l");

		InputStream commandOutput = channel.getInputStream();
		channel.connect();
		int readByte = commandOutput.read();

		while (readByte != 0xffffffff) {
			outputBuffer.append((char) readByte);
			readByte = commandOutput.read();
		}

		if (!outputBuffer.toString().trim().equalsIgnoreCase("0")) {
			isStuck = true;
		}
		;

		channel.disconnect();
		System.out.println("isStuck:  " + isStuck);
		return isStuck;

	}

	private static void getCloverJobStatus() throws IOException, SAXException, ParserConfigurationException {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		// CloseableHttpClient httpclient;
		

			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope("104.130.35.109", 8080),
					new UsernamePasswordCredentials("jhung", "totalwine"));
			httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

			ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

				@Override
				public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {

					int status = response.getStatusLine().getStatusCode();
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
					/*
					 * if (status >= 200 && status < 300) { HttpEntity entity =
					 * response.getEntity(); return entity != null ?
					 * EntityUtils.toString(entity) : null; } else {
					 * 
					 * throw new
					 * ClientProtocolException("Unexpected response status: " +
					 * status); }
					 */
				}
			};

			for (Integer entry : reports.keySet()) {
				if (reports.get(entry).getType().equalsIgnoreCase("Clover")) {
					StringBuilder httpsb = new StringBuilder();
					// calculate trigger time
					DateTime now = new DateTime();
					DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
					LocalTime localTime = fmt.parseLocalTime(reports.get(entry).getScheduled());
					DateTime dt;

					System.out.println(
							"is today ? :" + reports.get(entry).isToday() + "/// today is " + now.getDayOfMonth());

					if (reports.get(entry).isToday()) {
						dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(),
								localTime.getHourOfDay(), localTime.getMinuteOfHour(), 0, 0);
					} else {
						dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth() - 1,
								localTime.getHourOfDay(), localTime.getMinuteOfHour(), 0, 0);
					}

					System.out.println(
							"sechedule time is " + dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));

					String fromString = URLEncoder.encode(dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")),
							"UTF-8");

					httpsb.append("http://104.130.35.109:8080/clover/request_processor/executions_history?")
							.append(reports.get(entry).getName() + "&")
							// .append("from=2017-02-02%2000%3A00%3A00&&")
							.append("from=" + fromString + "&").append("records=1&")
							.append("returnType=DESCRIPTION_XML");
					System.out.println("Key : " + entry + " Value : " + reports.get(entry).toString());
					HttpGet httpget = new HttpGet(httpsb.toString());
					System.out.println("Executing request " + httpget.getRequestLine());
					String responseBody = httpclient.execute(httpget, responseHandler);
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					// Using factory get an instance of document builder
					DocumentBuilder db = dbf.newDocumentBuilder();

					// parse using builder to get DOM representation of the XML
					// file

					InputStream stream = new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8));
					Document dom = db.parse(stream);

					if (dom.getElementsByTagName("cs:execution").getLength() == 1) {

						reports.get(entry).setStatus(dom.getElementsByTagName("cs:status").item(0).getTextContent());

						DateTime tmpdate;

						tmpdate = DateTime.parse(
								dom.getElementsByTagName("cs:startTime").item(0).getTextContent().substring(0, 19),
								DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"));
						// fix date format
						/*
						 * reports.get(entry)
						 * .setStartTime(tmpdate.toString(DateTimeFormat.
						 * forPattern("yyyy-MM-dd HH:mm:ss.s")));
						 */
						reports.get(entry).setStartTime(tmpdate.toString(DateTimeFormat.forPattern(dateFormat)));

						// reports.get(entry).setStartTime(dom.getElementsByTagName("cs:startTime").item(0).getTextContent());
						// reports.get(entry).setEndTime(dom.getElementsByTagName("cs:stopTime").item(0).getTextContent());
						tmpdate = DateTime.parse(
								dom.getElementsByTagName("cs:stopTime").item(0).getTextContent().substring(0, 19),
								DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"));

						// fix date format
						/*
						 * reports.get(entry)
						 * .setEndTime(tmpdate.toString(DateTimeFormat.
						 * forPattern("yyyy-MM-dd HH:mm:ss.s")));
						 */

						reports.get(entry).setEndTime(tmpdate.toString(DateTimeFormat.forPattern(dateFormat)));

						reports.get(entry)
								.setDuration(dom.getElementsByTagName("cs:durationString").item(0).getTextContent());

						System.out.println("----------------------------------------");
						System.out.println("->>>>>>>>  " + reports.get(entry).toString());

						System.out.println("----------------------------------------");
					} else {
						reports.get(entry).setStatus("NO EXECUTION");

					}

				}
			}
		
				httpclient.close();
			

	}

	private static void getJobContent() {
		for (Integer entry : reports.keySet()) {

			System.out.println("-->" + reports.get(entry).toString());

		}

	}

	public static void sendEmailByTWMSmtp(String emailuser, String emailto, String emailcc,String emailtosuccess, String emailccsuccess , String reportname,
			String filepath) throws AddressException, MessagingException, IOException, JSchException {
		

			// use twm
			// Properties props = System.getProperties();

			// props.put("mail.smtp.host", "smtp.totalwine.com");

			// use gmail

			Properties props = System.getProperties();
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");

			// use gmail

			Session session = Session.getInstance(props, null);
			Message message = new MimeMessage(session);
			// Set From: header field of the header.
			message.setFrom(new InternetAddress("DOTProductionSupportTeam@totalwine.com"));

			// Set To: header field of the header.
			// message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailto));
			//if (emailcc != null) {
			//	message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailcc));
			//}

			// Set Subject: header field
			// Calendar now = Calendar.getInstance();
			//message.setSubject(reportname+": COMPLETE -- No CloverETL/Hybris job issue [EOM]");

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message

			// messageBodyPart.setContent("<h3>Please check the attached for
			// report " + reportname + "</h3><br>", "text/html");

			messageBodyPart.setContent(getEmailContent(), "text/html");
			
			//Check all job is done?
			// Set To: header field of the header.
			if (Tool.isAllJobDoneSuccessfully(reports,isATSGetStuck)){
				message.setSubject(reportname+": COMPLETE -- No CloverETL/Hybris job issue [EOM]");
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailtosuccess));
				if (emailccsuccess != null) {
					message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailccsuccess));
				}

			}
			else
			{
				message.setSubject(reportname);
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailto));
				if (emailcc != null) {
					message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailcc));
				}

			}

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			// messageBodyPart = new MimeBodyPart();

			// DataSource source = new FileDataSource(filepath);
			// messageBodyPart.setDataHandler(new DataHandler(source));
			// messageBodyPart.setFileName(new File(filepath).getName());
			// multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			message.setContent(multipart);

			message.setSentDate(new Date());

			/// message.setRecipients(Message.RecipientType.TO,
			/// InternetAddress.parse(toEmail, false));*/
			System.out.println("Message is ready");
			// use TWM
			// Transport.send(message);

			// use gmail

			Transport transport = session.getTransport("smtp");

			// Enter your correct gmail UserID and Password
			// if you have 2FA enabled then provide App Specific Password
			transport.connect("smtp.gmail.com", "twmpsnotification", "Grapes123!");

			transport.sendMessage(message, message.getAllRecipients());
			// use gmail

			System.out.println("EMail Sent Successfully!!");

	}

	public static String getEmailContent() throws JSchException, IOException {

		StringBuilder htmlcontent = new StringBuilder();

		htmlcontent.append(
				"<table style = \"border-collapse: collapse; font-family: Arial, Verdana, sans-serif; font-size: 10pt;\">");

		htmlcontent.append("<tr bgcolor=\"" + color_headerBG
				+ "\"  >     <th style = \"border: 1px solid white;color:white; \">No.</th>     <th style = \"border: 1px solid white;color:white;\">Job Type</th>     <th style = \"border: 1px solid white;color:white;\">Job name</th>  <th style = \"border: 1px solid white;color:white;\">Scheduled</th> <th style = \"border: 1px solid white;color:white;\">Start Time</th> <th style = \"border: 1px solid white;color:white;\">End Time</th>   <th style = \"border: 1px solid white;color:white;\">Duration</th> <th style = \"border: 1px solid white;color:white;\">Status</th>  </tr>");

		for (Integer entry : reports.keySet()) {
			if (entry % 2 == 0) {
				htmlcontent.append("<tr bgcolor=\"" + color_cellLight + "\" >");
			} else {
				htmlcontent.append("<tr bgcolor=\"" + color_cellDark + "\" >");
			}
			htmlcontent.append("<th style = \"border: 1px solid white;\">" + entry + "</th>");

			htmlcontent.append("<th style = \"border: 1px solid white;\">" + reports.get(entry).getType() + "</th>");
			htmlcontent.append(
					"<th style = \"border: 1px solid white;\">" + reports.get(entry).getDisplayname() + "</th>");
			htmlcontent
					.append("<th style = \"border: 1px solid white;\">" + reports.get(entry).getScheduled() + "</th>");

			if (reports.get(entry).getStartTime() == null) {
				htmlcontent.append("<th style = \"border: 1px solid white;\">" + "" + "</th>");
			} else {
				htmlcontent.append(
						"<th style = \"border: 1px solid white;\">" + reports.get(entry).getStartTime() + "</th>");
			}

			if (reports.get(entry).getEndTime() == null) {
				htmlcontent.append("<th style = \"border: 1px solid white;\">" + "" + "</th>");
			} else {
				htmlcontent.append(
						"<th style = \"border: 1px solid white;\">" + reports.get(entry).getEndTime() + "</th>");
			}

			// duration
			if (reports.get(entry).getStatus() != null) {
				/*
				 * htmlcontent.append(
				 * "<th style = \"border: 1px solid white;\">" +
				 * reports.get(entry).getStartTime() + "</th>");
				 * htmlcontent.append(
				 * "<th style = \"border: 1px solid white;\">" +
				 * reports.get(entry).getEndTime() + "</th>");
				 */
				// && reports.get(entry).getStatus().contains(new
				// StringBuilder("FINISH"))) {

				if (reports.get(entry).getStartTime() != null && reports.get(entry).getEndTime() != null) {
					DateTime d1 = DateTime.parse(reports.get(entry).getStartTime(),
							DateTimeFormat.forPattern(dateFormat));
					DateTime d2 = DateTime.parse(reports.get(entry).getEndTime(),
							DateTimeFormat.forPattern(dateFormat));

					Period p = new Period(d1, d2);

					htmlcontent.append(
							"<th style = \"border: 1px solid white;\">" + p.toString(new PeriodFormatterBuilder()

									.appendHours().appendSuffix("h").printZeroIfSupported().appendSeparator(":")
									.appendMinutes().appendSuffix("m").printZeroIfSupported().minimumPrintedDigits(2)
									.appendSeparator(":").appendSeconds().appendSuffix("s").minimumPrintedDigits(2)
									.toFormatter()) + "</th>");
				} else {
					htmlcontent.append("<th style = \"border: 1px solid white;\">" + "N/A" + "</th>");
				}
			} else {
				// htmlcontent.append("<th style = \"border: 1px solid
				// white;\">" + "" + "</th>");
				// htmlcontent.append("<th style = \"border: 1px solid
				// white;\">" + "" + "</th>");

				htmlcontent.append("<th style = \"border: 1px solid white;\">" + "N/A" + "</th>");

			}

			if (reports.get(entry).getStatus() == null) {
				htmlcontent.append("<th style = \"border: 1px solid white;color:#847d1a;\" bgcolor=\""
						+ color_statusRunning + "\"> " + "NOT FOUND" + "</th>");
				reports.get(entry).setStatus("NOT FOUND");

			} else {

				if (reports.get(entry).getStatus().contains(new StringBuilder("FINISH"))) {
					htmlcontent.append("<th style = \"border: 1px solid white;color:green;\"   bgcolor=\""
							+ color_statusSuccess + "\"> " + "SUCCESS" + "</th>");
					reports.get(entry).setStatus("SUCCESS");

				} else {
					if (reports.get(entry).getStatus().contains(new StringBuilder("RUNN"))) {
						htmlcontent.append("<th style = \"border: 1px solid white;color:#847d1a;\" bgcolor=\""
								+ color_statusRunning + "\"> " + reports.get(entry).getStatus() + "</th>");
						reports.get(entry).setStatus("RUNNING");

					} else {
						htmlcontent.append("<th style = \"border: 1px solid white;color:red;\"  > "
								+ reports.get(entry).getStatus() + "</th>");
						reports.get(entry).setStatus("FAILURE");
						
					}
				}

			}

			htmlcontent.append("</tr >");

			///// ----------------------

			if (reports.get(entry).getSubjobs() != null) {
				Map<Integer, Job> subjobs = reports.get(entry).getSubjobs();
				for (Integer subindex : subjobs.keySet()) {
					htmlcontent.append("<tr bgcolor=\"" + color_cellLight + "\">");

					htmlcontent.append("<th style = \"border: 1px solid white;\">" + " " + "</th>");

					htmlcontent.append("<th style = \"border: 1px solid white;\">" + " " + "</th>");
					htmlcontent.append(
							"<th style = \"border: 1px solid white;\">" + subjobs.get(subindex).getName() + "</th>");
					htmlcontent.append("<th style = \"border: 1px solid white;\">" + " " + "</th>");

					if (subjobs.get(subindex).getStartTime() == null) {
						htmlcontent.append("<th style = \"border: 1px solid white;\">" + "" + "</th>");
					} else {
						htmlcontent.append("<th style = \"border: 1px solid white;\">"
								+ subjobs.get(subindex).getStartTime() + "</th>");
					}

					if (subjobs.get(subindex).getEndTime() == null) {
						htmlcontent.append("<th style = \"border: 1px solid white;\">" + "" + "</th>");
					} else {
						htmlcontent.append("<th style = \"border: 1px solid white;\">"
								+ subjobs.get(subindex).getEndTime() + "</th>");
					}

					// duration
					// if (subjobs.get(subindex).getStatus() != null) {

					if (subjobs.get(subindex).getStartTime() != null && subjobs.get(subindex).getEndTime() != null) {
						DateTime d1 = DateTime.parse(subjobs.get(subindex).getStartTime(),
								DateTimeFormat.forPattern(dateFormat));
						DateTime d2 = DateTime.parse(subjobs.get(subindex).getEndTime(),
								DateTimeFormat.forPattern(dateFormat));

						Period p = new Period(d1, d2);

						htmlcontent.append(
								"<th style = \"border: 1px solid white;\">" + p.toString(new PeriodFormatterBuilder()

										.appendHours().appendSuffix("h").printZeroIfSupported().appendSeparator(":")
										.appendMinutes().appendSuffix("m").printZeroIfSupported()
										.minimumPrintedDigits(2).appendSeparator(":").appendSeconds().appendSuffix("s")
										.minimumPrintedDigits(2).toFormatter()) + "</th>");

					}

					else {
						htmlcontent.append("<th style = \"border: 1px solid white;\">" + "N/A" + "</th>");
					}
					/*
					 * }
					 * 
					 * else { // htmlcontent.
					 * append("<th style = \"border: 1px solid white;\">" + "" +
					 * "</th>"); // htmlcontent.
					 * append("<th style = \"border: 1px solid white;\">" + "" +
					 * "</th>");
					 * 
					 * htmlcontent.
					 * append("<th style = \"border: 1px solid white;\">" +
					 * "N/A" + "</th>");
					 * 
					 * }
					 */
					// htmlcontent.append(
					// "<th style = \"border: 1px solid white;\">" +
					// subjobs.get(subindex).getStatus() + "</th>");

					if (subjobs.get(subindex).getStatus() != null) {

						if (subjobs.get(subindex).getStatus().contains(new StringBuilder("FINISH"))) {
							htmlcontent.append("<th style = \"border: 1px solid white; color:green;\"   bgcolor=\""
									+ color_statusSuccess + "\"> " + "SUCCESS" + "</th>");
							subjobs.get(subindex).setStatus("SUCCESS");
						} else {
							if (subjobs.get(subindex).getStatus().contains(new StringBuilder("RUNN"))) {
								htmlcontent.append("<th style = \"border: 1px solid white;color:#847d1a;\" bgcolor=\""
										+ color_statusRunning + "\"> " + subjobs.get(subindex).getStatus() + "</th>");
								subjobs.get(subindex).setStatus("RUNNING");

							} else {
								htmlcontent.append("<th style = \"border: 1px solid white;color:red;\" bgcolor=\""
										+ color_statusFail + "\"> " + subjobs.get(subindex).getStatus() + "</th>");
								subjobs.get(subindex).setStatus("FAILURE");
							}
						}
						// htmlcontent.append("</tr >");
					} else {

						htmlcontent.append("<th style = \"border: 1px solid white;color:#847d1a;\" bgcolor=\""
								+ color_statusRunning + "\"> " + "NOT FOUND" + "</th>");
						subjobs.get(subindex).setStatus("NOT FOUND");

						// htmlcontent.append("</tr >");
					}

					htmlcontent.append("</tr >");
				}

			}

		}

		htmlcontent.append("<tr bgcolor=\"" + color_cellDark + "\" >");

		htmlcontent.append("<th style = \"border: 1px solid white;\">" + "10" + "</th>");

		htmlcontent.append("<th style = \"border: 1px solid white;\">" + "Jump Box" + "</th>");
		htmlcontent.append("<th style = \"border: 1px solid white;\">" + "Hot Folder" + "</th>");
		htmlcontent.append("<th style = \"border: 1px solid white;\">" + "7:00" + "</th>");

		htmlcontent.append("<th style = \"border: 1px solid white;\">" + "" + "</th>");

		htmlcontent.append("<th style = \"border: 1px solid white;\">" + "" + "</th>");

		// duration

		htmlcontent.append("<th style = \"border: 1px solid white;\">" + "" + "</th>");

		if (isATSGetStuck) {

			htmlcontent.append("<th style = \"border: 1px solid white;color:red;\" bgcolor=\"" + color_statusFail
					+ "\"> " + "FAILURE" + "</th>");

		} else {

			htmlcontent.append("<th style = \"border: 1px solid white;color:green;\" bgcolor=\"" + color_statusSuccess
					+ "\"> " + "SUCCESS" + "</th>");

		}
		htmlcontent.append("</tr >");
		htmlcontent.append("</table>");

		System.out.println(htmlcontent.toString());

		return htmlcontent.toString();
	}

}
