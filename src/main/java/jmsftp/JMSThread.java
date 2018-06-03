package jmsftp;

public class JMSThread implements Runnable {
	JMSConsumer consumer = new JMSConsumer();
	boolean isClosed = false;

	@Override
	public void run() {

		while (!isClosed) {
			if (!consumer.isConnected) {
				consumer.connect();
			}
			try {
				Thread.sleep(JMSSettings.CHECK_CONNECT);
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
