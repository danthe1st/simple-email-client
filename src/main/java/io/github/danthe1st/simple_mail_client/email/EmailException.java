package io.github.danthe1st.simple_mail_client.email;

public class EmailException extends Exception {
	public EmailException(String message, Exception cause) {
		super(message, cause);
	}
}
