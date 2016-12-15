package application.model;

import java.io.Serializable;

public class Query implements Serializable{
	
	
	private String key;
	
	private String type;
	
	private String name;
	
	private String query;
	
	@Override
	public String toString() {
		return "Query [key=" + key + ", type=" + type + ", name=" + name + ", query=" + query + ", email=" + email
				+ "]";
	}
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
