import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import utils.ConfigUtils;
import utils.OutputUtils;

public class ProductImageImportValidation {


	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException {

		Map<String, String> configs;
		// read config

		configs = ConfigUtils.readConfig();
        
		//read file list
		ImageDriveService imageDrive = new ImageDriveService();
		imageDrive.readFileListInImageDrive(configs.get("image.upload.folder"));

		List<String> filesWithWrongFormat = new ArrayList<>();
		List<String> filesNotInHybris = new ArrayList<>();
		List<String> filesNotInApex = new ArrayList<>();

		// 2. based on 1 , read existing codes in APEX

		APEXDao apexDao = new APEXDao();
		String APEX_DB_URL = configs.get("apex.db.url");
		String APEX_DB_USER = configs.get("apex.db.user");
		String APEX_PASSWORD = configs.get("apex.db.pasword");
		System.out.println("start apexdao");

		try {

			filesNotInApex = (List<String>) ((ArrayList<String>) imageDrive.getAllImageWithItemCodeInFolder()).clone();
			filesNotInApex.addAll(imageDrive.getAllImageWithITempositionCodeInFolder());

			apexDao.initConnection(APEX_DB_USER, APEX_PASSWORD, APEX_DB_URL);
			showListContent(imageDrive.getAllImageWithItemCodeInFolder());
			filesNotInApex.removeAll(apexDao.getExistingItemcode(imageDrive.getAllImageWithItemCodeInFolder()));
			filesNotInApex.removeAll(apexDao
					.getExistingItempositioncode((List<String>) imageDrive.getAllImageWithITempositionCodeInFolder()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("end apexdao");

		// TODO 3, based on 1 , read item level codes in Hybris

		HybrisDao hybrisDao = new HybrisDao();

		String HYBRIS_DRIVER_URL = configs.get("hybris.db.url");
		String HYBRIS_USER = configs.get("hybris.db.user");
		String HYBRIS_PASSWORD = configs.get("hybris.db.password");

		try {

			filesNotInHybris = (List<String>) ((ArrayList<String>) imageDrive.getAllImageWithItemCodeInFolder())
					.clone();
			filesNotInHybris.addAll(imageDrive.getAllImageWithITempositionCodeInFolder());

			hybrisDao.initConnection(HYBRIS_USER, HYBRIS_PASSWORD, HYBRIS_DRIVER_URL);

			filesNotInHybris.removeAll(
					hybrisDao.getExistingItemcode((ArrayList<String>) imageDrive.getAllImageWithItemCodeInFolder()));
			filesNotInHybris.removeAll(hybrisDao.getExistingItemPositioncode(
					(ArrayList<String>) imageDrive.getAllImageWithITempositionCodeInFolder()));
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
			OutputUtils.sendEmailBySmtp(configs.get("smtp.email.to"), configs.get("smtp.email.cc"),
					"Validation of product images",
					OutputUtils.getHtmlOutput(filesWithWrongFormat, filesNotInHybris, filesNotInApex));
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
