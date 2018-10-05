package jmsftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JMSMessage {
	private static final Logger log = LogManager.getLogger();

	public enum Type {
		MESSAGE, TEXT, MAP, OBJECT, BYTES, STREAM
	}

	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	public static String saveToFile(String tempDir, Message message, String queue) throws JMSException, IOException {

		String filename = getFileName(message, queue);
		String path = Paths.get(tempDir, filename).toString();

		byte[] data = null;

		switch (JMSMessage.getType(message)) {
		case TEXT:
			data = ((TextMessage) message).getText().getBytes();
			break;
		case MAP:
			break;
		case OBJECT:
			break;
		case BYTES:
			data = new byte[(int) ((BytesMessage) message).getBodyLength()];
			((BytesMessage) message).readBytes(data);
			break;
		case STREAM:
			break;
		case MESSAGE:
		default:
			break;
		}
		if (data != null) {
			Files.write(Paths.get(path), data, StandardOpenOption.CREATE);
		} else {
			Files.createFile(Paths.get(path));
			log.info("empty jms message received: " + path);
		}
		return filename;
	}

	public static TextMessage loadFromFile(String path, TextMessage message) throws JMSException, IOException {
		message.setText(new String(Files.readAllBytes(Paths.get(path))));
		return message;
	}

	public static String getTimestamp() {
		return formatter.format(new Date());
	}

	public static String getFileName(Message message, String queue) throws JMSException {
		return getTimestamp() + "_" + Config.getMappedQueueName(queue).replace(':', '_') + "_" + message.getJMSMessageID().replace(':', '_') + "." + Config.COMMON.FILE_EXTENSION;
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
