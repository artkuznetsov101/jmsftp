package jmsftp;

public class Main {

	public static void main(String[] args) throws Exception {

		// TODO check temp dir exist

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
