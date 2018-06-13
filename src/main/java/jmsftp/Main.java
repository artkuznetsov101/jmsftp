package jmsftp;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.ini4j.Wini;

public class Main {

	public static void main(String[] args) throws Exception {

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

		if (Config.COMMON.FTP2JMS) {
			ftpThread.join();
		}
	}
}
