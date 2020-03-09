package jmsftp;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Emailer {
	private static final Logger log = LogManager.getLogger();

	public static void send(String subject, String message) {
		try {
			Email email = new SimpleEmail();
			email.setHostName(Config.MAIL.SMTP_SERVER);
			email.setSmtpPort(Config.MAIL.SMTP_PORT);
			email.setAuthenticator(new DefaultAuthenticator(Config.MAIL.FROM_USERNAME, Config.MAIL.FROM_PASSWORD));
			email.setSSLOnConnect(Config.MAIL.SMTP_USE_SSL);
			email.setFrom(Config.MAIL.FROM_EMAIL);
			email.addTo(Config.MAIL.EMAIL_TO);
			email.setSubject(subject);
			email.setMsg(message);
			email.send();
			log.info(subject + " report email successfully sended");
		} catch (EmailException e) {
			log.error("report email not sended: " + e.getMessage());
		}
	}
}