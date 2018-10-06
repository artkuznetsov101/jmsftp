package jmsftp;

import java.io.IOException;

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
	String queue;
	boolean isConnected = false;
	boolean isReceiving = false;

	public JMSConsumer(String queue) {
		this.queue = queue;
		connectFTP();
	}

	public void connectFTP() {
		try {
			client.connect(Config.JMS.TEMP_DIR, Config.JMS.FTP_DIR);
		} catch (FileSystemException e) {
		}
	}

	public void connect() {
		log.info("jms2ftp -> jms [" + queue + "] connect");
		try {
			connection = JMSConnectionFactory.getIBMMQ().createConnection();
			connection.setExceptionListener(this);
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(queue);
			consumer = session.createConsumer(destination);
			isConnected = true;

			startReceive();
		} catch (JMSException e) {
			log.error("jms2ftp -> jms [" + queue + "] connect exception: " + e.getMessage());
			if (e.getCause() != null)
				log.error("jms2ftp -> jms [" + queue + "] connect exception: " + e.getCause().getMessage());
		}
	}

	public void startReceive() {
		log.info("jms2ftp -> jms [" + queue + "] start receive");
		try {
			if (consumer != null)
				consumer.setMessageListener(this);
			isReceiving = true;
			if (connection != null)
				connection.start();
		} catch (JMSException e) {
			log.error("jms2ftp -> jms [" + queue + "] start receive exception: " + e.getMessage());
		}
	}

	public void stopReceive() {
		log.info("jms2ftp -> jms [" + queue + "] stop receive");
		try {
			if (consumer != null)
				consumer.setMessageListener(null);
			if (connection != null)
				connection.stop();

			isReceiving = false;
		} catch (JMSException e) {
			log.error("jms2ftp -> jms [" + queue + "] stop receive exception: " + e.getMessage());
		} finally {
			isReceiving = false;
		}
	}

	public void disconnect() {
		log.info("jms2ftp -> jms [" + queue + "] disconnect");
		try {
			stopReceive();

			if (consumer != null) {
				consumer.close();
				consumer = null;
			}
			if (session != null) {
				session.close();
				session = null;
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (JMSException e) {
			log.error("jms2ftp -> jms [" + queue + "] disconnect exception: " + e.getMessage());
		}
	}

	@Override
	public void onException(JMSException e) {
		log.error("jms2ftp -> jms [" + queue + "] onException: " + e.getMessage());

		disconnect();
		isConnected = false;
	}

	@Override
	public void onMessage(Message message) {
		try {
			log.info("jms2ftp -> jms [" + queue + "] get: " + message.getJMSMessageID());
			String filename = JMSMessage.saveToFile(Config.JMS.TEMP_DIR, message, queue);
			client.upload(filename);
			log.info("jms2ftp -> ftp put: " + message.getJMSMessageID());
			session.commit();
			log.info("jms2ftp -> jms [" + queue + "] commit: " + message.getJMSMessageID());
		} catch (IOException e) {
			Emailer.send("jmsftp error", Main.getStackTrace(e));
		} catch (JMSException e) {
			try {
				session.rollback();
				log.error("jms2ftp -> jms [" + queue + "] rollback: " + e.getMessage());
				try {
					Thread.sleep(Config.COMMON.TIMEOUT);
				} catch (InterruptedException e1) {
				}
			} catch (JMSException e1) {
			}
		}
	}
}
