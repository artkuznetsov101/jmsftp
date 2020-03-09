package jmsftp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

public class Main {
	private static final Logger log = LogManager.getLogger();

	public static void main(String[] args) throws InvalidFileFormatException, IOException, InterruptedException {

		Wini ini = new Wini(new File(Config.NAME));
		Config.setConfig(ini);
		log.info(ini);

		List<Thread> ftpThreadList = new ArrayList<>();
		List<FTPThread> ftpList = new ArrayList<>();
		if (Config.COMMON.FTP2JMS) {
			Config.FTP2JMS.sections.forEach(section -> {

				Path temp = Paths.get(Config.COMMON.TEMP_DIR, section.NAME);
				try {
					Files.createDirectories(temp);
				} catch (IOException e) {
					log.error(e);
				}

				FTPThread ftp = new FTPThread(section.FTP_DIR, Arrays.asList(section.JMS_QUEUES.split(",")),
						temp.toString());
				Thread thread = new Thread(ftp, section.NAME);
				thread.start();
				ftpThreadList.add(thread);
				ftpList.add(ftp);
			});
		}

		List<Thread> jmsThreadList = new ArrayList<>();
		List<JMSThread> jmsList = new ArrayList<>();
		if (Config.COMMON.JMS2FTP) {
			Config.JMS2FTP.sections.forEach(section -> {

				Path temp = Paths.get(Config.COMMON.TEMP_DIR, section.NAME);
				try {
					Files.createDirectories(temp);
				} catch (IOException e) {
					log.error(e);
				}

				JMSThread jms = new JMSThread(Arrays.asList(section.JMS_QUEUES.split(",")), section.FTP_DIR,
						temp.toString());
				Thread thread = new Thread(jms, section.NAME);
				thread.start();
				jmsThreadList.add(thread);
				jmsList.add(jms);
			});
		}

		if (Config.MAIL.SEND_START_EMAIL)
			Emailer.send("jmsftp start", "start time is "
					+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("shutdown hook start");
				try {

					ftpList.forEach(ftp -> ftp.close());
					jmsList.forEach(jms -> jms.close());

					Thread.sleep(Config.COMMON.TIMEOUT * 2);

					ftpThreadList.forEach(ftpThread -> {
						try {
							ftpThread.join();
						} catch (InterruptedException e) {
							log.error(e);
						}
					});
					jmsThreadList.forEach(jmsThread -> {
						try {
							jmsThread.join();
						} catch (InterruptedException e) {
							log.error(e);
						}
					});
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
