package jmsftp;

public class Settings {
	static String HOST = "127.0.0.1";
	static int PORT = 1414;

	static String QUEUE_MANAGER = "QM1";
	static String CHANNEL = "DEV.APP.SVRCONN";

	static String QUEUE_NAME = "DEV.QUEUE.1";
	static boolean IS_QUEUE = true;

	static String APP_USER = "app";
	static String APP_PASSWORD = "app";
	
	static int CHECK_CONNECT = 5 * 1000;
}
