package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.yaml.snakeyaml.Yaml;

import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

import application.model.Query;
import application.model.MapBean;
import application.model.Report;
import javafx.application.Platform;
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

	private Map<String, Report> querySel;
	private ArrayList<String> headers = new ArrayList<String>();
	private ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();

	@FXML
	void show(ActionEvent event) {
		DisableVisualsWhenSearch();
		ResultSet rs;
		Connection connection;
		updateStatus(new Date().toString() + " | " + "search is starting \n");
		headers = new ArrayList<String>();
		rows = new ArrayList<ArrayList<String>>();
	
	
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
			updateStatus(new Date().toString() + " | " + "search is done \n");

		} catch (Exception e) {

			// TODO Auto-generated catch block

			e.printStackTrace();
			// log.appendText(e.getMessage());
		} finally {
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
		updateStatus(new Date().toString() + " | " + "save is starting \n");

		if (headers.size() > 0) {
			ExportTool et = new ExportTool();
			try {
				et.convertToExcel(headers, rows, "c:\\tmp\\out.xlsx");
				updateStatus(new Date().toString() + " | " + "save is done \n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				updateStatus(new Date().toString() + " | " + e.getMessage() + "\n");

				e.printStackTrace();
			}
		}

	}

	@FXML
	private void AddQueryToTxttArea(ActionEvent event) {
		updateStatus(new Date().toString() + " | " + "Add query clicked \n");

		queryinput.setText(querySel.get(query.getValue()).getContent());

		updateStatus(new Date().toString() + " | " + "Add query done \n");

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
		try {
			initData();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initData() throws FileNotFoundException {

		querySel = new HashMap<String, Report>();
		ObservableList<String> options = FXCollections.observableArrayList();
		/*
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
			updateStatus(new Date().toString() + " | " + e.getMessage());
		}*/
		
		


	
			FileReader reader = new FileReader(new File("c:\\tmp\\configuration1.properties"));
			Yaml yaml = new Yaml();
			MapBean parsed = yaml.loadAs(reader, MapBean.class);
			
			Map<String, Report> data = parsed.getReports();
			
			for (String entry : data.keySet()) {
				System.out.println("Key : " + entry + " Value : " + data.get(entry).toDisplayName());
				options.add(entry + "-" + data.get(entry).toDisplayName());
				querySel.put(entry + "-" + data.get(entry).toDisplayName(), data.get(entry));
			}
			
		
			query.setItems(options);
		
		
		

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

	private void updateStatus(String message) {
		if (Platform.isFxApplicationThread()) {
			log.appendText(message);
		} else {
			Platform.runLater(() -> log.appendText(message));
		}
	}

}
