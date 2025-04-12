package io.github.danthe1st.simple_mail_client.ui.webdisplay;

import javafx.scene.Node;

enum WebDisplayOption {
	JAVAFX, SWING;
	
	static final WebDisplayOption SELECTED_OPTION;
	static{
		if("swing".equals(System.getProperty("emailView.framework", "javafx").toLowerCase())){
			SELECTED_OPTION = SWING;
		}else{
			SELECTED_OPTION = JAVAFX;
		}
	}
}

public interface WebDisplay {
	Node getJavaFXNode();
	
	void open(String content, String contentType);
	
	default void updateWidth(double width) {
	}
	
	static WebDisplay getDisplay() {
		return switch(WebDisplayOption.SELECTED_OPTION) {
		case SWING -> new SwingWebDisplay();
		case JAVAFX -> new JavaFXWebDisplay();
		};
	}
}
