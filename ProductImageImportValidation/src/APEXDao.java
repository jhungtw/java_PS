import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class APEXDao {

	private static String driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

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

	@SuppressWarnings("unchecked")
	public ArrayList<String> getExistingItemcode(ArrayList<String> items) throws ClassNotFoundException, SQLException {
		// load the properties
		// get the properties
		int noOfItems = items.size();
		List inApexItems = new ArrayList<String>();
		System.out.println(items.size());
		StringBuilder sql = new StringBuilder();

		sql.append("select item_code from dbo.item where item_code in (");

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

				inApexItems.add(rset.getString("item_code"));
			}
		}

		return (ArrayList<String>) inApexItems;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getExistingItempositioncode(ArrayList<String> items)
			throws ClassNotFoundException, SQLException {
		// load the properties
		// get the properties
		int noOfItems = items.size();
		List inApexItems = new ArrayList<String>();
		System.out.println(items.size());
		StringBuilder sql = new StringBuilder();
// TODO change sql to find itemposition
		sql.append("select item_code from dbo.item where item_code in (");

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

				inApexItems.add(rset.getString("item_code"));
			}
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
