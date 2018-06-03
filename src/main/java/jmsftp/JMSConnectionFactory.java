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

		factory.setStringProperty(WMQConstants.WMQ_HOST_NAME, JMSSettings.HOST);
		factory.setIntProperty(WMQConstants.WMQ_PORT, JMSSettings.PORT);
		factory.setStringProperty(WMQConstants.WMQ_CHANNEL, JMSSettings.CHANNEL);
		factory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		factory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, JMSSettings.QUEUE_MANAGER);
		factory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "jms ftp bridge");
		factory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
		factory.setStringProperty(WMQConstants.USERID, JMSSettings.APP_USER);
		factory.setStringProperty(WMQConstants.PASSWORD, JMSSettings.APP_PASSWORD);

		return factory;
	}
}
