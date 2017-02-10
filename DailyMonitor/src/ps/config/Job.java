package ps.config;

import java.util.Map;



public class Job {

	private String type;
	private String name;
	private String displayname;
	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	private String scheduled;
	private boolean today;
	private String status;
	private String startTime;
	private String endTime;
	private String duration;

	private Map<Integer, Job> subjobs;

	public Map<Integer, Job> getSubjobs() {
		return subjobs;
	}

	public void setSubjobs(Map<Integer, Job> subjobs) {
		this.subjobs = subjobs;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
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

	public String getScheduled() {
		return scheduled;
	}

	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
	}

	public boolean isToday() {
		return today;
	}

	public void setToday(boolean today) {
		this.today = today;
	}

	@Override
	public String toString() {
		return "Report [type=" + type + ", name=" + name + ", scheduled=" + scheduled + ", today=" + today + ", status="
				+ status + ", startTime=" + startTime + ", endTime=" + endTime + ", duration=" + duration + ", subjobs="
				+ subjobs + "]";
	}

}
