package utils;

import static j2html.TagCreator.body;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.li;
import static j2html.TagCreator.ul;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import j2html.tags.ContainerTag;


public class OutputUtils {
	private OutputUtils() {
		throw new IllegalAccessError("Utility class");
	}

	public static String getHtmlOutput(List<String> filesWithWrongFormat, List<String> filesNotInHybris,
			List<String> filesNotInApex) {

		ContainerTag wrongFormatHtml = ul();

		if (!filesWithWrongFormat.isEmpty()) {

			for (String tmp : filesWithWrongFormat) {
				wrongFormatHtml.with(li(tmp));
			}

		}
		ContainerTag notInHybrisHtml = ul();
		if (!filesNotInHybris.isEmpty()) {

			for (String tmp : filesNotInHybris) {
				notInHybrisHtml.with(li(tmp));
			}

		}

		ContainerTag notInApexHtml = ul();
		if (!filesNotInApex.isEmpty()) {

			for (String tmp : filesNotInApex) {
				notInApexHtml.with(li(tmp));
			}

		}

		return body()
				.with(h2("Validation of file name for product image import"),
						h3("File name(s) with wrong format").with(wrongFormatHtml),
						h3("Image for an Item that is not found in Hybris (image needs to be uploaded once item is in hybris)").with(notInHybrisHtml), 
						h3("Image for an Item that is not found in Apex").with(notInApexHtml))
				.render();

	}

	public static void sendEmailBySmtp( String emailto, String emailcc, String reportname,
			String htmlContent) throws AddressException, MessagingException, IOException{



		Properties props = System.getProperties();
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		// use gmail

		Session session = Session.getInstance(props, null);
		Message message = new MimeMessage(session);
		// Set From: header field of the header.
		message.setFrom(new InternetAddress("DOTProductionSupportTeam@totalwine.com"));

		BodyPart messageBodyPart = new MimeBodyPart();

		// Now set the actual message

		// messageBodyPart.setContent("<h3>Please check the attached for
		// report " + reportname + "</h3><br>", "text/html");

		messageBodyPart.setContent(htmlContent, "text/html");

		// Check all job is done?
		// Set To: header field of the header.

		message.setSubject(reportname);
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailto));
		if (!emailcc.isEmpty()) {
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailcc));
		}

		// Create a multipar message
		Multipart multipart = new MimeMultipart();

		// Set text message part
		multipart.addBodyPart(messageBodyPart);

		// Part two is attachment

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
	/*
	public static void encryptFile( 
			OutputStream    out, 
	        String          fileName, 
	        PGPPublicKey    encKey, 
	        boolean         armor, 
	        boolean         withIntegrityCheck) 
	        throws IOException, NoSuchProviderException 
	    {     
	        if (armor) 
	        { 
	            out = new ArmoredOutputStream(out); 
	        } 
	         
	        try 
	        { 
	            ByteArrayOutputStream       bOut = new ByteArrayOutputStream(); 
	             
	     
	            PGPCompressedDataGenerator  comData = new PGPCompressedDataGenerator( 
	                                                                    PGPCompressedData.ZIP); 
	                                                                     
	            PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, new File(fileName)); 
	             
	            comData.close(); 
	            
	            byte[] keyBytes = new String("12324234").getBytes();
	            PGPDataEncryptorBuilder builder  = new PGPDataEncryptorBuilder() {
					
					@Override
					public SecureRandom getSecureRandom() {

						return new SecureRandom();
					}
					
					@Override
					public int getAlgorithm() {
						return PGPEncryptedData.CAST5;
					}
					
					@Override
					public PGPDataEncryptor build(byte[] arg0) throws PGPException {
						// TODO Auto-generated method stub
						return null;
					}
				};
	            // PGPEncryptedDataGenerator   cPk = new PGPEncryptedDataGenerator(PGPEncryptedData.CAST5, withIntegrityCheck, new SecureRandom(), "BC"); 
	            PGPEncryptedDataGenerator   cPk = (PGPEncryptedDataGenerator) builder.build(keyBytes);

	            cPk.addMethod(encKey);
	             
	            byte[]                bytes = bOut.toByteArray(); 
	             
	            OutputStream    cOut = cPk.open(out, bytes.length); 
	 
	            cOut.write(bytes); 
	             
	            cOut.close(); 
	 
	            out.close(); 
	        } 
	        catch (PGPException e) 
	        { 
	            System.err.println(e); 
	            if (e.getUnderlyingException() != null) 
	            { 
	                e.getUnderlyingException().printStackTrace(); 
	            } 
	        } 
	    } 
*/
}
