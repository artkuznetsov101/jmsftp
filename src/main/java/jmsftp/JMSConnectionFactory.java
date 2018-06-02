package jmsftp;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

public class JMSConnectionFactory {

	public static ConnectionFactory getIBMMQ() throws JMSException {

		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		JmsConnectionFactory factory = ff.createConnectionFactory();

		factory.setStringProperty(WMQConstants.WMQ_HOST_NAME, Settings.HOST);
		factory.setIntProperty(WMQConstants.WMQ_PORT, Settings.PORT);
		factory.setStringProperty(WMQConstants.WMQ_CHANNEL, Settings.CHANNEL);
		factory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		factory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, Settings.QUEUE_MANAGER);
		factory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "jms ftp bridge");
		factory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
		factory.setStringProperty(WMQConstants.USERID, Settings.APP_USER);
		factory.setStringProperty(WMQConstants.PASSWORD, Settings.APP_PASSWORD);

		return factory;
	}
}
