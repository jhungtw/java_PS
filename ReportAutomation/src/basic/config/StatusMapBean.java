package basic.config;

import java.util.Map;

public class StatusMapBean {

	@Override
	public String toString() {
		return "MapBean [reports=" + reports + "]";
	}

	private Map<String, DoneStatus> reports;

	public Map<String, DoneStatus> getReports() {
		return reports;
	}

	public void setReports(Map<String, DoneStatus> reports) {
		this.reports = reports;
	}

	
}
