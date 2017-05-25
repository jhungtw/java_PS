import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class APEXDaoTest {

	@SuppressWarnings("unchecked")
	@Test
	
	public void testApex() {
		APEXDao dao = new APEXDao();
		String dburl="jdbc:sqlserver://cs-pdb-mirror;databaseName=Apex;integratedSecurity=true;";
		String username="RSSCL\\jhung";
		String pwd="Hhj1101@";
		
		try {
			dao.initConnection(username, pwd, dburl);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List items = new ArrayList<String>();
		items.add("205750");
		items.add("206750");
		items.add("207750");
		List result ;
		try {
			result = dao.getExistingItemcode((ArrayList<String>) items);
			showListContent(result);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void testHybrisItemPOsition() {
		HybrisDao dao = new HybrisDao();
		String dburl="jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";
		String username="jhung";
		String pwd="hhj1101";
		
		try {
			dao.initConnection(username, pwd, dburl);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List items = new ArrayList<String>();
		items.add("29088-2");
		items.add("29121-2");
		items.add("29903-3");
		List result ;
		try {
			result = dao.getExistingItemPositioncode((ArrayList<String>) items);
			showListContent(result);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void testHybrisItem() {
		HybrisDao dao = new HybrisDao();
		String dburl="jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";
		String username="jhung";
		String pwd="hhj1101";
		
		try {
			dao.initConnection(username, pwd, dburl);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List items = new ArrayList<String>();
		items.add("125426126");
		items.add("125427750");
		items.add("132966901");
		List result ;
		try {
			result = dao.getExistingItemcode((ArrayList<String>) items);
			showListContent(result);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	private static void showListContent(List tDriveWithPosition) {
		// TODO Auto-generated method stub

		for (String tmp : (ArrayList<String>) tDriveWithPosition) {
			System.out.println("--> " + tmp);

		}
	}

}
