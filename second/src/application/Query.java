package application;

import com.opencsv.bean.CsvBind;


public class Query {
	
	@CsvBind
	private String key;
	@CsvBind
	private String type;
	@CsvBind
	private String name;
	@CsvBind
	private String query;
	@CsvBind
	private String email;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
