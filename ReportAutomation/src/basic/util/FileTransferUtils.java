package basic.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class FileTransferUtils {

	public static void copyFilesToFtpFolder(String host, String user, String password, String localFileFullName,
			String fileName, String hostDir) throws Exception {
		System.out.println("Start copy");
		FTPClient ftp = new FTPClient();
	
		ftp.connect(host);
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new Exception("Exception in connecting to FTP Server");
		}
		ftp.login(user, password);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();
	
		try (InputStream input = new FileInputStream(new File(localFileFullName))) {
			ftp.storeFile(hostDir + fileName, input);
		}
		if (ftp.isConnected()) {
	
			ftp.logout();
			ftp.disconnect();
	
		}
		System.out.println("Done copy");
	}

	public static void copyFilesToFtpFolderOverSSH(String SFTPHOST, String SFTPUSER, String SFTPPASS,
			String FILETOTRANSFER, String fileName, String SFTPWORKINGDIR) throws Exception {
		System.out.println("Start copy");
		com.jcraft.jsch.Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;
		JSch jsch = new JSch();
		session = jsch.getSession(SFTPUSER, SFTPHOST, 22);
		session.setPassword(SFTPPASS);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		channel = session.openChannel("sftp");
		channel.connect();
		channelSftp = (ChannelSftp) channel;
		channelSftp.cd(SFTPWORKINGDIR);
		File f = new File(FILETOTRANSFER);
		channelSftp.put(new FileInputStream(f), f.getName());
		System.out.println("Done copy");
	}

}
