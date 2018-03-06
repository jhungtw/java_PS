package ps.util.basic;

import java.text.MessageFormat;

public class IO_Validation {
	public static boolean isNumericString(String toBeValidated, int length) throws Exception {
		String errorMessage = "";
		boolean returnValue = true;
		if (toBeValidated.length() != length) {
			errorMessage = MessageFormat.format("Failed validation: the string length is not {0}", length);

			returnValue = false;
		}

		if (!toBeValidated.matches("[0-9]+")) {

			errorMessage = MessageFormat.format("Failed validation: the string length is not {0}", length);
			returnValue = false;
		}
		if (returnValue = false) {
			throw new Exception(errorMessage);
		}
		return returnValue;

	}

}
