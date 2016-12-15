package application;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import application.model.Query;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.TextArea;

public class MainUIController implements Initializable {
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextArea queryinput;

	@FXML
	private ComboBox<String> query;

	@FXML
	private WebView view;

	@FXML
	private TextArea log;

	@FXML
	private Button btn;

	@FXML
	private Button save;

	@FXML
	private ComboBox<?> env;

	private WebEngine engine;

	private String HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";

	private String HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";

	private String HYBRIS_USER = "jhung";
	private String HYBRIS_PASSWORD = "hhj1101";

	private Map<String, Query> querySel;
	private ArrayList<String> headers = new ArrayList<String>();
	private ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();

	@FXML
	void show(ActionEvent event) {
		DisableVisualsWhenSearch();
		ResultSet rs;
		Connection connection;
		log.setText("search is starting \n");
		headers = new ArrayList<String>();
		rows = new ArrayList<ArrayList<String>>();
		/*
		 * // add 4 different values to list headers.add("eBay");
		 * headers.add("Paypal"); headers.add("Name"); headers.add("email");
		 * 
		 * ArrayList<ArrayList<String>> rows = new
		 * ArrayList<ArrayList<String>>();
		 * 
		 * List<String> r1 = new ArrayList<String>(); r1.add("1"); r1.add("2");
		 * r1.add("300000"); r1.add("400000"); List<String> r2 = new
		 * ArrayList<String>(); r2.add("1"); r2.add("2"); r2.add(
		 * "3000001111111111111111111111111111111111111111111111111111111111111111111111111111"
		 * ); r2.add("400000"); List<String> r3 = new ArrayList<String>();
		 * r3.add("1"); r3.add("2"); r3.add("300000"); r3.add("400000");
		 * rows.add((ArrayList<String>)r1); rows.add((ArrayList<String>) r2);
		 * rows.add((ArrayList<String>) r3);
		 */
		try {

			StringBuilder FQSB = new StringBuilder();
			FQSB.append(queryinput.getText());
			Class.forName(HYBRIS_DRIVER_CLASS).newInstance();

			connection = DriverManager.getConnection(HYBRIS_DRIVER_URL, HYBRIS_USER, HYBRIS_PASSWORD);

			// ResultSet rs = new ReportDao().FlexableQuery(HYBRIS_DRIVER_URL,
			// HYBRIS_USER, HYBRIS_PASSWORD, FQstr.toString());
			rs = new SearchDAO().FlexableQuery(connection, FQSB.toString());

			// System.out.println(new ExportTool().ListToHTML(headers, rows));
			// engine.setUserStyleSheetLocation("style.css");
			// engine.setUserStyleSheetLocation(value);
			ExportTool et = new ExportTool();
			headers = et.getHeadersFromResultset(rs);
			System.out.println("headers" + headers.size());
			rows = et.getRowsFromResultset(rs);
			System.out.println("rows" + rows.size());
			engine.loadContent(new ExportTool().ListToHTML(headers, rows));
			rs.close();

			connection.close();
			log.appendText("search is successfully");

		} catch (Exception e) {

			// TODO Auto-generated catch block

			e.printStackTrace();
			// log.appendText(e.getMessage());
		}
		finally{
			EnableVisualsSearchDone();
		}

	}

	@FXML
	private void ChangeEnv(ActionEvent event) {
		log.appendText("change env clicked \n");
		String selected;
		selected = (String) env.getValue();
		switch (selected) {
		case "Prod":
			HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";
			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";
			break;
		case "Bugfix":
			HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";
			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://bugfix.totalwine.com/virtualjdbc/service";
			break;
		case "UAT":
			HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";
			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://uat.totalwine.com/virtualjdbc/service";
			break;
		case "Local":
			HYBRIS_DRIVER_CLASS = "com.mysql.jdbc.Driver";
			HYBRIS_DRIVER_URL = "jdbc:mysql://localhost:3306/sakila";
			break;

		default:
			HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";
			HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://bugfix.totalwine.com/virtualjdbc/service";
		}

		log.appendText("change env done \n");

	}

	@FXML
	void save(ActionEvent event) {
		log.setText("save is starting");

		if (headers.size() > 0) {
			ExportTool et = new ExportTool();
			try {
				et.convertToExcel(headers, rows, "c:\\tmp\\out.xlsx");
				log.appendText("save is successfully");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.appendText(e.getMessage());
				e.printStackTrace();
			}
		}

	}

	@FXML
	private void AddQueryToTxttArea(ActionEvent event) {
		log.appendText("Add query clicked");

		queryinput.setText(querySel.get(query.getValue()).getQuery());

		log.appendText("Add query done");

	}

	@FXML
	void initialize() {
		assert btn != null : "fx:id=\"btn\" was not injected: check your FXML file 'MainUI.fxml'.";
		engine = view.getEngine();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		engine = view.getEngine();
		initData();

	}

	private void initData() {

		querySel = new HashMap<String, Query>();
		ObservableList<String> options = FXCollections.observableArrayList();

		ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
		strat.setType(Query.class);
		String[] columns = new String[] { "key", "type", "name", "query", "email" };
		strat.setColumnMapping(columns);

		CsvToBean csv = new CsvToBean();

		CSVReader reader;
		char retChar = '|';
		try {
			reader = new CSVReader(new FileReader("C:\\tmp\\query.csv"), retChar);
			List<Query> list = csv.parse(strat, reader);
			for (Query cf : list) {
				System.out.println(cf.getKey() + "---" + cf.getQuery());
				options.add(cf.getKey() + "-" + cf.getType() + "-" + cf.getName());
				querySel.put(cf.getKey() + "-" + cf.getType() + "-" + cf.getName(), cf);
			}
			query.setItems(options);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.setText(e.getMessage());
		}

	}

	private void DisableVisualsWhenSearch() {
		queryinput.setDisable(true);
		query.setDisable(true);
		save.setDisable(true);
		env.setDisable(true);

	}

	private void EnableVisualsSearchDone() {
		queryinput.setDisable(false);
		query.setDisable(false);
		save.setDisable(false);
		env.setDisable(false);

	}

}
