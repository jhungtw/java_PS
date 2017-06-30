package basic.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.yaml.snakeyaml.Yaml;

import basic.config.DoneStatus;
import basic.config.Report;
import basic.config.StatusMapBean;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class Tool {
	private static Logger accessLog = Logger.getLogger("ReportAutomationLog");

	public static String packLocal(String filePath, String password) throws ZipException {
		ZipParameters zipParameters = new ZipParameters();
		zipParameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		zipParameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
		zipParameters.setEncryptFiles(true);
		zipParameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
		zipParameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		zipParameters.setPassword(password);
		String baseFileName = FilenameUtils.getBaseName(filePath);
		String basePathName = FilenameUtils.getFullPath(filePath);
		String destinationZipFilePath = FilenameUtils.concat(basePathName, baseFileName.concat(".zip"));
		System.out.println("---" + filePath);
		System.out.println("===" + destinationZipFilePath);
		ZipFile zipFile = new ZipFile(destinationZipFilePath);

		zipFile.addFile(new File(filePath), zipParameters);
		return destinationZipFilePath;
	}

	public static String getWeekDayName(int index) {

		String[] dayNames = new String[] { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday",
				"Sunday" };
		return dayNames[index - 1];

	}

	public static boolean isResultEmpty(ResultSet rs) throws Exception {

		if (!rs.next()) {

			rs.beforeFirst();

			return true;
		} else {
			return false;
		}

	}

	

	public static boolean processDeliveryActions(boolean isResultSetEmpty, boolean returnEmpty) throws SQLException {

		if (!returnEmpty && isResultSetEmpty) {
			return false;
		} else {
			return true;
		}

	}

	public static Map<String, Report> disableIrrelevantReports(Map<String, Report> reports) {

		DateTime dt = new DateTime();

		// test monday as 1 st day of month
		// today.set(2017, 4, 1);
		// test monday
		// today.set(2017, 0, 2);
		// test 1 st day of month
		// today.set(2017, 0, 11);

		// 1.when enabled: true
		for (String entry : reports.keySet()) {

			// System.out.println("-->"+reports.get(entry).getTrigger_day()+reports.get(entry).isEnabled()+getWeekDayName(dt.getDayOfWeek()));

			if (reports.get(entry).isEnabled()) {
				// System.out.println(" enabled!!!");
				// 2.0 when frequency: DAILY
				if (reports.get(entry).getFrequency().equalsIgnoreCase(ReportFrequency.DAILY.toString())) {
					// 2.0.1 nothing

				}
				// 2.1 when frequency: WEEK
				if (reports.get(entry).getFrequency().equalsIgnoreCase(ReportFrequency.WEEK.toString())) {
					// 2.1.1 when trigger_day not matched
					if (!reports.get(entry).getTrigger_day().equalsIgnoreCase(getWeekDayName(dt.getDayOfWeek()))) {
						// System.out.println(" set false!!!");
						reports.get(entry).setEnabled(false);

					}
				}

				// 2.2 when frequency: MONTH
				if (reports.get(entry).getFrequency().equalsIgnoreCase(ReportFrequency.MONTH.toString())) {
					// 2.2.1 when trigger_day not matched

					if (!reports.get(entry).getTrigger_day().equalsIgnoreCase(Integer.toString(dt.getDayOfMonth()))) {
						reports.get(entry).setEnabled(false);

					}

				}
			}

		}
		System.out.println("reports is : " + reports.toString());

		/*
		 * today.setFirstDayOfWeek(Calendar.MONDAY);
		 * 
		 * System.out.println("today: " + today.getTime().toString());
		 * 
		 * boolean isMonth = (today.get(Calendar.DAY_OF_MONTH) == 1); boolean
		 * isWeek = (today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY);
		 * 
		 * System.out.println("isMonth//isWeek : " + isMonth + "//" + isWeek);
		 * 
		 * if (!isWeek) {
		 * 
		 * for (String entry : reports.keySet()) { if
		 * (reports.get(entry).getFrequency().equalsIgnoreCase(ReportFrequency.
		 * WEEK.toString())) reports.get(entry).setEnabled(false);
		 * System.out.println("Key : " + entry + " Value : " +
		 * reports.get(entry).toString());
		 * 
		 * }
		 * 
		 * }
		 * 
		 * if (!isMonth)
		 * 
		 * { for (String entry : reports.keySet()) {
		 * System.out.println("-->Key : " + entry + " Value : " +
		 * reports.get(entry).toString()); if
		 * (reports.get(entry).getFrequency().equalsIgnoreCase(ReportFrequency.
		 * MONTH.toString())) reports.get(entry).setEnabled(false);
		 * System.out.println("Key : " + entry + " Value : " +
		 * reports.get(entry).toString());
		 * 
		 * } } System.out.println("reports is : " + reports.toString());
		 */
		return reports;

	}

	public static void addStatusForRerun(String reportid, String name, boolean status, String filepath)
			throws IOException {

		// Path path = Paths.get(filepath);

		if (Files.notExists(Paths.get(filepath))) {

			Files.createFile(Paths.get(filepath));
			DoneStatus ds = new DoneStatus();
			ds.setDone(status);
			ds.setName(name);
			Map<String, DoneStatus> reports = new HashMap<String, DoneStatus>();
			reports.put(reportid, ds);
			StatusMapBean parsed = new StatusMapBean();
			parsed.setReports(reports);
			FileWriter writer = new FileWriter(filepath);
			Yaml yaml = new Yaml();
			yaml.dump(parsed, writer);

		} else {
			FileReader reader = new FileReader(new File(filepath));
			Yaml yaml = new Yaml();

			StatusMapBean parsed = yaml.loadAs(reader, StatusMapBean.class);
			Map<String, DoneStatus> reports = parsed.getReports();
			if (reports.containsKey(reportid)) {
				DoneStatus ds = reports.get(reportid);
				ds.setDone(status);
				ds.setName(name);
				// implement key is found
				reports.put(reportid, ds);

			} else {
				DoneStatus ds = new DoneStatus();
				ds.setDone(status);
				ds.setName(name);
				// implement key is found
				reports.put(reportid, ds);
			}

			parsed.setReports(reports);
			FileWriter writer = new FileWriter(filepath);
			yaml.dump(parsed, writer);

		}

	}

	public static ArrayList<String> getReportsForRerun(String filepath) throws FileNotFoundException {

		ArrayList<String> ret = new ArrayList<String>();
		FileReader reader = new FileReader(new File(filepath));
		Yaml yaml = new Yaml();
		StatusMapBean parsed = yaml.loadAs(reader, StatusMapBean.class);

		Map<String, DoneStatus> reports = parsed.getReports();

		for (String entry : reports.keySet()) {
			System.out.println("Key : " + entry + " Value : " + reports.get(entry).toString());
			if (!reports.get(entry).isDone())
				ret.add(entry);

		}
		return ret;

	}

	public static String getOutputFilePath(String reportkey, String reportname, String tmpfolder, String extenstion) {

		System.out.println("tmpfolder is " + tmpfolder);
		String report = reportname.trim().replaceAll(" ", "_").replace("/", "Or").toLowerCase();

		StringBuilder sb = new StringBuilder();

		if (StringUtils.isEmpty(extenstion)) {
			extenstion = "csv";
		}
		;

		sb.append(tmpfolder).append("\\").append(reportkey).append("_").append(report).append("_")
				.append(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())).append(".")
				.append(extenstion);

		return sb.toString();
	}

	public static String getOutputFilePathWithFormat(String format, String reportname, String tmpfolder,
			String extension) {

		System.out.println("tmpfolder is " + tmpfolder);
		String report = reportname.trim().replaceAll(" ", "_").toLowerCase();

		StringBuilder sb = new StringBuilder();

		if (format.contains("[") && format.contains("]")) {
			String dateFormat = format.substring(format.indexOf("[") + 1, format.indexOf("]"));
			String bf_dateformat = format.substring(0, format.indexOf("[") - 1);
			String af_dateformat = "";
			if (format.length() > (format.indexOf("]") + 1)) {
				af_dateformat = format.substring(format.indexOf("]") + 1, format.length());
			}

			System.out.println("Date format is : " + dateFormat);
			sb.append(tmpfolder).append("\\").append(bf_dateformat)
					.append(new SimpleDateFormat(dateFormat).format(Calendar.getInstance().getTime()))
					.append(af_dateformat).append(".").append(extension.toLowerCase());

		} else {
			sb.append(tmpfolder).append("\\").append(report).append("_")
					.append(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())).append(".")
					.append(extension.toLowerCase());
		}
		return sb.toString().replace(" ", "_");
	}

	public static String getFirstDayOfLastWeek(String format) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -7);
		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			c.add(Calendar.DATE, -1);
		}
		DateFormat df = new SimpleDateFormat(format);

		return df.format(c.getTime());

	}

	public static String getFirstDayOfThisWeek(String format) {
		Calendar c = Calendar.getInstance();

		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			c.add(Calendar.DATE, -1);
		}
		DateFormat df = new SimpleDateFormat(format);

		return df.format(c.getTime());

	}

	public static boolean isFirstDayOfThisWeek(Calendar today) {

		return (today.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY);

	}

	public static String getFirstDayOfThisMonth(String format) {
		Calendar c = Calendar.getInstance();

		c.set(Calendar.DAY_OF_MONTH, 1);

		DateFormat df = new SimpleDateFormat(format);

		return df.format(c.getTime());

	}

	public static boolean isFirstDayOfThisMonth(Calendar today) {

		return (today.get(Calendar.DAY_OF_MONTH) == 1);

	}

	public static String getFirstDayOfLastMonth(String format) {
		Calendar c = Calendar.getInstance();

		if (c.get(Calendar.MONTH) == 0) {

			c.set(Calendar.MONTH, 11);

			c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
		} else {
			System.out.println("2--" + c.getTime().toString());
			c.set(Calendar.MONTH, Calendar.MONTH - 1);

		}
		c.set(Calendar.DAY_OF_MONTH, 1);

		DateFormat df = new SimpleDateFormat(format);

		return df.format(c.getTime());

	}

	public static String getFinalDayOfLastWeek(String format) {
		// Calendar rightNow = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -7);

		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			c.add(Calendar.DATE, +1);
		}
		DateFormat df = new SimpleDateFormat(format);
		return df.format(c.getTime());

	}

}
