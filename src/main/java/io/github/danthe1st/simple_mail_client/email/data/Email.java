package io.github.danthe1st.simple_mail_client.email.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import io.github.danthe1st.simple_mail_client.email.EmailException;

public record Email(
		String from,
		List<String> to,
		String subject,
		Lazy<List<EmailContent>> content,
		LocalDateTime timestamp) {
	public Email {
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		Objects.requireNonNull(content);
	}
	
	public record EmailContent(String content, String contentType) {
		
	}
	
	public static class Lazy<T> {
		private T value;
		private ExceptionalSupplier<T> supplier;
		
		public Lazy(ExceptionalSupplier<T> supplier) {
			this.supplier = supplier;
		}
		
		public synchronized T getValue() throws EmailException {
			if(value == null){
				value = supplier.get();
			}
			return value;
		}
		
		@FunctionalInterface
		public interface ExceptionalSupplier<T> {
			T get() throws EmailException;
		}
	}
}
