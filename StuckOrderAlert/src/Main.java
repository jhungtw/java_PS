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

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.yaml.snakeyaml.Yaml;

import basic.config.MapBean;
import basic.config.Config;

public class Main {
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	static Map<String, String> configs;
	static String configfile;
	static Logger accessLog;

	public static void main(String[] args) throws FileNotFoundException {
         
		
		
		
		 accessLog = Logger.getLogger("myFirstLog");

		
		
		System.out.println(args[0]);
		configfile = args[0];
		readConfig();
		
		initLogger();

		StringBuilder body_Status = new StringBuilder();
		StringBuilder body_Process = new StringBuilder();

		String HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";

		String HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";

		// String HYBRIS_USER = "jhung";
		String HYBRIS_USER = configs.get("hybris.user").toString().trim();

		// String HYBRIS_PASSWORD = "hhj1101";
		String HYBRIS_PASSWORD = configs.get("hybris.password").toString().trim();

		// TODO Auto-generated method stub
		String tmp1 = "select {code} from {order} where {status} is null and {modifiedtime} >= [from] and {modifiedtime} <= [to]";
		String tmp2 = "select {bp.code} from {businessprocess as bp join ProcessState as ps on {ps.pk}={bp.state}} where {bp.code} like 'oms-submitorder-process%' and {bp.modifiedtime} >= [from] and {bp.modifiedtime} <= [to] and {ps.code} = 'ERROR'";
		StringBuilder queryStatus = new StringBuilder();
		StringBuilder queryProcess = new StringBuilder();

		Calendar rightNow = Calendar.getInstance();

		String to = "'" + rightNow.get(Calendar.YEAR) + "-" + (rightNow.get(Calendar.MONTH) + 1) + "-"
				+ rightNow.get(Calendar.DAY_OF_MONTH) + " " + rightNow.get(Calendar.HOUR_OF_DAY) + ":"
				+ rightNow.get(Calendar.MINUTE) + "'";
		//System.out.println(to);
		rightNow.add(Calendar.MINUTE, -30);

		String from = "'" + rightNow.get(Calendar.YEAR) + "-" + (rightNow.get(Calendar.MONTH) + 1) + "-"
				+ rightNow.get(Calendar.DAY_OF_MONTH) + " " + rightNow.get(Calendar.HOUR_OF_DAY) + ":"
				+ rightNow.get(Calendar.MINUTE) + "'";

		//System.out.println(from);

		if (tmp1.contains("[from]") & tmp1.contains("[to]")) {

			tmp1 = tmp1.replace("[from]", from).replace("[to]", to);

			queryStatus.append(tmp1);

			//System.out.println("queryStatus is : " + queryStatus);

		}

		if (tmp2.contains("[from]") & tmp2.contains("[to]")) {
			tmp2 = tmp2.replace("[from]", from).replace("[to]", to);

			queryProcess.append(tmp2);

			//System.out.println("queryStatus is : " + queryProcess);

		}

		ResultSet rs = null;
		Connection connection = null;

		Statement stmt = null;

		try {
			Class.forName(HYBRIS_DRIVER_CLASS).newInstance();

			connection = DriverManager.getConnection(HYBRIS_DRIVER_URL, HYBRIS_USER, HYBRIS_PASSWORD);

			stmt = connection.createStatement();

			rs = stmt.executeQuery(queryStatus.toString());

			int numberOfRecords_ST = 0;

			body_Status.append("<h3>Find blank status in the below order(s): </h3> <br><ul>");

			while (rs.next()) {
				numberOfRecords_ST = numberOfRecords_ST + 1;
				body_Status.append("<li>" + rs.getString(1) + "</li>");
				// System.out.println("body_Status is a " +
				// body_Status.toString());

			}
			body_Status.append("</ul>");
			// System.out.println("body_Status is b " + body_Status.toString());

			// System.out.println("body_Status is c " + body_Status.toString());

			rs = stmt.executeQuery(queryProcess.toString());

			int numberOfRecords_PR = 0;

			body_Process.append("<h3>Find stuck process in the below order(s): </h3> <br><ul>");

			while (rs.next()) {
				numberOfRecords_PR = numberOfRecords_PR + 1;
				body_Process.append("<li>" + rs.getString(1) + "</li>");

			}
			body_Process.append("</ul>");

			accessLog.info("body_Process is " + body_Process.toString());

			accessLog.info("row nuber is : " + numberOfRecords_ST + "--" + numberOfRecords_PR);

			rs.close();
			stmt.close();
			connection.close();
			sendNotification(numberOfRecords_ST, numberOfRecords_PR, body_Status.toString(), body_Process.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void sendNotification(int numberOfRecords_ST, int numberOfRecords_PR, String body_Status,
			String body_process) {
		Calendar now = Calendar.getInstance();
		// Step1
		try {
			accessLog.info("\n 1st ===> setup Mail Server Properties..");
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.smtp.port", "587");
			mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");
			accessLog.info("Mail Server Properties have been setup successfully..");

			// Step2
			accessLog.info("\n\n 2nd ===> get Mail Session..");
			getMailSession = Session.getDefaultInstance(mailServerProperties, null);
			generateMailMessage = new MimeMessage(getMailSession);

			generateMailMessage.addRecipients(Message.RecipientType.TO,
					InternetAddress.parse(configs.get("email.to").toString().trim()));
			generateMailMessage.addRecipients(Message.RecipientType.CC,
					InternetAddress.parse(configs.get("email.cc").toString().trim()));

			generateMailMessage.setSubject(
					"Alert for stuck order(s) on " + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE));
			StringBuilder emailBody = new StringBuilder();

			if (numberOfRecords_ST != 0 & numberOfRecords_PR != 0)
				emailBody.append(body_Status).append("<br>").append(body_process);

			if (numberOfRecords_ST == 0 & numberOfRecords_PR != 0)
				emailBody.append(body_process);
			if (numberOfRecords_ST != 0 & numberOfRecords_PR == 0)
				emailBody.append(body_Status);
			if (numberOfRecords_ST == 0 & numberOfRecords_PR == 0)
				emailBody.append("<h2>No stuck order</h2>");

			generateMailMessage.setContent(emailBody.toString(), "text/html");
			accessLog.info("Mail Session has been created successfully..");

			// Step3

			accessLog.info("\n\n 3rd ===> Get Session and Send mail");
			Transport transport = getMailSession.getTransport("smtp");
			if (numberOfRecords_ST != 0 | numberOfRecords_PR != 0) {
				// Enter your correct gmail UserID and Password
				// if you have 2FA enabled then provide App Specific Password
				// transport.connect("smtp.gmail.com", "twmpsnotification",
				// "Grapes123!");
				transport.connect("smtp.gmail.com", configs.get("smtp.user").toString().trim(),
						configs.get("smtp.password").toString().trim());

				transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			}
			transport.close();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			accessLog.error(e.getLocalizedMessage());
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void initLogger() {
	    try {
	      String filePath =configs.get("log.path").toString().trim();
	      PatternLayout layout = new PatternLayout("%-5p %d %m%n");
	      RollingFileAppender appender = new RollingFileAppender(layout, filePath);
	      appender.setName("myFirstLog");
	      appender.setMaxFileSize("1MB");
	      appender.activateOptions();
	      Logger.getRootLogger().addAppender(appender);
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

	}

}
