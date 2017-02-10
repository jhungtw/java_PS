package ps.config;

import java.util.Map;



public class MapBean {

	
	private Map<String, String> configrations;

	public Map<String, String> getConfigrations() {
		return configrations;
	}

	public void setConfigrations(Map<String, String> configrations) {
		this.configrations = configrations;
	}

	private Map<Integer, Job> reports;
	


	public Map<Integer, Job> getReports() {
		return reports;
	}

	public void setReports(Map<Integer, Job> reports) {
		this.reports = reports;
	}
	



}
