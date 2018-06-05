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

		//start jms2sftp thread
		JMSThread jms = new JMSThread();
		Thread jmsThread = new Thread(jms);
		jmsThread.start();

		// // start receiving
		// Thread.sleep(15 * 1000);
		//
		// // stop receiving
		// jms.stop();
		// Thread.sleep(15 * 1000);
		//
		// // start receiving
		// jms.start();
		// Thread.sleep(15 * 1000);
		//
		// // stop receiving and close
		// jms.close();
		jmsThread.join();
	}
}
