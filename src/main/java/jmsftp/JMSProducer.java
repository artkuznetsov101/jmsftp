package jmsftp;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class JMSProducer implements AutoCloseable {
	Connection connection;
	Session session;
	Destination destination;
	MessageProducer producer;

	public JMSProducer(ConnectionFactory factory, String destination_name, boolean is_queue) throws JMSException {

		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		if (is_queue)
			destination = session.createQueue(destination_name);
		else
			destination = session.createTopic(destination_name);
		
		producer = session.createProducer(destination);

		connection.start();
	}

	@Override
	public void close() {
		try {
			producer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}		
	}

	public void send(String text) {
		try {
			producer.send(session.createTextMessage(text));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
