package jmsftp;

public class Settings {

	public static class COMMON {
		static String TEMP_DIR= "C:\\!!!";
		
		static String FILE_NAME = "inventory";
		static String FILE_EXTENSION = "xml";	
	}
	
	public static class SFTP {
		static String HOST = "127.0.0.1";
		static String PORT = "22";

		static String USERNAME = "app";
		static String PASSWORD = "app";
	}

	public static class JMS {
		static String HOST = "127.0.0.1";
		static String PORT = "1414";

		static String USER = "app";
		static String PASSWORD = "app";

		static String QUEUE_MANAGER = "QM1";
		static String CHANNEL = "DEV.APP.SVRCONN";
		static String QUEUE_NAME = "DEV.QUEUE.1";

		static int CONNECT_TIMEOUT = 5000;
		static int ROLLBACK_TIMEOUT = 5000;	
	}
}
