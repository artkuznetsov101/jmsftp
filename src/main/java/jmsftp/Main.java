package jmsftp;

import java.io.File;

import org.ini4j.Wini;

public class Main {

	public static void main(String[] args) throws Exception {

		// get config
		Wini ini = new Wini(new File(Config.NAME));
		Config.setConfig(ini);
		System.out.println(ini);

		// TODO check temp dir exist

		// start ftp2jms thread
		FTPThread ftp = null;
		Thread ftpThread = null;
		if (Config.COMMON.FTP2JMS) {
			ftp = new FTPThread();
			ftpThread = new Thread(ftp);
			ftpThread.start();
		}

		// start jms2sftp thread
		JMSThread jms = null;
		Thread jmsThread = null;
		if (Config.COMMON.JMS2FTP) {
			jms = new JMSThread();
			jmsThread = new Thread(jms);
			jmsThread.start();
		}

		// Thread.sleep(60 * 1000);
		// sftp.close();
		// jms.close();

		if (Config.COMMON.FTP2JMS) {
			ftpThread.join();
		}
		if (Config.COMMON.JMS2FTP) {
			jmsThread.join();
		}
	}
}
