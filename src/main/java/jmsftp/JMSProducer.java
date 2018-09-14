package jmsftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

	FTPClient client = new FTPClient();

	Connection connection;
	Session session;
	Destination destination;
	MessageProducer producer;
	String queue;
	boolean isConnected = false;

	public JMSProducer(String queue) {
		this.queue = queue;
		connectFTP();
	}

	public void connectFTP() {
		try {
			client.connect(Config.FTP.TEMP_DIR, Config.FTP.FTP_DIR);
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
	}

	public void connect() {
		log.info("ftp2jms -> jms [" + queue + "] connect");
		try {
			connection = JMSConnectionFactory.getIBMMQ().createConnection();
			connection.setExceptionListener(this);
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(queue);
			producer = session.createProducer(destination);
			isConnected = true;
		} catch (JMSException e) {
			log.error("ftp2jms -> jms [" + queue + "] connect exception: " + e.getMessage());
			if (e.getCause() != null)
				log.error("jms2ftp -> jms [" + queue + "] connect exception: " + e.getCause().getMessage());
		}
	}

	public void disconnect() {
		log.info("ftp2jms -> jms [" + queue + "] disconnect");
		try {
			if (producer != null) {
				producer.close();
				producer = null;
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
			log.error("ftp2jms -> jms [" + queue + "] disconnect exception: " + e.getMessage());
		}
	}

	public void send(String path, String file) throws JMSException, IOException {
		producer.send(session.createTextMessage(new String(Files.readAllBytes(Paths.get(path, file)))));
	}

	@Override
	public void onException(JMSException e) {
		log.error("ftp2jms -> jms [" + queue + "] onException: " + e.getMessage());

		disconnect();
		isConnected = false;
	}
}
