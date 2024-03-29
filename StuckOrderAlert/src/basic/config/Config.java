package basic.config;

import java.io.Serializable;

public class Config implements Serializable{
	
	
	private String key;
	
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Config [key=" + key + ", value=" + value + "]";
	}

	public Config(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
	


	
	
}
