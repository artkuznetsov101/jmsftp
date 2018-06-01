package jmsftp;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

public class JMSConsumer implements Runnable, AutoCloseable {
	Connection connection;
	Session session;
	Destination destination;
	MessageConsumer consumer;

	public JMSConsumer(ConnectionFactory factory, String destination_name, boolean is_queue) throws JMSException {

		connection = factory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		if (is_queue)
			destination = session.createQueue(destination_name);
		else
			destination = session.createTopic(destination_name);

		consumer = session.createConsumer(destination);

		connection.start();
	}

	@Override
	public void close() {
		try {
			consumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Message msg;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Thread.sleep(100);
				if ((msg = consumer.receive()) != null) {
					System.out.print("receive: ");
					if (msg instanceof TextMessage) {
						System.out.println(TextMessage.class);
					} else if (msg instanceof MapMessage) {
						System.out.println(MapMessage.class);
					} else if (msg instanceof ObjectMessage) {
						System.out.println(ObjectMessage.class);
					} else if (msg instanceof BytesMessage) {
						System.out.println(BytesMessage.class);
					} else if (msg instanceof StreamMessage) {
						System.out.println(StreamMessage.class);
					}
					System.out.println(msg);
				}
			}
		} catch (InterruptedException ex) {
			
		} catch (JMSException ex) {
			if (ex.getCause() instanceof InterruptedException) {

			} else {
				ex.printStackTrace();
			}
		}
	}
}
