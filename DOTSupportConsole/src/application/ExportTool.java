package application;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableArrayList;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;


public class ExportTool {
	public String ListToHTML(ArrayList<String> headers, ArrayList<ArrayList<String>> rows){
		
		StringBuilder style = new StringBuilder();
		
		style.append(" table {color: #333;font-family: Helvetica, Arial, sans-serif;width: 640px;border-collapse:collapse; border-spacing: 0;} ");
		style.append(" td, th { border: 1px solid #CCC; height: 30px;max-width: 100px;word-break:break-all; }");
		style.append(" td { background: #F3F3F3;font-weight: bold;background-color:#66c2ff;text-align: center;}");
		style.append(" th {background: #FAFAFA;text-align: right; }");
		//style.append(" .header {position:fixed; }");
     
		
		Document doc = Jsoup.parse("<html></html>");
		doc.head().appendElement("style").text(style.toString());
		Element table =doc.body().appendElement("table");
		table.attr( "border","1");
		table.attr( "style","border:1px solid black;border-collapse: separate; border-spacing: 1px; cellspacing=1;margin: 0 auto;");
		//table.attr( "style","border:5px black;cellpadding:5;cellspacing=0;cellpadding:10;");
		//background-color:SteelBlue;color:Wheat
		// style="border-collapse:collapse;background-color:Wheat;color:black;font-family:arial,helvetica,sans-serif;"
		Element head = table.appendElement("tr");
		//head.attr( "style","background-color:#660034;color:White;font-family:arial,helvetica,sans-serif;");
		head.attr( "class","header");
		
		for (String temp : headers) {
			head.appendElement("td").text(temp);
			System.out.println(temp);
		}
		
		for (ArrayList<String> row : rows) {
			Element rowElement = table.appendElement("tr");
			for (String cell : row) {
				//System.out.println("---->"+cell);
				//System.out.println("---->"+cell.isEmpty());
				
				if (cell == null){ cell="//NULL";}
				rowElement.appendElement("th").text(cell.toString());
				System.out.println("---->"+cell);
				
			}
		}
		
		
   System.out.println(doc.toString());
		
		return doc.toString();
		
	}

