package basic.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public final class SystemConfig {

	// the configuration file is stored in the root of the class path as a
	// .properties file
	private static final String CONFIGURATION_FILE = "c:\\tmp\\configuration.properties";
	//
	// private static final Properties properties;
	//
	// // use static initializer to read the configuration file when the class
	// is loaded
	// static {
	// properties = new Properties();
	// try (InputStream inputStream =
	// Configuration.class.getResourceAsStream(CONFIGURATION_FILE)) {
	// properties.load(inputStream);
	// } catch (IOException e) {
	// throw new RuntimeException("Failed to read file " + CONFIGURATION_FILE,
	// e);
	// }
	// }
	//
	// public static Map<String, String> getConfiguration() {
	// // ugly workaround to get String as generics
	// Map temp = properties;
	// Map<String, String> map = new HashMap<String, String>(temp);
	// // prevent the returned configuration from being modified
	// return Collections.unmodifiableMap(map);
	// }
	//
	//
	// public static String getConfigurationValue(String key) {
	// return properties.getProperty(key);
	// }

	// private constructor to prevent initialization

	private static String HYBRIS_USER = null;
	private static String HYBRIS_PASSWORD = null;

	public static String getHYBRIS_USER() {
		return HYBRIS_USER;
	}

	public static String getHYBRIS_PASSWORD() {
		return HYBRIS_PASSWORD;
	}

	private SystemConfig() {
		 Yaml yaml = new Yaml();
		   
	      try {
	         InputStream ios = new FileInputStream(new File(CONFIGURATION_FILE));
	         
	         // Parse the YAML file and return the output as a series of Maps and Lists
	         Map<String,Object> result = (Map<String,Object>)yaml.load(ios);
	         System.out.println(result.toString());
	         
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		System.out.println("  private SystemConfig");

	}

}
