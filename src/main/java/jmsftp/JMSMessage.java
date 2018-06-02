package jmsftp;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

public class JMSMessage {

	public enum Type {
		MESSAGE, TEXT, MAP, OBJECT, BYTES, STREAM
	}

	public static Type getType(Message message) {
		if (message instanceof TextMessage) {
			return Type.TEXT;
		} else if (message instanceof MapMessage) {
			return Type.MAP;
		} else if (message instanceof ObjectMessage) {
			return Type.OBJECT;
		} else if (message instanceof BytesMessage) {
			return Type.BYTES;
		} else if (message instanceof StreamMessage) {
			return Type.STREAM;
		}
		return Type.MESSAGE;
	}
}
