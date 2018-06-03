package jmsftp;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

public class JMSConsumer implements ExceptionListener, MessageListener {
	Connection connection;
	Session session;
	Destination destination;
	MessageConsumer consumer;
	boolean isConnected = false;
	boolean isReceiving = false;

	public void connect() {
		System.out.println("jms -> connect");
		try {
			connection = JMSConnectionFactory.getIBMMQ().createConnection();
			connection.setExceptionListener(this);
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(JMSSettings.QUEUE_NAME);
			consumer = session.createConsumer(destination);
			isConnected = true;

			startReceive();
		} catch (JMSException e) {
			System.out.println("jms -> connect exception " + e.getMessage());
			// e.printStackTrace();
		}
	}

	public void startReceive() {
		System.out.println("jms -> start receive");
		try {
			if (consumer != null)
				consumer.setMessageListener(this);
			isReceiving = true;
			if (connection != null)
				connection.start();
		} catch (JMSException e) {
			System.out.println("jms -> start receive exception " + e.getMessage());
			// e.printStackTrace();
		}
	}

	public void stopReceive() {
		System.out.println("jms -> stop receive");
		try {
			if (consumer != null)
				consumer.setMessageListener(null);
			if (connection != null)
				connection.stop();

			isReceiving = false;
		} catch (JMSException e) {
			System.out.println("jms -> stop receive exception " + e.getMessage());
			// e.printStackTrace();
		} finally {
			isReceiving = false;
		}
	}

	public void disconnect() {
		System.out.println("jms -> disconnect");
		try {
			stopReceive();

			if (consumer != null)
				consumer.close();
			if (session != null)
				session.close();
			if (connection != null)
				connection.close();
		} catch (JMSException e) {
			System.out.println("jms -> disconnect exception " + e.getMessage());
			// e.printStackTrace();
		}
	}

	@Override
	public void onException(JMSException e) {
		System.out.println("jms -> onException: " + e.getMessage());
		// e.printStackTrace();

		disconnect();
		isConnected = false;
	}

	@Override
	public void onMessage(Message message) {
		System.out.println("jms -> receive " + JMSMessage.getType(message).name() + " message: ");
		try {
			switch (JMSMessage.getType(message)) {
			case TEXT:
				break;
			case MAP:
				break;
			case OBJECT:
				break;
			case BYTES:
				break;
			case STREAM:
				break;
			case MESSAGE:
			default:
				break;
			}
			System.out.print(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
