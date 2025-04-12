package io.github.danthe1st.simple_mail_client.email.communication;

import java.util.List;

import io.github.danthe1st.simple_mail_client.email.EmailException;
import io.github.danthe1st.simple_mail_client.email.data.Email;
import io.github.danthe1st.simple_mail_client.email.data.EmailFolder;
import jakarta.mail.Folder;

class EmailFolderImpl implements EmailFolder {
	private final Folder folder;
	private List<Email> emails;
	private boolean emailsInitialized;
	private final EmailReceiver receiver;
	
	EmailFolderImpl(EmailReceiver receiver, Folder folder) {
		this.receiver = receiver;
		this.folder = folder;
	}
	
	@Override
	public String getName() {
		return folder.getName();
	}
	
	@Override
	public List<Email> getEmails() throws EmailException {
		if(!emailsInitialized){
			emails = receiver.receiveEmailsInFolder(folder);
		}
		return emails;
	}
	
}
