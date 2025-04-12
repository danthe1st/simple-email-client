package io.github.danthe1st.simple_mail_client.email.data;

import java.util.List;

import io.github.danthe1st.simple_mail_client.email.EmailException;

public interface EmailFolder {
	String getName();
	List<Email> getEmails() throws EmailException;
}