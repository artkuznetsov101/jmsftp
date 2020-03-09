package jmsftp;

import java.util.List;

public class JMSThread implements Runnable {

	JMSConsumer consumer;

	boolean isClosed = false;

	public JMSThread(List<String> queues, String ftp, String temp) {
		consumer = new JMSConsumer(queues, ftp, temp);
	}

	@Override
	public void run() {
		while (!isClosed) {
			if (!consumer.isConnected) {
				consumer.connect();
			}
			try {
				Thread.sleep(Config.COMMON.TIMEOUT);
			} catch (InterruptedException e) {
			}
		}
	}

	public void stop() {
		consumer.stopReceive();
	}

	public void start() {
		consumer.startReceive();
	}

	public void close() {
		isClosed = true;
		consumer.disconnect();
	}
}
