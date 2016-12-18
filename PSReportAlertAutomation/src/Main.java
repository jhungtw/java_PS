import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Main {
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
	public static void main(String[] args) {
		String HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";

		String HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";

		String HYBRIS_USER = "jhung";
		String HYBRIS_PASSWORD = "hhj1101";
		// TODO Auto-generated method stub
		String queryDupIS = "select {item},{store} as count from {itemstore} where {catalogversion}='8796093121113'  group by {item},{store},{catalogVersion} having count(*)>1";
		String queryDupPR = "select {pk},{product},{pricerowcode} from {PriceRow} where {product} is null and {catalogVersion}= 8796093121113";

		ResultSet rs = null;
		Connection connection = null;

		Statement stmt = null;
		

		try {
			Class.forName(HYBRIS_DRIVER_CLASS).newInstance();

			connection = DriverManager.getConnection(HYBRIS_DRIVER_URL, HYBRIS_USER, HYBRIS_PASSWORD);

			stmt = connection.createStatement();

			rs = stmt.executeQuery(queryDupIS);
			
			boolean b1 = rs.last();
			int numberOfRecords_IS = 0;
			if(b1){
			    numberOfRecords_IS = rs.getRow();
			}
            
			rs = stmt.executeQuery(queryDupPR);
			boolean b2 = rs.last();
			int numberOfRecords_PR = 0;
			if(b2){
			    numberOfRecords_PR = rs.getRow();
			}
			
			System.out.println("row nuber is : "+numberOfRecords_IS +"--"+ numberOfRecords_PR );
			
			rs.close();

			connection.close();
			sendNotification( numberOfRecords_IS, numberOfRecords_PR) ;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}

	private static void sendNotification(int IS, int PR) {
		Calendar now = Calendar.getInstance();
		// Step1
		try {
			System.out.println("\n 1st ===> setup Mail Server Properties..");
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.smtp.port", "587");
			mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");
			System.out.println("Mail Server Properties have been setup successfully..");
 
			// Step2
			System.out.println("\n\n 2nd ===> get Mail Session..");
			getMailSession = Session.getDefaultInstance(mailServerProperties, null);
			generateMailMessage = new MimeMessage(getMailSession);
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("jhung@totalwine.com"));
			generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("jhung@totalwine.com"));
			generateMailMessage.setSubject("Alert for Duplicate PriceRow/ItemStore issue on "+ (now.get(Calendar.MONTH)+1)+"-"+now.get(Calendar.DATE));
			StringBuilder emailBody = new StringBuilder(); //"Test email by Crunchify.com JavaMail API example. " + "<br><br> Regards, <br>Crunchify Admin";
			String body_PR= "<br>Check http://www.totalwine.com/validatepricerow to fix ProceRow issue. \n";
		    String body_IS= "<br>Check http://twsharepoint.totalwine.net/DOT%20Production%20Support/Knowladge%20Base/Run%20Books/Fix_ItemStore.docx to fix ItemStore issue. \n";
			
			
			if (IS != 0 & PR !=0)
				emailBody.append("<h1>Find duplicate PriceRow and ItemStore issue.</h1>").append(body_PR).append(body_IS);
			if (IS == 0 & PR !=0)
				emailBody.append("<h1>Find duplicate PriceRow issue.</h1>").append(body_PR);
			if (IS != 0 & PR ==0)
				emailBody.append("<h1>Find duplicate ItemStore issue.</h1>").append(body_IS);
			if (IS == 0 & PR ==0)
				emailBody.append("<h1>No duplicate PriceRow/ItemStore issue.</h1>");
			
			generateMailMessage.setContent(emailBody.toString(), "text/html");
			System.out.println("Mail Session has been created successfully..");
 
			// Step3
			System.out.println("\n\n 3rd ===> Get Session and Send mail");
			Transport transport = getMailSession.getTransport("smtp");
 
			// Enter your correct gmail UserID and Password
			// if you have 2FA enabled then provide App Specific Password
			transport.connect("smtp.gmail.com", "jerry110160", "hhj050460");
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
