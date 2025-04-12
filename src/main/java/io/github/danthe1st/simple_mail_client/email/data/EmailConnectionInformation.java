package io.github.danthe1st.simple_mail_client.email.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.danthe1st.simple_mail_client.email.StorageException;

public record EmailConnectionInformation(
		ServerInformation incoming,
		ServerInformation outgoing,
		String senderAddress,
		String username,
		String password) {
	public EmailConnectionInformation {
		Objects.requireNonNull(incoming);
		Objects.requireNonNull(outgoing);
		Objects.requireNonNull(senderAddress);
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);
	}
	
	public record ServerInformation(String serverAddress, int port) {
		public ServerInformation {
			Objects.requireNonNull(serverAddress);
		}
	}
	
	public void save(Path path) throws StorageException {
		ObjectMapper mapper = new ObjectMapper();
		try{
			mapper.writeValue(path.toFile(), this);
		}catch(IOException e){
			throw new StorageException("Cannot write connection information to file", e);
		}
	}
	
	public static EmailConnectionInformation load(Path path) throws StorageException {
		ObjectMapper mapper = new ObjectMapper();
		try{
			return mapper.readValue(path.toFile(), EmailConnectionInformation.class);
		}catch(IOException e){
			throw new StorageException("Cannot read connection information from file", e);
		}
	}
}
