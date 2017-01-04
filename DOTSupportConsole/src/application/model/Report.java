package application.model;

public class Report {
	private String  type;
	private String name;
	private String content;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getEmail() {
		return email;
	}
	@Override
	public String toString() {
		return "Report [type=" + type + ", name=" + name + ", content=" + content + ", email=" + email + "]";
	}
	public void setEmail(String email) {
		this.email = email;
	}
	private String email;
	public String toDisplayName() {
		return type + " - " + name ;
	}
}
