package io.github.danthe1st.simple_mail_client.email.communication;

import java.util.Properties;

import io.github.danthe1st.simple_mail_client.email.EmailException;
import io.github.danthe1st.simple_mail_client.email.data.EmailConnectionInformation;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

public class EmailFactory {
	private final Session session;
	private final EmailConnectionInformation connectionInformation;
	
	public EmailFactory(EmailConnectionInformation connectionInformation) {
		this.connectionInformation = connectionInformation;
		Properties props = new Properties();
		props.put("mail.smtp.auth", String.valueOf(connectionInformation.password() != null));
		props.put("mail.smtp.starttls.enable", String.valueOf(connectionInformation.outgoing().secure()));
		props.put("mail.smtp.host", connectionInformation.outgoing().serverAddress());
		props.put("mail.smtp.port", connectionInformation.outgoing().port());
		
		props.put("mail.store.protocol", connectionInformation.incoming().secure() ? "imaps" : "imap");
		props.put("mail.imap.host", connectionInformation.incoming().serverAddress());
		props.put("mail.imap.port", connectionInformation.incoming().port());
		
		session = Session.getInstance(
				props,
				new Authenticator() {
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(connectionInformation.username(), connectionInformation.password());
					}
				}
		);
	}
	
	public EmailSender createSender() throws EmailException {
		// ensure address is valid
		try{
			new InternetAddress(connectionInformation.senderAddress());
		}catch(AddressException e){
			throw new EmailException("Username is not a valid E-Mail address", e);
		}
		return new EmailSender(session);
	}
	
	public EmailReceiver createReceiver() throws EmailException {
		return new EmailReceiver(session, connectionInformation);
	}
}
