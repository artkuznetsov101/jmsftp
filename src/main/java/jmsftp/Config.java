package jmsftp;

import org.ini4j.Wini;

public class Config {

	public static String NAME = "jmsftp.ini";
	
	public static class COMMON {
		static String TEMP_DIR;

		static String FILE_NAME;
		static String FILE_EXTENSION;
	}

	public static class SFTP {
		static String HOST;
		static String PORT;

		static String USERNAME;
		static String PASSWORD;
	}

	public static class JMS {
		static String HOST;
		static String PORT;

		static String USERNAME;
		static String PASSWORD;

		static String QUEUE_MANAGER;
		static String CHANNEL;
		static String QUEUE_NAME;

		static int CONNECT_TIMEOUT;
		static int ROLLBACK_TIMEOUT;
	}

	public static void setConfig(Wini ini) {
		// COMMON section
		if ((Config.COMMON.TEMP_DIR = ini.get("COMMON", "TEMP_DIR")) == null)
			throw new IllegalArgumentException("COMMON->TEMP_DIR parameter not specified in ini file. Exit");
		if ((Config.COMMON.FILE_NAME = ini.get("COMMON", "FILE_NAME")) == null)
			throw new IllegalArgumentException("COMMON->FILE_NAME parameter not specified in ini file. Exit");
		if ((Config.COMMON.FILE_EXTENSION = ini.get("COMMON", "FILE_EXTENSION")) == null)
			throw new IllegalArgumentException("COMMON->FILE_EXTENSION parameter not specified in ini file. Exit");

		// SFTP section
		if ((Config.SFTP.HOST = ini.get("SFTP", "HOST")) == null)
			throw new IllegalArgumentException("SFTP->HOST parameter not specified in ini file. Exit");
		if ((Config.SFTP.PORT = ini.get("SFTP", "PORT")) == null)
			throw new IllegalArgumentException("SFTP->PORT parameter not specified in ini file. Exit");
		if ((Config.SFTP.USERNAME = ini.get("SFTP", "USERNAME")) == null)
			throw new IllegalArgumentException("SFTP->USERNAME parameter not specified in ini file. Exit");
		if ((Config.SFTP.PASSWORD = ini.get("SFTP", "PASSWORD")) == null)
			throw new IllegalArgumentException("SFTP->PASSWORD parameter not specified in ini file. Exit");

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
		if ((Config.JMS.QUEUE_NAME = ini.get("JMS", "QUEUE_NAME")) == null)
			throw new IllegalArgumentException("JMS->QUEUE_NAME parameter not specified in ini file. Exit");
		if ((Config.JMS.CONNECT_TIMEOUT = ini.get("JMS", "CONNECT_TIMEOUT", Integer.TYPE).intValue()) == 0)
			throw new IllegalArgumentException("JMS->CONNECT_TIMEOUT parameter not specified in ini file. Exit");
		if ((Config.JMS.ROLLBACK_TIMEOUT = ini.get("JMS", "ROLLBACK_TIMEOUT", Integer.TYPE).intValue()) == 0)
			throw new IllegalArgumentException("JMS->ROLLBACK_TIMEOUT parameter not specified in ini file. Exit");
	}
}
