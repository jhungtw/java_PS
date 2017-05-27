import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import utils.OutputUtils;

public class ProductImageImportValidation {

	  // private static final String marketingFolderWithImage = "\\\\tw-pv-file-4\\advertising\\Advertising\\AWR\\Hybris\\Send to Hybris";
	 private static final String marketingFolderWithImage = "\\\\tw-pv-file-4\\advertising\\Advertising\\AWR\\Hybris\\Send to Hybris\\Archive\\ARC_20170523_2300";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ImageDriveService imageDrive = new ImageDriveService();
		imageDrive.readFileListInImageDrive(marketingFolderWithImage);
		
		List<String> filesWithWrongFormat = new ArrayList<>();
		List<String> filesNotInHybris = new ArrayList<>();
		List<String> filesNotInApex = new ArrayList<>();

		// TODO 1a, validate file names

		// TODO 2. based on 1 , read existing codes in APEX

		APEXDao apexDao = new APEXDao();
		String APEX_DB_URL="jdbc:sqlserver://cs-pdb-mirror;databaseName=Apex;integratedSecurity=true;";
		String APEX_DB_USER="RSSCL\\jhung";
		String APEX_PASSWORD="Hhj1101@";
		
		System.out.println("start apexdao");

		try {
			
			filesNotInApex= (List<String>) ((ArrayList<String>) imageDrive.getAllImageWithItemCodeInFolder()).clone();
			filesNotInApex.addAll(imageDrive.getAllImageWithITempositionCodeInFolder());
			
			apexDao.initConnection(APEX_DB_USER, APEX_PASSWORD, APEX_DB_URL);
			showListContent(imageDrive.getAllImageWithItemCodeInFolder());
			filesNotInApex.removeAll(apexDao.getExistingItemcode(imageDrive.getAllImageWithItemCodeInFolder()));
			filesNotInApex.removeAll(apexDao.getExistingItempositioncode((List<String>) imageDrive.getAllImageWithITempositionCodeInFolder()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("end apexdao");

		// TODO 3, based on 1 , read item level codes in Hybris

		HybrisDao hybrisDao = new HybrisDao();

		String HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";

		String HYBRIS_USER = "jhung";
		String HYBRIS_PASSWORD = "hhj1101";

		try {
			
			filesNotInHybris=  (List<String>) ((ArrayList<String>) imageDrive.getAllImageWithItemCodeInFolder()).clone(); 
			filesNotInHybris.addAll(imageDrive.getAllImageWithITempositionCodeInFolder());
			
			hybrisDao.initConnection(HYBRIS_USER, HYBRIS_PASSWORD, HYBRIS_DRIVER_URL);
			
			filesNotInHybris.removeAll(hybrisDao.getExistingItemcode((ArrayList<String>) imageDrive.getAllImageWithItemCodeInFolder()));
			filesNotInHybris.removeAll(hybrisDao.getExistingItemPositioncode((ArrayList<String>) imageDrive.getAllImageWithITempositionCodeInFolder()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("end hybrisdao");
		
		filesWithWrongFormat = imageDrive.getAllImageWithWrongNameFormatInFolder();
		// TODO 4 based on 11 read item position level code in Hybris

		
		// TODO output
		try {
			OutputUtils.sendEmailBySmtp("jhung@totalwine.com", "jhung@totalwine.com", "Validation of product images", OutputUtils.getHtmlOutput(filesWithWrongFormat,filesNotInHybris,filesNotInApex));
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	
	private static void showListContent(List<String> tDriveWithPosition) {
		// TODO Auto-generated method stub
		System.out.println("YYYYYY ");
		for (String tmp : (ArrayList<String>) tDriveWithPosition) {
			System.out.println("--> " + tmp);

		}
	}

}
