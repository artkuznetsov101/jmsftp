package jmsftp;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

public class AbstractConnectionFactory {

	public static ConnectionFactory getIBMMQConnectionFactory(String host, int port, String queue_manager,
			String channel, String queue_name, String app_user, String app_password) throws JMSException {

		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		JmsConnectionFactory factory = ff.createConnectionFactory();
		
		factory.setStringProperty(WMQConstants.WMQ_HOST_NAME, host);
		factory.setIntProperty(WMQConstants.WMQ_PORT, port);
		factory.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
		factory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		factory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queue_manager);
		factory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "jms ftp bridge");
		factory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
		factory.setStringProperty(WMQConstants.USERID, app_user);
		factory.setStringProperty(WMQConstants.PASSWORD, app_password);
		
		return factory;
	}
}
