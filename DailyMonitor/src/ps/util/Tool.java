package ps.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import ps.config.Job;

public class Tool {
	public static boolean isCompletedToday() {
		DateTime dt = new DateTime();
		File file = new File("c:\\tmp\\control_" + dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd")) + ".txt");
		return file.exists();

	}

	public static void AddControlFileforFulfillment() throws IOException {
		DateTime dt = new DateTime();
		File file = new File("c:\\tmp\\control_" + dt.toString(DateTimeFormat.forPattern("yyyy-MM-dd")) + ".txt");
		file.createNewFile();

	}

	public static boolean isAllJobDoneSuccessfully(Map<Integer, Job> jobs,boolean isATSGetStuck) throws IOException {

		boolean result = true;

		for (Integer entry : jobs.keySet()) {

			System.out.println("Key : " + entry + "-----" + jobs.get(entry).getStatus().toLowerCase() + " Value : "
					+ jobs.get(entry).toString());
			if (jobs.get(entry).getStatus() == null) {
				result = false;
				break;
			} else {
				if (!jobs.get(entry).getStatus().toLowerCase().contains("success")) {
					result = false;
					break;

				}

			}

			Map<Integer, Job> subjobs = jobs.get(entry).getSubjobs();

			if (subjobs != null) {
				
				for (Integer subindex : subjobs.keySet()) {
					System.out.println("Key : " + entry + "-----" + subjobs.get(subindex).getStatus().toLowerCase() + " Value : "
							+ subjobs.get(subindex).toString());
					if (subjobs.get(subindex).getStatus() == null) {
						result = false;
						break;
					} else {
						if (!subjobs.get(subindex).getStatus().toLowerCase().contains("success")) {
							result = false;
							break;

						}
					}

				}
			}
		}
		
		
		if (isATSGetStuck){result =false;}
		
		return result;

	}

}
