package basic.config;

public class Report {
	private String type;
	private String name;
	private boolean enabled;
	private String frequency;
	private String trigger_day;

	private String content;
	private String output_format;
	private String output_filename;
	private boolean password_protected;
	private String password;

	private boolean email_notification;
	private String email_to;
	private String email_cc;
	private boolean ftp_notification;
	private String ftp_host;
	private String ftp_folder;
	private String ftp_username;
	private String ftp_password;
	private boolean backup_notification;
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
	public String getTrigger_day() {
		return trigger_day;
	}
	public void setTrigger_day(String trigger_day) {
		this.trigger_day = trigger_day;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOutput_format() {
		return output_format;
	}
	public void setOutput_format(String output_format) {
		this.output_format = output_format;
	}
	public String getOutput_filename() {
		return output_filename;
	}
	public void setOutput_filename(String output_filename) {
		this.output_filename = output_filename;
	}
	public boolean isPassword_protected() {
		return password_protected;
	}
	public void setPassword_protected(boolean password_protected) {
		this.password_protected = password_protected;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isEmail_notification() {
		return email_notification;
	}
	public void setEmail_notification(boolean email_notification) {
		this.email_notification = email_notification;
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
	public boolean isFtp_notification() {
		return ftp_notification;
	}
	public void setFtp_notification(boolean ftp_notification) {
		this.ftp_notification = ftp_notification;
	}
	public String getFtp_host() {
		return ftp_host;
	}
	public void setFtp_host(String ftp_host) {
		this.ftp_host = ftp_host;
	}
	public String getFtp_folder() {
		return ftp_folder;
	}
	public void setFtp_folder(String ftp_folder) {
		this.ftp_folder = ftp_folder;
	}
	public String getFtp_username() {
		return ftp_username;
	}
	public void setFtp_username(String ftp_username) {
		this.ftp_username = ftp_username;
	}
	public String getFtp_password() {
		return ftp_password;
	}
	public void setFtp_password(String ftp_password) {
		this.ftp_password = ftp_password;
	}
	public boolean isBackup_notification() {
		return backup_notification;
	}
	public void setBackup_notification(boolean backup_notification) {
		this.backup_notification = backup_notification;
	}
	@Override
	public String toString() {
		return "Report [type=" + type + ", name=" + name + ", enabled=" + enabled + ", frequency=" + frequency
				+ ", trigger_day=" + trigger_day + ", content=" + content + ", output_format=" + output_format
				+ ", output_filename=" + output_filename + ", password_protected=" + password_protected + ", password="
				+ password + ", email_notification=" + email_notification + ", email_to=" + email_to + ", email_cc="
				+ email_cc + ", ftp_notification=" + ftp_notification + ", ftp_host=" + ftp_host + ", ftp_folder="
				+ ftp_folder + ", ftp_username=" + ftp_username + ", ftp_password=" + ftp_password
				+ ", backup_notification=" + backup_notification + "]";
	}

}
