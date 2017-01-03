package basic.config;

public class Report {
	private String  type;
	private String name;
	private boolean enabled;
	private String frequency;
	private String content;
	private String email_to;
	private String email_cc;
	private String schedule;
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
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getEmail_to() {
		return email_to;
	}
	public void setEmail_to(String email_to) {
		this.email_to = email_to;
	}
	public String getEmail_cc() {
		return email_cc;
	}
	public void setEmail_cc(String email_cc) {
		this.email_cc = email_cc;
	}
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	@Override
	public String toString() {
		return "Report [type=" + type + ", name=" + name + ", enabled=" + enabled + ", frequency=" + frequency
				+ ", content=" + content + ", email_to=" + email_to + ", email_cc=" + email_cc + ", schedule="
				+ schedule + "]";
	}
	
	
	
}
