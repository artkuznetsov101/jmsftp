package jmsftp;

import java.io.IOException;

import javax.jms.JMSException;

public class FTPThread implements Runnable {
	JMSProducer producer = new JMSProducer();

	boolean isClosed = false;

	@Override
	public void run() {
		String file;

		while (!isClosed) {
			if (!producer.isConnected) {
				producer.connect();
			}
			if (producer.client.remote == null) {
				producer.connectFTP();
			}
			
			try {
				if (producer.isConnected == true && (file = producer.client.get()) != null) {
					System.out.println("ftp2jms ->  ftp get: " + file);
					producer.send(Config.FTP.TEMP_DIR, file);
					System.out.println("ftp2jms ->  jms put: " + file);
					producer.client.delete(Config.FTP.TEMP_DIR, file);
					producer.client.delete(file);
					producer.session.commit();
					System.out.println("ftp2jms ->   commit: " + file);
				} else {
					try {
						Thread.sleep(Config.COMMON.TIMEOUT);
					} catch (InterruptedException e) {
					}
				}
			} catch (JMSException | IOException e) {
				try {
					producer.session.rollback();
					System.out.println("ftp2jms -> rollback");
					try {
						Thread.sleep(Config.COMMON.TIMEOUT);
					} catch (InterruptedException e1) {
					}
				} catch (JMSException ex) {
				}
			}
		}
	}

	public void close() {
		isClosed = true;
		producer.disconnect();
	}
}
