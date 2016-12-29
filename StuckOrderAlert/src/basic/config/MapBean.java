package basic.config;

import java.util.Map;

public class MapBean {

    @Override
	public String toString() {
		return "MapBean [reports=" + configrations + "]";
	}

	private Map<String, String> configrations;

	public Map<String, String> getConfigrations() {
		return configrations;
	}

	public void setConfigrations(Map<String, String> configrations) {
		this.configrations = configrations;
	}

	

}
