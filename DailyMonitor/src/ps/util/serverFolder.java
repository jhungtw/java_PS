package ps.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.text.MessageFormat;
import java.util.logging.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import ps.util.basic.Datetime_Validation;
import ps.util.basic.IO_Validation;

public class serverFolder {
	private final Logger LOGGER = Logger.getLogger("DailyMonitorLog");

	// Check event info processed by PCS
	public boolean isEventProcessedByPCS(String hotFolderPath, String archivePath, String serverip,
			String serverusername, String serverpassword) throws Exception {
		LOGGER.info("checking on isEventProcessedByPCS");
		boolean isProcessedBeforePCSRun = false;
		// if event file exists, check the timestamps
		if (isFileExisting(archivePath,
				MessageFormat.format("events-en*{0}*", new LocalDate().toString(DateTimeFormat.forPattern("yyyyMMdd"))),
				serverip, serverusername, serverpassword)) {
			isProcessedBeforePCSRun = true;
		} else {
			Channel channel = getJschChannel(serverusername, serverip, serverpassword);

			String linuxCommand = MessageFormat.format(
					"cd {0} && ls -ltr --time-style=long-iso events-en*{1}* | head -1 | awk '{print $8}' | cut -d'_' -f2",
					hotFolderPath, new LocalDate().toString(DateTimeFormat.forPattern("yyyyMMdd")));
			LOGGER.info(linuxCommand);
			System.out.println(linuxCommand);

			((ChannelExec) channel).setCommand(linuxCommand);

			InputStream commandOutput = channel.getInputStream();
			channel.connect();

			String outputfromServer = getOuputFromServer(commandOutput);
			LOGGER.info("linuxcommand result : " + outputfromServer);
			IO_Validation.isNumericString(outputfromServer, 17);
			Datetime_Validation dv = new Datetime_Validation();

			channel.disconnect();
			isProcessedBeforePCSRun = dv.isProcessedBeforePCS(outputfromServer);
		}
		return isProcessedBeforePCSRun;

	}

	private boolean isFileExisting(String folder, String fileName, String serverip, String serverusername,
			String serverpassword) throws JSchException, IOException {
		boolean returnValue = true;
//		final String FLAG_EXIST = "yes";
		final String FLAG_NOT_EXIST = "no";
		LOGGER.info("checking on isFileExisting in " + folder);

		Channel channel = getJschChannel(serverusername, serverip, serverpassword);
		String linuxCommand = MessageFormat.format(" cd {0} && [ -s {1} ] && echo yes || echo no", folder, fileName);
		LOGGER.info(linuxCommand);

		((ChannelExec) channel).setCommand(linuxCommand);

		InputStream commandOutput = channel.getInputStream();
		channel.connect();
		String outputfromServer = getOuputFromServer(commandOutput);

		if (outputfromServer.equalsIgnoreCase(FLAG_NOT_EXIST)) {
			returnValue = false;
		}

		channel.disconnect();
		return returnValue;
	}

	private Channel getJschChannel(String user, String ip, String password) throws JSchException {
		JSch jsch = new JSch();
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		config.put("compression.s2c", "zlib,none");
		config.put("compression.c2s", "zlib,none");

		com.jcraft.jsch.Session session = jsch.getSession(user, ip);
		session.setConfig(config);
		session.setPort(22);
		session.setPassword(password);
		session.connect();
		// /opt/dataload/import

		Channel channel = session.openChannel("exec");
		return channel;
	}

	private String getOuputFromServer(InputStream commandOutput) throws IOException {
		int readByte = commandOutput.read();
		StringBuilder outputBuffer = new StringBuilder();
		while (readByte != 0xffffffff) {
			outputBuffer.append((char) readByte);
			readByte = commandOutput.read();
		}
		LOGGER.info("result: " + outputBuffer.toString().trim());
		return outputBuffer.toString();
	}

	// Check item info processed by PCS
	public boolean isItemInfoProcessedByPCS(String hotFolderPath, String archivePath, String serverip,
			String serverusername, String serverpassword) throws Exception {
		LOGGER.info("checking on isItemInfoProcessedByPCS");
		boolean isProcessedBeforePCSRun = false;

		// if event file exists, check the timestamps
		if (isFileExisting(archivePath,
				MessageFormat.format("price-en*{0}*", new LocalDate().toString(DateTimeFormat.forPattern("yyyyMMdd"))),
				serverip, serverusername, serverpassword)) {
			isProcessedBeforePCSRun = true;
		} else {
			Channel channel = getJschChannel(serverusername, serverip, serverpassword);

			String linuxCommand = MessageFormat.format(
					"cd {0} && ls -ltr --time-style=long-iso price-en*{1}* | head -1 | awk '{print $8}' | cut -d'_' -f2",
					hotFolderPath, new LocalDate().toString(DateTimeFormat.forPattern("yyyyMMdd")));
			LOGGER.info(linuxCommand);

			((ChannelExec) channel).setCommand(linuxCommand);

			InputStream commandOutput = channel.getInputStream();
			channel.connect();

			String outputfromServer = getOuputFromServer(commandOutput);
			LOGGER.info("linuxcommand result : " + outputfromServer);
			IO_Validation.isNumericString(outputfromServer, 17);
			Datetime_Validation dv = new Datetime_Validation();

			channel.disconnect();
			isProcessedBeforePCSRun = dv.isProcessedBeforePCS(outputfromServer);
		}
		return isProcessedBeforePCSRun;

	}

	public boolean isHotFolderJammed(String hotFolderPath, String serverip, String serverusername,
			String serverpassword) throws JSchException, IOException {

		LOGGER.info("checking on isHotFolderJammed");

		boolean isJammed = false;

		StringBuilder outputBuffer = new StringBuilder();

		Channel channel = getJschChannel(serverusername, serverip, serverpassword);

		LOGGER.info(
				"cd " + hotFolderPath + " && ls -ltr --time-style=long-iso *.log | head -1 | awk '{print $6\" \"$7}' ");

		((ChannelExec) channel).setCommand(
				"cd " + hotFolderPath + " && ls -ltr --time-style=long-iso *.log | head -1 | awk '{print $6\" \"$7}' ");

		InputStream commandOutput = channel.getInputStream();
		channel.connect();
		int readByte = commandOutput.read();

		while (readByte != 0xffffffff) {
			outputBuffer.append((char) readByte);
			readByte = commandOutput.read();
		}
		System.out.println("result: " + outputBuffer.toString().trim());

		if (outputBuffer.toString().trim().length() > 0) {
			DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
			DateTime lastdt = f.parseDateTime(outputBuffer.toString().trim());

			DateTime nowdt = new DateTime();
			long diff = nowdt.getMillis() - lastdt.getMillis();
			System.out.println(lastdt + "///" + nowdt + "///" + diff);
			// Check 20 mins delay
			if (diff > 20)
				isJammed = true;
		}
		;

		channel.disconnect();
		LOGGER.info("isJammed:  " + isJammed);
		return isJammed;

	}

}
