package jmsftp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class Main {

	public static void main(String[] args) throws InvalidFileFormatException, IOException, InterruptedException {

		// get configuration
		Wini ini = new Wini(new File(Config.NAME));
		Config.setConfig(ini);
		System.out.println(ini);

		FTPThread ftp = null;
		Thread ftpThread = null;
		if (Config.COMMON.FTP2JMS) {
			// check temporary directory exist
			Files.createDirectories(Paths.get(Config.FTP.TEMP_DIR));
			// start ftp2jms thread
			ftp = new FTPThread(Config.JMS.SEND_QUEUE_NAME);
			ftpThread = new Thread(ftp, "ftp2jms thread");
			ftpThread.start();
		}

		// check receive queues
		if (Config.COMMON.JMS2FTP) {
			// check temporary directory exist
			Files.createDirectories(Paths.get(Config.JMS.TEMP_DIR));

			String[] queues = Config.JMS.RECV_QUEUES_NAME.split(",");
			List<JMSThread> jmsList = new ArrayList<>();
			List<Thread> jmsThreadList = new ArrayList<>();

			for (String queue : queues) {
				// start jms2sftp thread
				JMSThread jms = new JMSThread(queue.trim());
				Thread jmsThread = new Thread(jms, "jms2ftp thread");
				jmsThread.start();

				jmsList.add(jms);
				jmsThreadList.add(jmsThread);
			}
		}

		if (Config.MAIL.SEND_START_EMAIL)
			Emailer.send("jmsftp start", "start time is " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
		if (Config.COMMON.FTP2JMS) {
			ftpThread.join();
		}
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
