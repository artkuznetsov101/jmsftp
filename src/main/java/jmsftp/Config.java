package jmsftp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ini4j.Wini;

public class Config {

	public static String NAME = "jmsftp.ini";

	public static class COMMON {

		static boolean JMS2FTP;
		static boolean FTP2JMS;

		static String FILE_NAME_MASK;
		static List<String> FILE_NAME_MASK_LIST;
		static String FILE_EXTENSION;

		static int TIMEOUT;
	}

	public static class FTP {
		static String HOST;
		static String PORT;

		static String USERNAME;
		static String PASSWORD;

		static String FTP_DIR;
		static String TEMP_DIR;
	}

	public static class JMS {
		static String HOST;
		static String PORT;

		static String USERNAME;
		static String PASSWORD;

		static String QUEUE_MANAGER;
		static String CHANNEL;
		static String RECV_QUEUES_NAME;
		static String SEND_QUEUE_NAME;

		static String FTP_DIR;
		static String TEMP_DIR;
	}

	public static class MAIL {
		static String SMTP_SERVER;
		static int SMTP_PORT;
		static boolean SMTP_USE_SSL;

		static String FROM_USERNAME;
		static String FROM_PASSWORD;
		static String FROM_EMAIL;

		static String EMAIL_TO;

		static boolean SEND_START_EMAIL;
	}

