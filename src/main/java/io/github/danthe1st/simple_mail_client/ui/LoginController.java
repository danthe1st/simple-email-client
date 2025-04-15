package io.github.danthe1st.simple_mail_client.ui;

import java.nio.file.Path;

import io.github.danthe1st.simple_mail_client.email.EmailException;
import io.github.danthe1st.simple_mail_client.email.StorageException;
import io.github.danthe1st.simple_mail_client.email.data.EmailConnectionInformation;
import io.github.danthe1st.simple_mail_client.email.data.EmailConnectionInformation.ServerInformation;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

public class LoginController extends AbstractController {

	public static final Path EMAIL_CONNECTION_PATH = Path.of("connection.json");
	
	@FXML
	private TextField emailField;
	
	@FXML
	private TextField incomingAddressField;
	
	@FXML
	private Spinner<Integer> incomingPortField;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private TextField smtpAddressField;
	
	@FXML
	private Spinner<Integer> smtpPortField;
	
	@FXML
	private CheckBox startTLSCheckbox;
	
	@FXML
	private CheckBox imapsCheckbox;
	
	@Override
	void init() {
		incomingPortField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 49151, 995));
		smtpPortField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 49151, 587));
	}
	
    @FXML
    void connect(ActionEvent event) {
		EmailConnectionInformation connectionInfo = new EmailConnectionInformation(
				new ServerInformation(incomingAddressField.getText(), incomingPortField.getValue(), imapsCheckbox.isSelected()),
				new ServerInformation(smtpAddressField.getText(), smtpPortField.getValue(), startTLSCheckbox.isSelected()),
				emailField.getText(),
				emailField.getText(), passwordField.getText()
		);
		new Thread(() -> {
			try{
				connectionInfo.save(EMAIL_CONNECTION_PATH);
				getMain().connect(connectionInfo);
			}catch(StorageException | EmailException e){
				e.printStackTrace();
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR, e.getMessage());
					alert.showAndWait();
				});
			}
		}).start();
    }
}
