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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JMSConsumer implements ExceptionListener, MessageListener {
    private static final Logger log = LogManager.getLogger();
    
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
		}
	}

	public void connect() {
		log.info("jms2ftp ->  jms connect");
		try {
			connection = JMSConnectionFactory.getIBMMQ().createConnection();
			connection.setExceptionListener(this);
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(Config.JMS.RECV_QUEUE_NAME);
			consumer = session.createConsumer(destination);
			isConnected = true;

			startReceive();
		} catch (JMSException e) {
			log.error("jms2ftp ->  jms connect exception " + e.getMessage());
		}
	}

	public void startReceive() {
		log.info("jms2ftp ->  jms start receive");
		try {
			if (consumer != null)
				consumer.setMessageListener(this);
			isReceiving = true;
			if (connection != null)
				connection.start();
		} catch (JMSException e) {
			log.error("jms2ftp ->  jms start receive exception " + e.getMessage());
		}
	}

	public void stopReceive() {
		log.info("jms2ftp ->  jms stop receive");
		try {
			if (consumer != null)
				consumer.setMessageListener(null);
			if (connection != null)
				connection.stop();

			isReceiving = false;
		} catch (JMSException e) {
			log.error("jms2ftp ->  jms stop receive exception " + e.getMessage());
		} finally {
			isReceiving = false;
		}
	}

	public void disconnect() {
		log.info("jms2ftp ->  jms disconnect");
		try {
			stopReceive();

			if (consumer != null)
				consumer.close();
			if (session != null)
				session.close();
			if (connection != null)
				connection.close();
		} catch (JMSException e) {
			log.error("jms2ftp ->  jms disconnect exception " + e.getMessage());
		}
	}

	@Override
	public void onException(JMSException e) {
		log.error("jms2ftp ->  jms onException: " + e.getMessage());

		disconnect();
		isConnected = false;
	}

	@Override
	public void onMessage(Message message) {
		try {
			log.info("jms2ftp ->  jms get: " + message.getJMSMessageID());
			String filename = JMSMessage.saveToFile(Config.JMS.TEMP_DIR, message);
			client.upload(filename);
			log.info("jms2ftp ->  ftp put: " + message.getJMSMessageID());
			session.commit();
			log.info("jms2ftp ->   commit: " + message.getJMSMessageID());
		} catch (Exception e) {
			try {
				session.rollback();
				log.error("jms2ftp -> rollback: " + message.getJMSMessageID());
				try {
					Thread.sleep(Config.COMMON.TIMEOUT);
				} catch (InterruptedException e1) {
				}
			} catch (JMSException e1) {
			}
		}
	}
}
