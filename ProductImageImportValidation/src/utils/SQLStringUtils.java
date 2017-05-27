package utils;

import java.util.List;

public class SQLStringUtils {
	public static String updatePlaceholder(String sqlstring, String placeholder, String newvalue) {
		StringBuilder result = new StringBuilder();
		if (!sqlstring.isEmpty()) {
			sqlstring.toString().replace("?" + placeholder, newvalue);
			result.append(sqlstring);

		} else {
			throw new IllegalArgumentException("Sql string is empty");

		}
		return result.toString();

	}

}
