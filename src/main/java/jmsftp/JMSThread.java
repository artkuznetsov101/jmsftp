package jmsftp;

public class JMSThread implements Runnable {
	JMSConsumer consumer;
	boolean isClosed = false;

	public JMSThread(String queue) {
		consumer = new JMSConsumer(queue);
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