	public ArrayList<String>  getHeadersFromResultset(ResultSet rs) throws SQLException
	{
		ArrayList<String> headers = new ArrayList<String> ();
		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			// We are using non property style for making dynamic table
			final int j = i;
			headers.add(rs.getMetaData().getColumnLabel(i+1));
			/*
			System.out.println(">>>"+rs.getMetaData().getColumnName(i+1));
			System.out.println(">>>"+rs.getMetaData().getSchemaName(i+1));
			System.out.println(">>>"+rs.getMetaData().getColumnLabel(i+1));
			*/
			
		}
		
		
		return headers;
		
	}
	
	public ArrayList<ArrayList<String>>  getRowsFromResultset(ResultSet rs) throws SQLException
	{
		ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
		
		while (rs.next()) {
			// Iterate Row
			ArrayList<String> row = new ArrayList<String>();
			//ObservableList<String> row = FXCollections.observableArrayList();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				// Iterate Column
				
				row.add(rs.getString(i));
			}
			rows.add(row);
			System.out.println("Row [1] added " + row);
			

		}
		
		return rows;
		
	}
	
	
	public TableView ResultsetToTableView(java.sql.ResultSet rs) throws SQLException {

		TableView tv = new TableView();
		ObservableList<ObservableList> data = FXCollections.observableArrayList();
		;

		/**
		 * ******************************** TABLE COLUMN ADDED DYNAMICALLY *
		 * ********************************
		 */
		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
			// We are using non property style for making dynamic table
			final int j = i;
			TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
			col.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
						public ObservableValue<String> call(
								TableColumn.CellDataFeatures<ObservableList, String> param) {
							return new SimpleStringProperty(param.getValue().get(j).toString());
						}
					});

			tv.getColumns().addAll(col);
			System.out.println("Column [" + i + "] ");
		}

		/**
		 * ****************************** Data added to ObservableList *
		 * ******************************
		 */
		while (rs.next()) {
			// Iterate Row
			ObservableList<String> row = FXCollections.observableArrayList();
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				// Iterate Column
				row.add(rs.getString(i));
			}
			System.out.println("Row [1] added " + row);
			data.add(row);

		}

		// FINALLY ADDED TO TableView
		tv.setItems(data);

		System.out.println(tv.toString());
		return tv;

	}

	private void convertToExcel(ResultSet rs) throws SQLException, FileNotFoundException {

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("employe db");
		XSSFRow row = sheet.createRow(0);

		// XSSFCell cell;
		// rowhead.createCell((short) 0).setCellValue("Roll No");
		// rowhead.createCell((short) 0).setCellValue("Roll No");
		// PrintWriter csvWriter = new PrintWriter(new File("whatever.csv")) ;
		ResultSetMetaData meta = rs.getMetaData();
		int numberOfColumns = meta.getColumnCount();
		// String dataHeaders = "\"" + meta.getColumnName(1) + "\"" ;
		int i = 1;
		for (i = 0; i < numberOfColumns; i++) {
			System.out.println(meta.getColumnName(i + 1).toString());
			row.createCell(i).setCellValue(meta.getColumnName(i + 1).toString());
		}
		int index = 1;
		int j = 1;

		while (rs.next()) {

			row = sheet.createRow(index);
			for (j = 0; j < numberOfColumns; j++) {
				System.out.println(rs.getString(j + 1));
				row.createCell(j).setCellValue(rs.getString(j + 1));
			}
			j = 1;
			index++;
		}

		try {
			FileOutputStream out = new FileOutputStream(new File("exceldatabase.xlsx"));
			wb.write(out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Data is saved in excel file.");

	}

	public void convertToCsv(TableView tv, ObservableList<ObservableList> data) throws FileNotFoundException {

		TableColumn col;
		ObservableList<String> rows;
		StringBuilder dataHeaders = new StringBuilder();
		StringBuilder dataRow = new StringBuilder();
		PrintWriter csvWriter = new PrintWriter(new File("whatever.csv"));
		int numberOfColumns = tv.getColumns().size();
		int numberOfRows = tv.getItems().size();

		for (int i = 0; i < numberOfColumns; i++) {
			col = (TableColumn) tv.getColumns().get(i);
			System.out.println("dddd" + col.getText() + "pppp");
			dataHeaders.append(",\"" + col.getText() + "\"");

		}
		csvWriter.println(dataHeaders);

		for (ObservableList<String> tmp : data) {
			dataRow = new StringBuilder();
			System.out.println("%%%%%  " + tmp.toString());
			for (String cell : tmp) {
				dataRow.append(",\"" + cell.toString() + "\"");
				System.out.println("XXXXX " + cell.toString());
			}
			csvWriter.println(dataRow);
		}

		csvWriter.close();

	}

	public void convertToExcel(TableView tv, ObservableList<ObservableList> data, String filepath) throws IOException {

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("employe db");
		XSSFRow row = sheet.createRow(0);

		int numberOfColumns = tv.getColumns().size();
		int numberOfRows = tv.getItems().size();

		TableColumn col;

		for (int i = 0; i < numberOfColumns; i++) {
			col = (TableColumn) tv.getColumns().get(i);
			System.out.println("dddd" + col.getText() + "pppp");
			row.createCell(i).setCellValue(col.getText());
		}

		int index = 1;
		int j = 0;

		for (ObservableList<String> tmp : data) {

			row = sheet.createRow(index);
			
				for (String cell : tmp) {
					System.out.println("YYYYY"+cell.toString());
					//String callstring = new String(cell.toString());
					row.createCell(j).setCellValue(cell.toString());
					j  ++;
				}
			j=0;
			
			index++;
		}
	
		
		FileOutputStream out = new FileOutputStream(new File(filepath));
		wb.write(out);
		out.close();

	}
	public void convertToExcel(ArrayList<String> headers, ArrayList<ArrayList<String>> rows, String filepath) throws IOException 
	{
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("employe db");
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
					if (cell == null){ cell="//NULL";}
					System.out.println("YYYYY"+cell.toString());
					//String callstring = new String(cell.toString());
					row.createCell(j).setCellValue(cell.toString());
					j  ++;
				}
			j=0;
			
			index++;
		}
	
		
		FileOutputStream out = new FileOutputStream(new File(filepath));
		wb.write(out);
		out.close();
	}
	
}
