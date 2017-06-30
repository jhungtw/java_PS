package basic.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class JDBCUtils {

	public static ArrayList<String> getHeadersFromResultset(ResultSet rs) throws SQLException {
		ArrayList<String> headers = new ArrayList<String>();
		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			// We are using non property style for making dynamic table
			// final int j = i;
			headers.add(rs.getMetaData().getColumnLabel(i + 1));
			/*
			 * System.out.println(">>>"+rs.getMetaData().getColumnName(i+1));
			 * System.out.println(">>>"+rs.getMetaData().getSchemaName(i+1));
			 * System.out.println(">>>"+rs.getMetaData().getColumnLabel(i+1));
			 */
	
		}
	
		return headers;
	
	}

	public static ArrayList<ArrayList<String>> getRowsFromResultset(ResultSet rs) throws SQLException {
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
	
		while (rs.next()) {
			// Iterate Row
			ArrayList<String> row = new ArrayList<String>();
			// ObservableList<String> row = FXCollections.observableArrayList();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				// Iterate Column
	
				row.add(rs.getString(i));
			}
			rows.add(row);
			// System.out.println("Row [1] added " + row);
	
		}
	
		return rows;
	
	}

	public static void saveResultsetToCSV(ResultSet rs, String filepath) throws FileNotFoundException, SQLException {
	
		// TableColumn col;
		// ObservableList<String> rows;
	
		if (!rs.isBeforeFirst()) {
			rs.beforeFirst();
		}
		System.out.println("save file as: " + filepath);
		StringBuilder dataHeaders = new StringBuilder();
		StringBuilder dataRow = new StringBuilder();
		PrintWriter csvWriter = new PrintWriter(new File(filepath));
	
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
	
		headers = getHeadersFromResultset(rs);
		// accessLog.info("headers" + headers.size());
		rows = getRowsFromResultset(rs);
	
		int numberOfColumns = headers.size();
		int numberOfRows = rows.size();
	
		for (int i = 0; i < numberOfColumns; i++) {
	
			if (i == 0) {
				dataHeaders.append(headers.get(i).toString());
			} else {
				dataHeaders.append("," + headers.get(i).toString());
			}
	
			// System.out.println("dddd" + headers.get(i).toString() + "pppp");
	
		}
		csvWriter.println(dataHeaders);
		int j = 0;
	
		for (ArrayList<String> tmp : rows) {
			dataRow = new StringBuilder();
			// for debug
			// System.out.println("%%%%% " + tmp.toString());
			j = 0;
			for (String cell : tmp) {
	
				if (j == 0) {
					if (tmp.get(j) == null) {
						dataRow.append("");
					} else {
						dataRow.append(tmp.get(j).toString());
					}
				} else {
					if (tmp.get(j) == null) {
						dataRow.append(",");
					} else {
						dataRow.append("," + tmp.get(j).toString());
					}
				}
	
				j++;
	
				// System.out.println("XXXXX " + cell.toString());
			}
			csvWriter.println(dataRow);
	
		}
	
		csvWriter.close();
	
	}

	public static void saveResultsetToExcel(String reportname, ResultSet rs, String filepath)
			throws SQLException, IOException, GeneralSecurityException {
		// ***********************************************************
		// implement save file
		// ***********************************************************
	
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
	
		headers = getHeadersFromResultset(rs);
		// accessLog.info("headers" + headers.size());
		rows = getRowsFromResultset(rs);
		// accessLog.info("rows" + rows.size());
	
		// String filepath = null;
	
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(reportname);
		XSSFRow row = sheet.createRow(0);
	
		int numberOfColumns = headers.size();
	
		for (int i = 0; i < numberOfColumns; i++) {
	
			// System.out.println("dddd" + headers.get(i) + "pppp");
			row.createCell(i).setCellValue(headers.get(i));
		}
	
		int index = 1;
		int j = 0;
	
		for (ArrayList<String> tmp : rows) {
	
			row = sheet.createRow(index);
	
			for (String cell : tmp) {
				if (cell == null) {
					cell = "//NULL";
				}
				// System.out.println("YYYYY" + cell.toString());
				// String callstring = new String(cell.toString());
				row.createCell(j).setCellValue(cell.toString());
				j++;
			}
			j = 0;
	
			index++;
		}
	
		FileOutputStream out = new FileOutputStream(new File(filepath));
	
		wb.write(out);
		out.close();
		wb.close();
	
	}

	public static void saveResultsetToExcelWithPassword(String reportname, ResultSet rs, String filepath,
			String password) throws SQLException, IOException, GeneralSecurityException {
		// ***********************************************************
		// implement save file
		// ***********************************************************
	
		ArrayList<String> headers = new ArrayList<String>();
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
	
		headers = getHeadersFromResultset(rs);
		// accessLog.info("headers" + headers.size());
		rows = getRowsFromResultset(rs);
		// accessLog.info("rows" + rows.size());
	
		// String filepath = null;
	
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(reportname);
		XSSFRow row = sheet.createRow(0);
	
		int numberOfColumns = headers.size();
	
		for (int i = 0; i < numberOfColumns; i++) {
	
			System.out.println("dddd" + headers.get(i) + "pppp");
			row.createCell(i).setCellValue(headers.get(i));
		}
	
		int index = 1;
		int j = 0;
	
		for (ArrayList<String> tmp : rows) {
	
			row = sheet.createRow(index);
	
			for (String cell : tmp) {
				if (cell == null) {
					cell = "//NULL";
				}
				System.out.println("YYYYY" + cell.toString());
				// String callstring = new String(cell.toString());
				row.createCell(j).setCellValue(cell.toString());
				j++;
			}
			j = 0;
	
			index++;
		}
	
		// dsiable by test
		// FileOutputStream out = new FileOutputStream(new File(filepath));
	
		// prepare password
		EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile, CipherAlgorithm.aes192, HashAlgorithm.sha384, -1,
				-1, null);
	
		Encryptor enc = info.getEncryptor();
		enc.confirmPassword(password);
	
		// Encrypt
		// OutputStream out = enc.getDataStream(new POIFSFileSystem());
		POIFSFileSystem fs = new POIFSFileSystem();
		OutputStream out = enc.getDataStream(fs);
	
		wb.write(out);
		// out.close();
		// wb.close();
	
		// Save
		FileOutputStream fos = new FileOutputStream(filepath);
		fs.writeFilesystem(fos);
		fos.close();
		wb.close();
	
	}

	public static String getHybrisDriverURL(String env) {
		String HYBRIS_DRIVER_URL = null;
		switch (env.toLowerCase()) {
		case "prod":
	
			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";
			break;
		case "bugfix":
	
			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://bugfix.totalwine.com/virtualjdbc/service";
			break;
		case "uat":
	
			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://uat.totalwine.com/virtualjdbc/service";
			break;
	
		default:
	
			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://bugfix.totalwine.com/virtualjdbc/service";
		}
	
		return HYBRIS_DRIVER_URL;
	
	}

	public static String addQueryInterval(String query, String from, String to) {
		if (query.contains("[from]") & query.contains("[to]")) {
	
			query = query.replace("[from]", from).replace("[to]", to);
	
		}
	
		return query;
	}

}
