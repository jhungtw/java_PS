package application.model;

import java.util.Map;

public class MapBean {

    @Override
	public String toString() {
		return "MapBean [reports=" + reports + "]";
	}

	private Map<String, Report> reports;

	public Map<String, Report> getReports() {
		return reports;
	}

	public void setReports(Map<String, Report> reports) {
		this.reports = reports;
	}


}
