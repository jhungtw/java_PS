package basic.util;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
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

import org.apache.log4j.Logger;

public class EmailUtils {
	private static Logger accessLog = Logger.getLogger("ReportAutomationLog");

	public static void sendEmailByTWMSmtp(String emailuser, String emailto, String emailcc, String extraContent,
			String reportname, String filepath) {
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
			message.setSubject(
					"[REPORT] " + reportname + " on " + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE));
	
			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();
	
			// Now set the actual message
			StringBuilder contentBuilder = new StringBuilder();
			contentBuilder.append("<h3>Please check the attached for report ").append(reportname).append("</h3><br>");
			if(null != extraContent)		
				contentBuilder.append(extraContent);
			messageBodyPart.setContent(contentBuilder.toString(), "text/html");
	
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

}
