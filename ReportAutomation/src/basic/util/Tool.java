package basic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.yaml.snakeyaml.Yaml;

import basic.config.DoneStatus;
import basic.config.Report;
import basic.config.StatusMapBean;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class Tool {
	private static Logger accessLog = Logger.getLogger("ReportAutomationLog");

	public static String packLocal(String filePath, String password) throws ZipException {
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
		zipParameters.setEncryptFiles(true);
		zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
		zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		zipParameters.setPassword(password);
		String baseFileName = FilenameUtils.getBaseName(filePath);
		String basePathName = FilenameUtils.getFullPath(filePath);
		String destinationZipFilePath = FilenameUtils.concat(basePathName, baseFileName.concat(".zip"));
		System.out.println("---" + filePath);
		System.out.println("===" + destinationZipFilePath);
		ZipFile zipFile = new ZipFile(destinationZipFilePath);

		zipFile.addFile(new File(filePath), zipParameters);
		return destinationZipFilePath;
	}

	public static Map<String, Report> disableIrrelevantReports(Map<String, Report> reports) {

		Calendar today = Calendar.getInstance();

		// test monday as 1 st day of month
		// today.set(2017, 4, 1);
		// test monday
		// today.set(2017, 0, 2);
		// test 1 st day of month
		// today.set(2017, 0, 11);

		today.setFirstDayOfWeek(Calendar.MONDAY);

		System.out.println("today: " + today.getTime().toString());

		boolean isMonth = (today.get(Calendar.DAY_OF_MONTH) == 1);
		boolean isWeek = (today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY);

		System.out.println("isMonth//isWeek : " + isMonth + "//" + isWeek);

		if (!isWeek) {

			for (String entry : reports.keySet()) {
				if (reports.get(entry).getFrequency().equalsIgnoreCase(ReportFrequency.WEEK.toString()))
					reports.get(entry).setEnabled(false);
				System.out.println("Key : " + entry + " Value : " + reports.get(entry).toString());

			}

		}

		if (!isMonth)

		{
			for (String entry : reports.keySet()) {
				System.out.println("-->Key : " + entry + " Value : " + reports.get(entry).toString());
				if (reports.get(entry).getFrequency().equalsIgnoreCase(ReportFrequency.MONTH.toString()))
					reports.get(entry).setEnabled(false);
				System.out.println("Key : " + entry + " Value : " + reports.get(entry).toString());

			}
		}
		System.out.println("reports is : " + reports.toString());
		return reports;

	}

	public static void addStatusForRerun(String reportid, String name, boolean status, String filepath)
			throws IOException {

		// Path path = Paths.get(filepath);

		if (Files.notExists(Paths.get(filepath))) {

			Files.createFile(Paths.get(filepath));
			DoneStatus ds = new DoneStatus();
			ds.setDone(status);
			ds.setName(name);
			Map<String, DoneStatus> reports = new HashMap<String, DoneStatus>();
			reports.put(reportid, ds);
			StatusMapBean parsed = new StatusMapBean();
			parsed.setReports(reports);
			FileWriter writer = new FileWriter(filepath);
			Yaml yaml = new Yaml();
			yaml.dump(parsed, writer);

		} else {
			FileReader reader = new FileReader(new File(filepath));
			Yaml yaml = new Yaml();

			StatusMapBean parsed = yaml.loadAs(reader, StatusMapBean.class);
			Map<String, DoneStatus> reports = parsed.getReports();
			if (reports.containsKey(reportid)) {
				DoneStatus ds = reports.get(reportid);
				ds.setDone(status);
				ds.setName(name);
				// implement key is found
				reports.put(reportid, ds);

			} else {
				DoneStatus ds = new DoneStatus();
				ds.setDone(status);
				ds.setName(name);
				// implement key is found
				reports.put(reportid, ds);
			}

			parsed.setReports(reports);
			FileWriter writer = new FileWriter(filepath);
			yaml.dump(parsed, writer);

		}

	}

	public static ArrayList<String> getReportsForRerun(String filepath) throws FileNotFoundException {

		ArrayList<String> ret = new ArrayList<String>();
		FileReader reader = new FileReader(new File(filepath));
		Yaml yaml = new Yaml();
		StatusMapBean parsed = yaml.loadAs(reader, StatusMapBean.class);

		Map<String, DoneStatus> reports = parsed.getReports();

		for (String entry : reports.keySet()) {
			System.out.println("Key : " + entry + " Value : " + reports.get(entry).toString());
			if (!reports.get(entry).isDone())
				ret.add(entry);

		}
		return ret;

	}

	public static String getHybrisDriverURL(String env) {
		String HYBRIS_DRIVER_URL = null;
		switch (env.toLowerCase()) {
		case "prod":

			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";
			break;
		case "bugfix":

			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://bugfix.totalwine.com/virtualjdbc/service";
			break;
		case "uat":

			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://uat.totalwine.com/virtualjdbc/service";
			break;

		default:

			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://bugfix.totalwine.com/virtualjdbc/service";
		}

		return HYBRIS_DRIVER_URL;

	}

	public static String getOutputFilePath(String reportkey, String reportname, String tmpfolder) {

		System.out.println("tmpfolder is " + tmpfolder);
		String report = reportname.trim().replaceAll(" ", "_").toLowerCase();

		StringBuilder sb = new StringBuilder();

		sb.append(tmpfolder).append("\\").append(reportkey).append("_").append(report).append("_")
				.append(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())).append(".xlsx");

		return sb.toString();
	}

	public static void saveResultsetToExcel(String reportname, ResultSet rs, String filepath)
			throws SQLException, IOException {
		// ***********************************************************
		// implement save file
		// ***********************************************************

		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();

		headers = getHeadersFromResultset(rs);
		// accessLog.info("headers" + headers.size());
		rows = getRowsFromResultset(rs);
		// accessLog.info("rows" + rows.size());

		// String filepath = null;

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(reportname);
		XSSFRow row = sheet.createRow(0);

		int numberOfColumns = headers.size();

		for (int i = 0; i < numberOfColumns; i++) {

			System.out.println("dddd" + headers.get(i) + "pppp");
			row.createCell(i).setCellValue(headers.get(i));
		}

		int index = 1;
		int j = 0;

		for (ArrayList<String> tmp : rows) {

			row = sheet.createRow(index);

			for (String cell : tmp) {
				if (cell == null) {
					cell = "//NULL";
				}
				System.out.println("YYYYY" + cell.toString());
				// String callstring = new String(cell.toString());
				row.createCell(j).setCellValue(cell.toString());
				j++;
			}
			j = 0;

			index++;
		}

		FileOutputStream out = new FileOutputStream(new File(filepath));
		wb.write(out);
		out.close();
		wb.close();

	}

	public static String addQueryInterval(String query, String from, String to) {
		if (query.contains("[from]") & query.contains("[to]")) {

			query = query.replace("[from]", from).replace("[to]", to);

		}

		return query;
	}

	public static String getFirstDayOfLastWeek(String format) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -7);
		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			c.add(Calendar.DATE, -1);
		}
		DateFormat df = new SimpleDateFormat(format);

		return df.format(c.getTime());

	}

	public static String getFirstDayOfThisWeek(String format) {
		Calendar c = Calendar.getInstance();

		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			c.add(Calendar.DATE, -1);
		}
		DateFormat df = new SimpleDateFormat(format);

		return df.format(c.getTime());

	}

	public static boolean isFirstDayOfThisWeek(Calendar today) {

		return (today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY);

	}

	public static String getFirstDayOfThisMonth(String format) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.DAY_OF_MONTH, 1);

		DateFormat df = new SimpleDateFormat(format);

		return df.format(c.getTime());

	}

	public static boolean isFirstDayOfThisMonth(Calendar today) {

		return (today.get(Calendar.DAY_OF_MONTH) == 1);

	}

	public static String getFirstDayOfLastMonth(String format) {
		Calendar c = Calendar.getInstance();

		if (c.get(Calendar.MONTH) == 0) {

			c.set(Calendar.MONTH, 11);

			c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
		} else {
			System.out.println("2--" + c.getTime().toString());
			c.set(Calendar.MONTH, Calendar.MONTH - 1);

		}
		c.set(Calendar.DAY_OF_MONTH, 1);

		DateFormat df = new SimpleDateFormat(format);

		return df.format(c.getTime());

	}

	public static String getFinalDayOfLastWeek(String format) {
		// Calendar rightNow = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -7);

		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			c.add(Calendar.DATE, +1);
		}
		DateFormat df = new SimpleDateFormat(format);
		return df.format(c.getTime());

	}

	public static void sendNotification(String emailuser, String emailpassword, String emailto, String emailcc,
			String reportname, String filepath) {
		Calendar now = Calendar.getInstance();
		// Step1
		try {
			accessLog.info("setup Mail Server Properties..");
			Properties mailServerProperties = System.getProperties();
			// change to TWM eamil
			mailServerProperties.put("mail.smtp.port", "587");

			mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");
			accessLog.info("Mail Server Properties have been setup successfully..");

			// Step2

			Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
			MimeMessage generateMailMessage = new MimeMessage(getMailSession);

			generateMailMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailto));
			generateMailMessage.addRecipients(Message.RecipientType.CC, InternetAddress.parse(emailcc));

			generateMailMessage
					.setSubject(reportname + " on " + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE));

			StringBuilder emailBody = new StringBuilder();
			emailBody.append("<h3>Please check the attached for report " + reportname + "</h3><br>");

			// Set text message part
			// multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			BodyPart messageBodyPart = new MimeBodyPart();

			// set content
			messageBodyPart.setContent(emailBody.toString(), "text/html");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			accessLog.info("Mail message have been setup successfully..");
			// set attached
			BodyPart attachBodyPart = new MimeBodyPart();
			// messageBodyPart = new MimeBodyPart();
			// String filename = "/home/manisha/file.txt";
			DataSource source = new FileDataSource(filepath);
			attachBodyPart.setDataHandler(new DataHandler(source));
			attachBodyPart.setFileName(new File(filepath).getName());
			multipart.addBodyPart(attachBodyPart);
			accessLog.info("Mail attachment have been setup successfully..");
			// Send the complete message parts
			generateMailMessage.setContent(multipart);
			accessLog.info("Mail Session has been created successfully..");

			// Step3

			Transport transport = getMailSession.getTransport("smtp");
			// change to TWM eamil
			transport.connect("smtp.gmail.com", emailuser, emailpassword);

			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());

			transport.close();
			accessLog.info("Sending mail is done");
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

	private static ArrayList<String> getHeadersFromResultset(ResultSet rs) throws SQLException {
		ArrayList<String> headers = new ArrayList<String>();
		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			// We are using non property style for making dynamic table
			// final int j = i;
			headers.add(rs.getMetaData().getColumnLabel(i + 1));
			/*
			 * System.out.println(">>>"+rs.getMetaData().getColumnName(i+1));
			 * System.out.println(">>>"+rs.getMetaData().getSchemaName(i+1));
			 * System.out.println(">>>"+rs.getMetaData().getColumnLabel(i+1));
			 */

		}

		return headers;

	}

	private static ArrayList<ArrayList<String>> getRowsFromResultset(ResultSet rs) throws SQLException {
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();

		while (rs.next()) {
			// Iterate Row
			ArrayList<String> row = new ArrayList<String>();
			// ObservableList<String> row = FXCollections.observableArrayList();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				// Iterate Column

				row.add(rs.getString(i));
			}
			rows.add(row);
			System.out.println("Row [1] added " + row);

		}

		return rows;

	}

	public static void copyFiles(String protocol, String host, String user, String password, String localFileFullName,
			String fileName, String hostDir) throws Exception {
		System.out.println("Start copy");
		FTPClient ftp = new FTPClient();

		ftp.connect(host);
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new Exception("Exception in connecting to FTP Server");
		}
		ftp.login(user, password);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();

		try (InputStream input = new FileInputStream(new File(localFileFullName))) {
			ftp.storeFile(hostDir + fileName, input);
		}
		if (ftp.isConnected()) {

			ftp.logout();
			ftp.disconnect();

		}
		System.out.println("Done copy");
	}

	public static void sendEmailByTWMSmtp(String emailuser, String emailto, String emailcc, String reportname,
			String filepath) {
		try {
			
			Properties props = System.getProperties();

			props.put("mail.smtp.host", "smtp.totalwine.com");

			Session session = Session.getInstance(props, null);
			Message message = new MimeMessage(session);
			// Set From: header field of the header.
			message.setFrom(new InternetAddress("jhung@totalwine.com"));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailto));
			if (emailcc != null) {
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailcc));
			}

			// Set Subject: header field
			Calendar now = Calendar.getInstance();
			message.setSubject(reportname + " on " + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE));

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Now set the actual message
		
			messageBodyPart.setContent("<h3>Please check the attached for report " + reportname + "</h3><br>", "text/html");

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);

			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			
			
			
			
			
			DataSource source = new FileDataSource(filepath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(new File(filepath).getName());
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			message.setContent(multipart);

			message.setSentDate(new Date());

			/// message.setRecipients(Message.RecipientType.TO,
			/// InternetAddress.parse(toEmail, false));*/
			System.out.println("Message is ready");
			Transport.send(message);

			System.out.println("EMail Sent Successfully!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