	public static void setConfig(Wini ini) {
		// COMMON section
		if ((Config.COMMON.FILE_NAME_MASK = ini.get("COMMON", "FILE_MASK")) == null)
			throw new IllegalArgumentException("COMMON->FILE_MASK parameter not specified in ini file. Exit");
		Config.COMMON.FILE_NAME_MASK_LIST = Arrays.asList(Config.COMMON.FILE_NAME_MASK.split("\\s*,\\s*"));

		if ((Config.COMMON.FILE_EXTENSION = ini.get("COMMON", "FILE_EXTENSION")) == null)
			throw new IllegalArgumentException("COMMON->FILE_EXTENSION parameter not specified in ini file. Exit");

		Config.COMMON.JMS2FTP = ini.get("COMMON", "JMS2FTP", Boolean.TYPE).booleanValue();
		Config.COMMON.FTP2JMS = ini.get("COMMON", "FTP2JMS", Boolean.TYPE).booleanValue();

		if ((Config.COMMON.TIMEOUT = ini.get("COMMON", "TIMEOUT", Integer.TYPE).intValue()) == 0)
			throw new IllegalArgumentException("COMMON->TIMEOUT parameter not specified in ini file. Exit");

		// FTP section
		if ((Config.FTP.HOST = ini.get("FTP", "HOST")) == null)
			throw new IllegalArgumentException("FTP->HOST parameter not specified in ini file. Exit");
		if ((Config.FTP.PORT = ini.get("FTP", "PORT")) == null)
			throw new IllegalArgumentException("FTP->PORT parameter not specified in ini file. Exit");
		if ((Config.FTP.USERNAME = ini.get("FTP", "USERNAME")) == null)
			throw new IllegalArgumentException("FTP->USERNAME parameter not specified in ini file. Exit");
		if ((Config.FTP.PASSWORD = ini.get("FTP", "PASSWORD")) == null)
			throw new IllegalArgumentException("FTP->PASSWORD parameter not specified in ini file. Exit");
		if ((Config.FTP.FTP_DIR = ini.get("FTP", "FTP_DIR")) == null)
			throw new IllegalArgumentException("FTP->FTP_DIR parameter not specified in ini file. Exit");
		if ((Config.FTP.TEMP_DIR = ini.get("FTP", "TEMP_DIR")) == null)
			throw new IllegalArgumentException("FTP->TEMP_DIR parameter not specified in ini file. Exit");

		// JMS section
		if ((Config.JMS.HOST = ini.get("JMS", "HOST")) == null)
			throw new IllegalArgumentException("JMS->HOST parameter not specified in ini file. Exit");
		if ((Config.JMS.PORT = ini.get("JMS", "PORT")) == null)
			throw new IllegalArgumentException("JMS->PORT parameter not specified in ini file. Exit");
		if ((Config.JMS.USERNAME = ini.get("JMS", "USERNAME")) == null)
			throw new IllegalArgumentException("JMS->USERNAME parameter not specified in ini file. Exit");
		if ((Config.JMS.PASSWORD = ini.get("JMS", "PASSWORD")) == null)
			throw new IllegalArgumentException("JMS->PASSWORD parameter not specified in ini file. Exit");

		if ((Config.JMS.QUEUE_MANAGER = ini.get("JMS", "QUEUE_MANAGER")) == null)
			throw new IllegalArgumentException("JMS->QUEUE_MANAGER parameter not specified in ini file. Exit");
		if ((Config.JMS.CHANNEL = ini.get("JMS", "CHANNEL")) == null)
			throw new IllegalArgumentException("JMS->CHANNEL parameter not specified in ini file. Exit");
		if ((Config.JMS.RECV_QUEUES_NAME = ini.get("JMS", "RECV_QUEUES_NAME")) == null)
			throw new IllegalArgumentException("JMS->RECV_QUEUES_NAME parameter not specified in ini file. Exit");
		if ((Config.JMS.SEND_QUEUE_NAME = ini.get("JMS", "SEND_QUEUE_NAME")) == null)
			throw new IllegalArgumentException("JMS->SEND_QUEUE_NAME parameter not specified in ini file. Exit");
		if ((Config.JMS.TEMP_DIR = ini.get("JMS", "TEMP_DIR")) == null)
			throw new IllegalArgumentException("JMS->TEMP_DIR parameter not specified in ini file. Exit");
		if ((Config.JMS.FTP_DIR = ini.get("JMS", "FTP_DIR")) == null)
			throw new IllegalArgumentException("JMS->FTP_DIR parameter not specified in ini file. Exit");

		// MAIL section
		if ((Config.MAIL.SMTP_SERVER = ini.get("MAIL", "SMTP_SERVER")) == null)
			throw new IllegalArgumentException("MAIL->SMTP_SERVER parameter not specified in ini file. Exit");
		if ((Config.MAIL.SMTP_PORT = ini.get("MAIL", "SMTP_PORT", Integer.TYPE).intValue()) == 0)
			throw new IllegalArgumentException("MAIL->SMTP_PORT parameter not specified in ini file. Exit");
		Config.MAIL.SMTP_USE_SSL = ini.get("MAIL", "SMTP_USE_SSL", Boolean.TYPE).booleanValue();

		if ((Config.MAIL.FROM_USERNAME = ini.get("MAIL", "FROM_USERNAME")) == null)
			throw new IllegalArgumentException("MAIL->FROM_USERNAME parameter not specified in ini file. Exit");
		if ((Config.MAIL.FROM_PASSWORD = ini.get("MAIL", "FROM_PASSWORD")) == null)
			throw new IllegalArgumentException("MAIL->FROM_PASSWORD parameter not specified in ini file. Exit");
		if ((Config.MAIL.FROM_EMAIL = ini.get("MAIL", "FROM_EMAIL")) == null)
			throw new IllegalArgumentException("MAIL->FROM_EMAIL parameter not specified in ini file. Exit");

		if ((Config.MAIL.EMAIL_TO = ini.get("MAIL", "EMAIL_TO")) == null)
			throw new IllegalArgumentException("MAIL->EMAIL_TO parameter not specified in ini file. Exit");

		Config.MAIL.SEND_START_EMAIL = ini.get("MAIL", "SEND_START_EMAIL", Boolean.TYPE).booleanValue();
	}

	static String getMappedQueueName(String queue) {
		List<String> list = new ArrayList<String>();
		for (String item : Config.COMMON.FILE_NAME_MASK_LIST) {
			if (queue.contains(item))
				list.add(item);
		}
		if (list.isEmpty()) {
			return queue;
		} else {
			return Collections.max(list, Comparator.comparing(s -> s.length()));
		}
	}
}
