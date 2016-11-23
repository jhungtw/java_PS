package application;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import com.mysql.jdbc.Driver;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

public class MainGUIController implements Initializable {

	@FXML
	BorderPane maincontent;

	@FXML
	Button searchBtn;

	@FXML
	TextArea ta;

	@FXML
	TableView tableview;

	@FXML
	ComboBox<?> env;

	@FXML
	private ComboBox<?> format;

	@FXML
	private ComboBox<String> query;

	@FXML
	private VBox qvbox;

	@FXML
	private TextField path;

	@FXML
	private Button saveas;

	@FXML
	TextArea buttomta;

	ObservableList<ObservableList> data;
	private String HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";

	private String HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";

	private String HYBRIS_USER = "jhung";
	private String HYBRIS_PASSWORD = "hhj1101";

	private Map<String, Query> querySel;

	@FXML
	void initialize() {
		assert maincontent != null : "fx:id=\"maincontent\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert ta != null : "fx:id=\"ta\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert searchBtn != null : "fx:id=\"searchBtn\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert env != null : "fx:id=\"env\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert format != null : "fx:id=\"format\" was not injected: check your FXML file 'MainGUI.fxml'.";
		System.out.println("before");
		assert query != null : "fx:id=\"query\" was not injected: check your FXML file 'MainGUI.fxml'.";
		System.out.println("after");
		assert tableview != null : "fx:id=\"tableview\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert qvbox != null : "fx:id=\"qvbox\" was not injected: check your FXML file 'MainGUI.fxml'.";

	}

	@FXML
	private void AddQueryToTxttArea(ActionEvent event) {
		System.out.println("Clicked!");

		ta.setText(querySel.get(query.getValue()).getQuery());

	}

	@FXML
	private void SaveAsFile(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("NOTIFICATION");
		alert.setHeaderText("Information Alert");
		if ((ta != null) && (data != null)) {
			try {
				new ExportTool().convertToExcel(tableview, data, path.getText());
				String s = "Export is done";
				alert.setContentText(s);
				alert.show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			String s = "Please do search first";
			alert.setContentText(s);
			alert.show();
		}
	}

	@FXML
	private void ChangeEnv(ActionEvent event) {
		System.out.println("Clicked!");
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

		// ta.setText(querySel.get(query.getValue()).getQuery());

	}

	@SuppressWarnings("unchecked")
	@FXML
	private void search(ActionEvent event) {
		
		buttomta.setText("Search is running since " + LocalDateTime.now() + "Please wait\n");
		System.out.println("Search begin!");
		
		try {
			tableview = new TableView();

			data = FXCollections.observableArrayList();

			StringBuilder FQSB = new StringBuilder();
			FQSB.append(ta.getText());

			Class.forName(HYBRIS_DRIVER_CLASS).newInstance();

			Connection connection = DriverManager.getConnection(HYBRIS_DRIVER_URL, HYBRIS_USER, HYBRIS_PASSWORD);

			// ResultSet rs = new ReportDao().FlexableQuery(HYBRIS_DRIVER_URL,
			// HYBRIS_USER, HYBRIS_PASSWORD, FQstr.toString());
			ResultSet rs = new SearchDAO().FlexableQuery(connection, FQSB.toString());

			/**
			 * ******************************** TABLE COLUMN ADDED DYNAMICALLY *
			 * ********************************
			 */
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				// We are using non property style for making dynamic table
				final int j = i;
				TableColumn col = new TableColumn(rs.getMetaData().getColumnLabel(i+1));
				/*
				System.out.println(">>>"+rs.getMetaData().getColumnName(i+1));
				System.out.println(">>>"+rs.getMetaData().getSchemaName(i+1));
				System.out.println(">>>"+rs.getMetaData().getColumnLabel(i+1));
				*/
				col.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								return new SimpleStringProperty(param.getValue().get(j).toString());
							}
						});

				tableview.getColumns().addAll(col);
				//System.out.println("Column [" + i + "] ");
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
			tableview.setItems(data);

			maincontent.setCenter(tableview);
/*
			for (ObservableList<String> tmp : data) {
				System.out.println(tmp.toString());
			}
*/
			rs.close();

			connection.close();
			// defaultQuery1(rs);

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("NOTIFICATION");
			alert.setHeaderText("Information Alert");

			alert.setContentText("Search is done");
			alert.show();

		} catch (Exception ex) {
			buttomta.setText(ex.getMessage());
			ex.printStackTrace();

		}
		buttomta.appendText("Search is dont at " + LocalDateTime.now());
		System.out.println("Load!");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		assert maincontent != null : "fx:id=\"maincontent\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert ta != null : "fx:id=\"ta\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert searchBtn != null : "fx:id=\"searchBtn\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert saveas != null : "fx:id=\"saveas\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert env != null : "fx:id=\"env\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert format != null : "fx:id=\"format\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert qvbox != null : "fx:id=\"qvbox\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert query != null : "fx:id=\"query\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert path != null : "fx:id=\"path\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert tableview != null : "fx:id=\"tableview\" was not injected: check your FXML file 'MainGUI.fxml'.";
		assert buttomta != null : "fx:id=\"buttomta\" was not injected: check your FXML file 'MainGUI.fxml'.";

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
			buttomta.setText(e.getMessage());
		}

	}

	
	@FXML
	public void CheckFolder() {
		// TODO Auto-generated method stub
		
		System.out.println(path.getText());
		try {
			Process p = new ProcessBuilder("explorer.exe", "/select,c:\\tmp\\out.txt").start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
