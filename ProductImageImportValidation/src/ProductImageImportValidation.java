import java.sql.SQLException;
import java.util.ArrayList;

public class ProductImageImportValidation {

	private static final String marketingFolderWithImage = "\\\\tw-pv-file-4\\advertising\\Advertising\\AWR\\Hybris\\Send to Hybris";

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ImageDriveService imageDrive = new ImageDriveService();
		imageDrive.readFileListInImageDrive(marketingFolderWithImage);

		// TODO 1a, validate file names

		// TODO 2. based on 1 , read existing codes in APEX

		APEXDao apexDao = new APEXDao();
		String APEX_DB_URL="jdbc:sqlserver://cs-pdb-mirror;databaseName=Apex;integratedSecurity=true;";
		String APEX_DB_USER="RSSCL\\jhung";
		String APEX_PASSWORD="Hhj1101@";
		
		System.out.println("start apexdao");

		try {
			apexDao.initConnection(APEX_DB_USER, APEX_PASSWORD, APEX_DB_URL);
			apexDao.getExistingItemcode((ArrayList<String>) imageDrive.getAllImageWithItemCodeInFolder());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("end apexdao");

		// TODO 3, based on 1 , read item level codes in Hybris

		HybrisDao hybrisDao = new HybrisDao();

		String HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";

		String HYBRIS_USER = "jhung";
		String HYBRIS_PASSWORD = "hhj1101";

		try {
			hybrisDao.initConnection(HYBRIS_USER, HYBRIS_PASSWORD, HYBRIS_DRIVER_URL);
			hybrisDao.getExistingItemcode((ArrayList<String>) imageDrive.getAllImageWithItemCodeInFolder());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("end hybrisdao");
		// TODO 4 based on 11 read item position level code in Hybris

		// TODO output

	}

}
