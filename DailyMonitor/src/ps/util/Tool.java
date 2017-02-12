package ps.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;


public class Tool {
	public static boolean isCompletedToday(){
		DateTime dt = new DateTime();
		File file = new File("c:\\tmp\\control_"+dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))+".txt");
		return file.exists();
		
	}
	
	
	public static void AddControlFileforFulfillment() throws IOException{
		DateTime dt = new DateTime();
		File file = new File("c:\\tmp\\control_"+dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd"))+".txt");
		file.createNewFile();
		
	}

}
