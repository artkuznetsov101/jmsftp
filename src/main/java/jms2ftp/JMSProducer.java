package jms2ftp;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JMSProducer implements ExceptionListener {
	private static final Logger log = LogManager.getLogger();

	FTPClient client;
	String ftp;

	Connection connection;
	Session session;
	List<MessageProducer> producers = new ArrayList<>();
	List<String> queues;

	String temp;

	boolean isConnected = false;

	public JMSProducer(String ftp, List<String> queues, String temp) {
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
			e.printStackTrace();
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
					MessageProducer producer = session.createProducer(destination);
					producers.add(producer);
				} catch (JMSException e) {
					log.error(e);
				}
			});

			isConnected = true;
		} catch (JMSException e) {
			log.error("jms [" + queues + "] connect exception: " + e.getMessage());
			if (e.getCause() != null)
				log.error("jms [" + queues + "] connect exception: " + e.getCause().getMessage());
		}
	}

	public void disconnect() {
		log.info("jms [" + queues + "] disconnect");
		try {
			if (producers != null && !producers.isEmpty()) {
				producers.forEach(producer -> {
					try {
						producer.close();
					} catch (JMSException e) {
						log.error(e);
					}
				});
				producers.clear();
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

	public void send(String data) throws JMSException {

		producers.forEach(producer -> {
			try {
				producer.send(session.createTextMessage(data));
			} catch (JMSException e) {
				log.error(e);
			}
		});
	}

	@Override
	public void onException(JMSException e) {
		log.error("jms [" + queues + "] onException: " + e.getMessage());

		disconnect();
		isConnected = false;
	}
}
