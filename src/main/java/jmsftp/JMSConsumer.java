package jmsftp;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.commons.vfs2.FileSystemException;

public class JMSConsumer implements ExceptionListener, MessageListener {

	FTPClient client = new FTPClient();

	Connection connection;
	Session session;
	Destination destination;
	MessageConsumer consumer;
	boolean isConnected = false;
	boolean isReceiving = false;

	public JMSConsumer() {
		connectFTP();
	}

	public void connectFTP() {
		try {
			client.connect(Config.JMS.TEMP_DIR, Config.JMS.FTP_DIR);
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
	}

	public void connect() {
		System.out.println("jms2ftp ->  jms connect");
		try {
			connection = JMSConnectionFactory.getIBMMQ().createConnection();
			connection.setExceptionListener(this);
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(Config.JMS.RECV_QUEUE_NAME);
			consumer = session.createConsumer(destination);
			isConnected = true;

			startReceive();
		} catch (JMSException e) {
			System.out.println("jms2ftp ->  jms connect exception " + e.getMessage());
			// e.printStackTrace();
		}
	}

	public void startReceive() {
		System.out.println("jms2ftp ->  jms start receive");
		try {
			if (consumer != null)
				consumer.setMessageListener(this);
			isReceiving = true;
			if (connection != null)
				connection.start();
		} catch (JMSException e) {
			System.out.println("jms2ftp ->  jms start receive exception " + e.getMessage());
			// e.printStackTrace();
		}
	}

	public void stopReceive() {
		System.out.println("jms2ftp ->  jms stop receive");
		try {
			if (consumer != null)
				consumer.setMessageListener(null);
			if (connection != null)
				connection.stop();

			isReceiving = false;
		} catch (JMSException e) {
			System.out.println("jms2ftp ->  jms stop receive exception " + e.getMessage());
			// e.printStackTrace();
		} finally {
			isReceiving = false;
		}
	}

	public void disconnect() {
		System.out.println("jms2ftp ->  jms disconnect");
		try {
			stopReceive();

			if (consumer != null)
				consumer.close();
			if (session != null)
				session.close();
			if (connection != null)
				connection.close();
		} catch (JMSException e) {
			System.out.println("jms2ftp ->  jms disconnect exception " + e.getMessage());
			// e.printStackTrace();
		}
	}

	@Override
	public void onException(JMSException e) {
		System.out.println("jms2ftp ->  jms onException: " + e.getMessage());
		// e.printStackTrace();

		disconnect();
		isConnected = false;
	}

	@Override
	public void onMessage(Message message) {
		try {
			System.out.println("jms2ftp ->  jms get: " + message.getJMSMessageID());
			String filename = JMSMessage.saveToFile(Config.JMS.TEMP_DIR, message);
			client.upload(filename);
			System.out.println("jms2ftp ->  ftp put: " + message.getJMSMessageID());
			session.commit();
			System.out.println("jms2ftp ->   commit: " + message.getJMSMessageID());
		} catch (Exception e) {
			try {
				session.rollback();
				System.out.println("jms2ftp -> rollback: " + message.getJMSMessageID());
				try {
					Thread.sleep(Config.COMMON.TIMEOUT);
				} catch (InterruptedException e1) {
				}
			} catch (JMSException e1) {
			}
			// TODO log
			// e.printStackTrace();
		}
	}
}
