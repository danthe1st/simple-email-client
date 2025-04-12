package io.github.danthe1st.simple_mail_client.email.communication;

class UncheckedWrapperException extends RuntimeException {
	private final Exception cause;
	
	public UncheckedWrapperException(Exception e) {
		super(e);
		this.cause = e;
	}
	
	@Override
	public Exception getCause() {
		return cause;
	}
}