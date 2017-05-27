import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import utils.SQLStringUtils;

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

	public List<String> getExistingItemcode(List<String> items) throws ClassNotFoundException, SQLException {
		this.showListContent(items);
		;

		int noOfItems = items.size();
		List<String> inApexItems = new ArrayList<String>();
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
			System.out.println(inApexItems.size());
		}

		return inApexItems;
	}

	public List<String> getExistingItempositioncode(List<String> items) throws ClassNotFoundException, SQLException {
		// load the properties
		// get the properties

		this.showListContent(items);
		;

		int noOfItems = items.size();
		List<String> inApexItems = new ArrayList<String>();
		System.out.println(items.size());
		StringBuilder sql = new StringBuilder();
		sql.append(
				"select distinct concat(i.item_code,'-',s.position_key) as positioncode from dbo.item as i join dbo.sku as s on s.item_key= i.item_key where concat(i.item_code,'-',s.position_key) in (");

		int i = 0;
		if (noOfItems > 0) {
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

				inApexItems.add(rset.getString("positioncode"));
			}
			System.out.println(inApexItems.size());
		}

		return(ArrayList<String>) inApexItems;
	}

	public List<String> getNewItempositioncode(List<String> items) throws ClassNotFoundException, SQLException {
		// load the properties
		// get the properties

		this.showListContent(items);
		;

		int noOfItems = items.size();
		List<String> inApexItems = new ArrayList<String>();
		System.out.println(items.size());
		StringBuilder sql = new StringBuilder();
		String sqlstring = "select distinct concat(i.item_code,'-',s.position_key) as positioncode from dbo.item as i join dbo.sku as s on s.item_key= i.item_key where CAST(create_date AS date) = ?today and concat(i.item_code,'-',s.position_key) in (";
		DateTime dt = new DateTime();
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");

		sqlstring = SQLStringUtils.updatePlaceholder(sqlstring, "today", dtf.print(dt));
		sql.append(sqlstring);

		int i = 0;
		if (noOfItems > 0) {
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

				inApexItems.add(rset.getString("positioncode"));
			}
			System.out.println(inApexItems.size());
		}

		return  inApexItems;
	}
	public List<String> getNewItemCode(List<String> items) throws ClassNotFoundException, SQLException {
		// load the properties
		// get the properties

		this.showListContent(items);
		;

		int noOfItems = items.size();
		List<String> inApexItems = new ArrayList<String>();
		System.out.println(items.size());
		StringBuilder sql = new StringBuilder();
		String sqlstring ="select item_code from dbo.item where CAST(create_date AS date) = ?today and item_code in (";
		DateTime dt = new DateTime();
		DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");

		sqlstring = SQLStringUtils.updatePlaceholder(sqlstring, "today", dtf.print(dt));
		sql.append(sqlstring);

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
			System.out.println(inApexItems.size());
		}

		return inApexItems;
	}
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) throws SQLException {

		this.conn = conn;
	}

	private void showListContent(List<String> tDriveWithPosition) {
		System.out.println("XXXXXXXX ");
		for (String tmp : (ArrayList<String>) tDriveWithPosition) {
			System.out.println("--> " + tmp);

		}
	}

}
