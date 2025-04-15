package io.github.danthe1st.simple_mail_client.email.communication;

import io.github.danthe1st.simple_mail_client.email.EmailException;
import io.github.danthe1st.simple_mail_client.email.data.Email;
import io.github.danthe1st.simple_mail_client.email.data.Email.EmailContent;
import jakarta.mail.BodyPart;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailSender {
	private final Session session;

	EmailSender(Session session) {
		this.session = session;
	}
	
	public void sendMail(Email email) throws EmailException {
		MimeMessage message = new MimeMessage(session);
		try{
			InternetAddress address = new InternetAddress(email.from());
			message.setFrom(address);
			message.setRecipients(RecipientType.TO, email.to()
					.stream()
				.map(this::toInternetAddressUnchecked)
				.toArray(InternetAddress[]::new)
			);
			message.setSubject(email.subject());
			
			Multipart multipart = new MimeMultipart();
			for(EmailContent contentPart : email.content().getValue()){
				BodyPart bodyPart = new MimeBodyPart();
				bodyPart.setContent(contentPart.content(), contentPart.contentType());
				multipart.addBodyPart(bodyPart);
			}
			message.setContent(multipart);
			Transport.send(message);
		}catch(MessagingException e){
			throw new EmailException("Email could not be sent", e);
		}catch (UncheckedWrapperException e) {
			throw new EmailException("Email could not be sent", e.getCause());
		}
	}
	
	private InternetAddress toInternetAddressUnchecked(String address) {
		try{
			return new InternetAddress(address);
		}catch(AddressException e){
			throw new UncheckedWrapperException(e);
		}
		
	}
	
}


