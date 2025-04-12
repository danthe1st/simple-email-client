package io.github.danthe1st.simple_mail_client.ui.webdisplay;

import javafx.scene.Node;
import javafx.scene.web.WebView;

class JavaFXWebDisplay implements WebDisplay {
	
	private WebView webView = new WebView();
	
	@Override
	public Node getJavaFXNode() {
		return webView;
	}
	
	@Override
	public void open(String content, String contentType) {
		webView.getEngine().loadContent(content, contentType);
	}
	
}
