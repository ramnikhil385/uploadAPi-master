package com.doc360.apibridge.utility;

import java.security.Security;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Utility for Sending Emails when errors occur
 */
@Component
public class MailUtil {

	private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);  

    @Autowired
    MailUtilProperties mailProps;
    
	@Value("${sendErrorMail}")
	private boolean sendErrorMail;

	@Autowired
	private Environment environment;
	
	/**
	 * Takes in message body and sends email using properties defined
	 * in Application.properties  file
	 *
	 * @param body The body of the message to be sent
	 * @throws MessagingException Exception if there is an error sending email
	 * @throws Exception Exception if there is an error in the function
	 */
	public void sendMail(String body) {

		if(!sendErrorMail)
		{
			logger.info("sendMail(): no error mail is sent due to configuration");
			return;
		}
		
		String[] toRecipients = null;
		String[] ccRecipients = null;
		
		String host = mailProps.getHost();
		String user = mailProps.getUser();
		String pwd = mailProps.getPwd();
		String profile=environment.getActiveProfiles()[0]!=null ? environment.getActiveProfiles()[0].toString() :"" ;
		String subject = mailProps.getSubject() + "-"+ profile;
		String from = mailProps.getFrom();

		if (mailProps.getTo() != null) {
			toRecipients = mailProps.getTo().trim().split(",");
		}
		if (mailProps.getCc() != null) {
			ccRecipients = mailProps.getCc().trim().split(",");
		}
		Properties systemProperties = System.getProperties();
		systemProperties.setProperty("mail.smtp.host", host);

		// To make the secure session
		systemProperties.putAll(initSecurity());
		Session session = Session.getInstance(systemProperties);

		try {
			MimeMessage message = new MimeMessage(session);

			if (!from.trim().isEmpty()) {
				message.setFrom(new InternetAddress(from));
			}
			if (toRecipients != null && toRecipients.length != 0) {
				for (String to : toRecipients) {
					if (!to.trim().isEmpty()) {
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
					}
				}
			}

			if (ccRecipients != null && ccRecipients.length != 0) {
				for (String cc : ccRecipients) {
					if (!cc.trim().isEmpty()) {
						message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc));
					}
				}
			}
			message.setSubject(subject);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			message.setContent(multipart);
			Transport.send(message);
			logger.info("Successfully Sent Error e-mail");
			
		} catch (MessagingException mex) {
			logger.error("Messaging Exception thrown when sending email: " + mex);
			
		} catch (Exception e) {
			logger.error("Exception thrown when sending email: " + e);

		}
	}

	/**
	 * Initializes security settings for Mail server
	 *
	 * @return Properties object containing the security settings
	 */
	@SuppressWarnings("restriction")
	private Properties initSecurity() {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		Properties props = System.getProperties();
		// IMAP provider
		props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
		// POP3 provider
		props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
		// NNTP provider (if any)
		// props.setProperty( "mail.nntp.socketFactory.class", SSL_FACTORY);

		// IMAP provider
		props.setProperty("mail.imap.socketFactory.fallback", "false");
		// POP3 provider
		props.setProperty("mail.pop3.socketFactory.fallback", "false");
		// NNTP provider (if any)
		// props.setProperty( "mail.nntp.socketFactory.fallback", "false");

		// IMAP provider
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.imap.socketFactory.port", "993");
		// POP3 provider
		props.setProperty("mail.pop3.port", "995");
		props.setProperty("mail.pop3.socketFactory.port", "995");
		// NNTP provider (if any)
		// props.setProperty( "mail.pop3.port", "563");
		// props.setProperty( "mail.pop3.socketFactory.port", "563");

		return props;
	}
	
    
	
	
}
