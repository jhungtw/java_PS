package basic.config;

import java.util.Map;



public class MapBean {

	

	@Override
	public String toString() {
		return "MapBean [reports=" + reports + ", configrations=" + configrations + "]";
	}

	private Map<String, Report> reports;
	
	private Map<String, String> configrations;

	public Map<String, Report> getReports() {
		return reports;
	}

	public void setReports(Map<String, Report> reports) {
		this.reports = reports;
	}
	

	public Map<String, String> getConfigrations() {
		return configrations;
	}

	public void setConfigrations(Map<String, String> configrations) {
		this.configrations = configrations;
	}
	

}
