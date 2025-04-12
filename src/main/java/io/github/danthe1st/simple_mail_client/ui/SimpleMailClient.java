package io.github.danthe1st.simple_mail_client.ui;

import java.io.IOException;
import java.nio.file.Files;

import io.github.danthe1st.simple_mail_client.email.EmailException;
import io.github.danthe1st.simple_mail_client.email.StorageException;
import io.github.danthe1st.simple_mail_client.email.communication.EmailFactory;
import io.github.danthe1st.simple_mail_client.email.communication.EmailReceiver;
import io.github.danthe1st.simple_mail_client.email.communication.EmailSender;
import io.github.danthe1st.simple_mail_client.email.data.EmailConnectionInformation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class SimpleMailClient extends Application {
	
	private Stage primaryStage;
	private Scene scene;
	private EmailSender emailSender;
	private EmailReceiver emailReceiver;
	private String ownAddress;
	
	@Override
	public void start(Stage stage) throws IOException {
		primaryStage = stage;
		if(Files.exists(LoginController.EMAIL_CONNECTION_PATH)){
			try{
				EmailConnectionInformation connectionInformation = EmailConnectionInformation.load(LoginController.EMAIL_CONNECTION_PATH);
				connect(connectionInformation);
			}catch(StorageException | EmailException e){
				e.printStackTrace();
				Alert alert = new Alert(AlertType.ERROR, e.getMessage());
				alert.showAndWait();
			}
		}
		if(emailSender == null){
			loadFXMLIntoStage("login", stage);
		}else{
			loadFXMLIntoStage("main", stage);
		}
		
		stage.show();
	}
	
	public void connect(EmailConnectionInformation connectionInfo) throws EmailException {
		ownAddress = connectionInfo.username();
		EmailFactory emailFactory = new EmailFactory(connectionInfo);
		emailReceiver = emailFactory.createReceiver();
		emailSender = emailFactory.createSender();
		Platform.runLater(() -> {
			try{
				changePrimaryStage("main");
			}catch(IOException e){
				e.printStackTrace();
			}
		});
	}
	
	private void changePrimaryStage(String fxml) throws IOException {
		loadFXMLIntoStage(fxml, primaryStage);
	}
	
	public void loadFXMLIntoStage(String fxml, Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(SimpleMailClient.class.getResource(fxml + ".fxml"));
		Parent root = fxmlLoader.load();
		Object controller = fxmlLoader.getController();
		if(controller instanceof AbstractController ctl){
			ctl.init(this, stage);
		}
		stage.setScene(new Scene(root));
	}
	
	public EmailReceiver getEmailReceiver() {
		return emailReceiver;
	}
	
	public EmailSender getEmailSender() {
		return emailSender;
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		if(emailReceiver != null){
			emailReceiver.close();
		}
	}
	
	public static void main(String[] args) {
		launch();
	}
	
	public String getOwnAddress() {
		return ownAddress;
	}
	
	

}