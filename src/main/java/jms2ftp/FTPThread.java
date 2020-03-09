package jms2ftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FTPThread implements Runnable {
	private static final Logger log = LogManager.getLogger();

	JMSProducer producer;

	String ftp;
	List<String> queues;
	String temp;

	boolean isClosed = false;

	public FTPThread(String ftp, List<String> queues, String temp) {
		this.ftp = ftp;
		this.queues = queues;
		this.temp = temp;

		producer = new JMSProducer(ftp, queues, temp);
	}

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
					log.info("ftp [" + ftp + "] get: " + file);
					producer.send(new String(Files.readAllBytes(Paths.get(temp, file))));
					log.info("jms [" + queues + "] put: " + file);
					producer.client.delete(temp, file);
					producer.client.delete(file);
					producer.session.commit();
					log.info("jms [" + queues + "] commit: " + file);
				} else {
					try {
						Thread.sleep(Config.COMMON.TIMEOUT);
					} catch (InterruptedException e) {
					}
				}
			} catch (IOException e) {
				Emailer.send("jms2ftp error", Main.getStackTrace(e));
			} catch (JMSException e) {
				try {
					producer.session.rollback();
					log.error("jms [" + queues + "] rollback: " + e.getMessage());
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
