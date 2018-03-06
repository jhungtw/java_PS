package ps.util.basic;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Datetime_Validation {

	public  boolean isProcessedBeforePCS(String outputfromServer) {
		boolean isJammed = false;
		if (outputfromServer.trim().length() > 0) {
			DateTimeFormatter f = DateTimeFormat.forPattern("yyyyMMddHHmmssSSS");
			DateTime processedTime = f.parseDateTime(outputfromServer.toString().trim());
			LocalDate systemDate = new LocalDate();
	
			DateTime pcsStart = new DateTime(
					systemDate.toString(DateTimeFormat.forPattern("yyyy-MM-dd")) + "T02:30:00");
	       
			if (processedTime.isAfter(pcsStart.getMillis()))
				isJammed = true;
		}
		;
		return isJammed;
	}

}
