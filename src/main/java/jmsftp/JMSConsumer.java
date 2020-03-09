package jmsftp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JMSConsumer implements ExceptionListener, MessageListener {
	private static final Logger log = LogManager.getLogger();

	FTPClient client;;
	String ftp;

	Connection connection;
	Session session;
	List<MessageConsumer> consumers = new ArrayList<>();;
	List<String> queues;

	String temp;

	boolean isConnected = false;
	boolean isReceiving = false;

	public JMSConsumer(List<String> queues, String ftp, String temp) {
		this.ftp = ftp;
		this.queues = queues;
		this.temp = temp;

		client = new FTPClient(ftp, temp);
		connectFTP();
	}

	public void connectFTP() {
		try {
			client.connect(temp, ftp);
		} catch (FileSystemException e) {
		}
	}

	public void connect() {
		log.info("jms [" + queues + "] connect");
		try {
			connection = JMSConnectionFactory.getIBMMQFactory().createConnection();
			connection.setExceptionListener(this);
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

			queues.forEach(queue -> {
				try {
					Destination destination = session.createQueue(queue);
					MessageConsumer consumer = session.createConsumer(destination);
					consumers.add(consumer);
				} catch (JMSException e) {
					log.error(e);
				}
			});

			isConnected = true;

			startReceive();
		} catch (JMSException e) {
			log.error("jms [" + queues + "] connect exception: " + e.getMessage());
			if (e.getCause() != null)
				log.error("jms [" + queues + "] connect exception: " + e.getCause().getMessage());
		}
	}

	public void startReceive() {
		log.info("jms [" + queues + "] start receive");
		try {
			if (consumers != null && !consumers.isEmpty()) {
				consumers.forEach(consumer -> {
					try {
						consumer.setMessageListener(this);
					} catch (JMSException e) {
						log.error(e);
					}
				});
			}
			isReceiving = true;
			if (connection != null)
				connection.start();
		} catch (JMSException e) {
			log.error("jms [" + queues + "] start receive exception: " + e.getMessage());
		}
	}

	public void stopReceive() {
		log.info("jms [" + queues + "] stop receive");
		try {
			if (consumers != null && !consumers.isEmpty()) {
				consumers.forEach(consumer -> {
					try {
						consumer.setMessageListener(null);
					} catch (JMSException e) {
						log.error(e);
					}
				});
			}
			if (connection != null)
				connection.stop();
			isReceiving = false;
		} catch (JMSException e) {
			log.error("jms [" + queues + "] stop receive exception: " + e.getMessage());
		} finally {
			isReceiving = false;
		}
	}

	public void disconnect() {
		log.info("jms [" + queues + "] disconnect");
		try {
			stopReceive();

			if (consumers != null && !consumers.isEmpty()) {
				consumers.forEach(consumer -> {
					try {
						consumer.close();
					} catch (JMSException e) {
						log.error(e);
					}
				});
				consumers.clear();
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
			log.error("jms [" + queues + "] disconnect exception: " + e.getMessage());
		}
	}

	@Override
	public void onException(JMSException e) {
		log.error("jms [" + queues + "] onException: " + e.getMessage());

		disconnect();
		isConnected = false;
	}

	@Override
	public void onMessage(Message message) {
		try {
			log.info("jms [" + queues + "] get: " + message.getJMSMessageID());

			String destination = null;
			if (message.getJMSDestination() instanceof Queue) {
				destination = ((Queue) message.getJMSDestination()).getQueueName();
			} else if (message.getJMSDestination() instanceof Topic) {
				destination = ((Queue) message.getJMSDestination()).getQueueName();
			}

			String filename = JMSMessage.saveToFile(temp, message, destination);
			client.upload(filename);
			log.info("ftp [" + ftp + "] put: " + message.getJMSMessageID());
			session.commit();
			log.info("jms [" + queues + "] commit: " + message.getJMSMessageID());
		} catch (IOException e) {
			Emailer.send("jmsftp error", Main.getStackTrace(e));
		} catch (JMSException e) {
			try {
				session.rollback();
				log.error("jms [" + queues + "] rollback: " + e.getMessage());
				try {
					Thread.sleep(Config.COMMON.TIMEOUT);
				} catch (InterruptedException e1) {
				}
			} catch (JMSException e1) {
			}
		}
	}
}
