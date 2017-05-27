import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HybrisDao {

	private static String driverClass = "de.hybris.vjdbc.VirtualDriver";

	private Connection conn;

	private String url;
	private String userName;
	private String password;

	/**
	 * Initialize the Dao
	 * 
	 * @param propertiesFile
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void initConnection(String usr, String pwd, String dburl) throws ClassNotFoundException, SQLException {
		// load the properties
		// get the properties
		url = dburl;
		userName = usr;
		password = pwd;

		// load the class
		Class.forName(driverClass);
		this.conn = DriverManager.getConnection(url, userName, password);
	}

	public ArrayList<String> getExistingItemcode(ArrayList<String> items) throws ClassNotFoundException, SQLException {
		List inApexItems = new ArrayList<String>();
		int noOfItems = items.size();
		System.out.println(items.size());
		StringBuilder sql = new StringBuilder();

		sql.append("select {code} from {twmitem} where {catalogversion} = 8796093153881 and  {code} in (");

		int i = 0;
		if (noOfItems > 0) {
			for (String tmp : items) {

				if (noOfItems > (i + 1)) {
					sql.append(tmp).append(",");
				} else {
					sql.append(tmp).append(")");
				}

				i++;

			}

			System.out.println(sql.toString());
			ResultSet rset;

			rset = conn.createStatement().executeQuery(sql.toString());

			while (rset.next()) {

				inApexItems.add(rset.getString("code"));
			}
			System.out.println(inApexItems.size());
		}
		return (ArrayList<String>) inApexItems;
	}

	public ArrayList<String> getExistingItemPositioncode(ArrayList<String> items)
			throws ClassNotFoundException, SQLException {
		List inApexItems = new ArrayList<String>();
		int noOfItems = items.size();
		System.out.println(items.size());
		StringBuilder sql = new StringBuilder();

		

		int i = 0;
		if (noOfItems > 0) {
			sql.append("select {code} from {twmitemposition} where {catalogversion} = 8796093153881 and  {code} in (");
			for (String tmp : items) {

				if (noOfItems > (i + 1)) {
					sql.append("'").append(tmp).append("',");
				} else {
					sql.append("'").append(tmp).append("')");
				}

				i++;

			}

			System.out.println(sql.toString());
			ResultSet rset;
			rset = conn.createStatement().executeQuery(sql.toString());

			while (rset.next()) {

				inApexItems.add(rset.getString("code"));
			}
			System.out.println(inApexItems.size());
		}
		return (ArrayList<String>) inApexItems;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) throws SQLException {

		this.conn = conn;
	}

}
