package jmsftp;

import javax.jms.ConnectionFactory;

public class Main {

	static String HOST = "127.0.0.1";
	static int PORT = 1414;

	static String QUEUE_MANAGER = "QM1";
	static String CHANNEL = "DEV.APP.SVRCONN";
	
	static String QUEUE_NAME = "DEV.QUEUE.1";
	static boolean IS_QUEUE = true;
	
	static String APP_USER = "app";
	static String APP_PASSWORD = "app";

	public static void main(String[] args) throws Exception {

		ConnectionFactory factory = AbstractConnectionFactory.getIBMMQConnectionFactory(HOST, PORT, QUEUE_MANAGER,
				CHANNEL, QUEUE_NAME, APP_USER, APP_PASSWORD);
		
		try(JMSProducer producer = new JMSProducer(factory, QUEUE_NAME, IS_QUEUE);){
			producer.send("test message");
		}
	}
}
