package application;

public class DefaultQuery {
	public DefaultQuery(String name, String query, String type) {
		super();
		this.name = name;
		this.query = query;
		this.type = type;
	}
	private String name;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private String query;
	private String type;
	

}
