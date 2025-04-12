package io.github.danthe1st.simple_mail_client.ui;

import java.time.LocalDateTime;
import java.util.List;

import io.github.danthe1st.simple_mail_client.email.EmailException;
import io.github.danthe1st.simple_mail_client.email.data.Email;
import io.github.danthe1st.simple_mail_client.email.data.Email.Lazy;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SendEmailController extends AbstractController {

    @FXML
    private TextArea contentField;

    @FXML
	private TextField subjectField;

    @FXML
	private TextField toField;

    @FXML
	void send(ActionEvent event) {
		new Thread(() -> {
			try{
				getMain().getEmailSender().sendMail(
						new Email(
								getMain().getOwnAddress(),
								List.of(toField.getText()),
								subjectField.getText(),
								new Lazy<>(() -> List.of(new Email.EmailContent(contentField.getText(), "text/plain"))),
								LocalDateTime.now()
						)
				);
				Platform.runLater(() -> getStage().close());
			}catch(EmailException e){
				e.printStackTrace();
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR, e.getMessage());
					alert.showAndWait();
				});
			}
		}).start();
	}

}
