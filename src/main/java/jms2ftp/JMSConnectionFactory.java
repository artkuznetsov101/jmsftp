package jms2ftp;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

public class JMSConnectionFactory {
	public static ConnectionFactory getIBMMQFactory() throws JMSException {

		JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
		JmsConnectionFactory factory = ff.createConnectionFactory();

		factory.setStringProperty(WMQConstants.WMQ_HOST_NAME, Config.JMS.HOST);
		factory.setStringProperty(WMQConstants.WMQ_PORT, Config.JMS.PORT);
		factory.setStringProperty(WMQConstants.WMQ_CHANNEL, Config.JMS.CHANNEL);
		factory.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
		factory.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, Config.JMS.QUEUE_MANAGER);
		factory.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, "jms2ftp bridge");
		factory.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
		factory.setStringProperty(WMQConstants.USERID, Config.JMS.USERNAME);
		factory.setStringProperty(WMQConstants.PASSWORD, Config.JMS.PASSWORD);

		return factory;
	}

	public static ConnectionFactory getActiveMQFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(Config.JMS.HOST + ":" + Config.JMS.PORT);

		RedeliveryPolicy policy = new RedeliveryPolicy();
		policy.setMaximumRedeliveries(-1);
		factory.setRedeliveryPolicy(policy);

		return factory;
	}
}
