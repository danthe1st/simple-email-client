package io.github.danthe1st.simple_mail_client.email.communication;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.danthe1st.simple_mail_client.email.EmailException;
import io.github.danthe1st.simple_mail_client.email.data.Email;
import io.github.danthe1st.simple_mail_client.email.data.Email.EmailContent;
import io.github.danthe1st.simple_mail_client.email.data.Email.Lazy;
import io.github.danthe1st.simple_mail_client.email.data.EmailConnectionInformation;
import io.github.danthe1st.simple_mail_client.email.data.EmailFolder;
import jakarta.mail.Address;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeBodyPart;

public class EmailReceiver implements AutoCloseable {
	private final Store store;
	private final Map<String, EmailFolderImpl> folders = new HashMap<>();
	
	public EmailReceiver(Session session, EmailConnectionInformation connectionInformation) throws EmailException {
		try{
			store = session.getStore();
			store.connect(connectionInformation.incoming().serverAddress(), connectionInformation.username(), connectionInformation.password());
			Folder inbox = store.getFolder("INBOX");
			folders.put("INBOX", new EmailFolderImpl(this, inbox));
		}catch(NoSuchProviderException e){
			throw new EmailException("A protocol error occured", e);
		}catch(MessagingException e){
			throw new EmailException("Cannot connect to server", e);
		}
	}
	
	public List<EmailFolder> getFolders() throws EmailException {
		try{
			List<EmailFolder> folders = new ArrayList<>();
			extractFolders(folders, store.getDefaultFolder());
			return folders;
		}catch(MessagingException e){
			throw new EmailException("Cannot read folders", e);
		}
	}
	
	private void extractFolders(List<EmailFolder> target, Folder current) throws MessagingException {
		if((current.getType() & Folder.HOLDS_MESSAGES) != 0){
			target.add(new EmailFolderImpl(this, current));
		}
		if((current.getType() & Folder.HOLDS_FOLDERS) != 0){
			for(Folder inner : current.list()){
				extractFolders(target, inner);
			}
		}
		
	}
	
	List<Email> receiveEmailsInFolder(Folder folder) throws EmailException {
		try{
			if(!folder.isOpen()){
				folder.open(Folder.READ_ONLY);
			}
			Message[] messages = folder.getMessages();
			return Stream.of(messages).map(this::messageToEmail).toList();
		}catch(MessagingException | UncheckedWrapperException e){
			throw new EmailException("Cannot receive E-Mails", e);
		}
	}
	
	private Email messageToEmail(Message msg) {
		try{
			return new Email(
					Stream.of(msg.getFrom())
						.map(Address::toString)
						.collect(Collectors.joining("; ")),
					Stream.of(emptyIfNull(msg.getAllRecipients()))
						.map(Address::toString)
						.toList(),
					msg.getSubject(),
					new Lazy<>(() -> extractContent(msg)),
					LocalDateTime.ofInstant(msg.getSentDate().toInstant(), ZoneId.systemDefault())
			);
		}catch(MessagingException e){
			throw new UncheckedWrapperException(e);
		}
	}
	
	private List<EmailContent> extractContent(Message msg) throws EmailException {
		List<EmailContent> target = new ArrayList<>();
		List<EmailContent> fallback = new ArrayList<>();
		try{
			extractContent(target, fallback, msg.getContent(), msg.getContentType());
		}catch(IOException | MessagingException e){
			throw new EmailException("Cannot read E-Mail content", e);
		}
		if(!target.isEmpty()){
			return target;
		}
		return fallback;
	}
	
	private void extractContent(List<EmailContent> target, List<EmailContent> fallback, Object toExtract, String contentType) throws IOException, MessagingException {
		switch(toExtract) {
		case Multipart multipart -> {
			for(int i = 0; i < multipart.getCount(); i++){
				extractContent(target, fallback, multipart.getBodyPart(i), multipart.getContentType());
			}
		}
		case MimeBodyPart part -> {
			extractContent(target, fallback, part.getContent(), part.getContentType());
		}
		case String s when contentType.toLowerCase().startsWith("text/") -> target.add(new EmailContent(s, contentType));
		case String s -> {
			fallback.add(new EmailContent(s, contentType));
		}
		case Object o -> {
			System.out.println("unknown content: " + o.getClass());
		}
		}
	}
	
	private Address[] emptyIfNull(Address[] addresses) {
		if(addresses == null){
			return new Address[0];
		}
		return addresses;
	}
	
	@Override
	public void close() throws Exception {
		store.close();
	}
}
