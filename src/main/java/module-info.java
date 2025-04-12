module io.github.danthe1st.simple_mail_client {
    requires javafx.controls;
    requires javafx.fxml;
	requires jakarta.mail;
	requires com.fasterxml.jackson.databind;
	requires javafx.web;
	requires java.logging;
	requires javafx.swing;
	requires java.desktop;// javafx-web doesn't work with native-image but javafx-swing works

	opens io.github.danthe1st.simple_mail_client.ui to javafx.fxml;
	
	exports io.github.danthe1st.simple_mail_client.ui;
	exports io.github.danthe1st.simple_mail_client.email.data to com.fasterxml.jackson.databind;
}
