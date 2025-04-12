package io.github.danthe1st.simple_mail_client.ui.webdisplay;

import java.awt.Dimension;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Node;

class SwingWebDisplay implements WebDisplay {
	
	@FXML
	private SwingNode emailSwingView;
	private JEditorPane swingEmailPane;
	
	private double targetWidth;
	
	public SwingWebDisplay() {
		swingEmailPane = new JEditorPane();
		emailSwingView = new SwingNode();
		SwingUtilities.invokeLater(() -> emailSwingView.setContent(swingEmailPane));
	}
	
	@Override
	public Node getJavaFXNode() {
		return emailSwingView;
	}
	
	@Override
	public void open(String content, String contentType) {
		SwingUtilities.invokeLater(() -> {
			swingEmailPane = new JEditorPane();
			swingEmailPane.setContentType(contentType);
			swingEmailPane.setText(content);
			
			swingEmailPane.setText(content);
			swingEmailPane.setContentType(contentType);
			Dimension sizeFromContent = swingEmailPane.getPreferredSize();
			swingEmailPane.setSize(
					new Dimension(
							(int) Math.max(sizeFromContent.getWidth(), targetWidth),
							(int) sizeFromContent.getHeight()
					)
			);
			swingEmailPane.setMinimumSize(
					new Dimension(
							(int) Math.max(sizeFromContent.getWidth(), targetWidth),
							(int) sizeFromContent.getHeight()
					)
			);
			swingEmailPane.revalidate();
			swingEmailPane.repaint();
			Platform.runLater(() -> {
				emailSwingView.setContent(swingEmailPane);
				emailSwingView.prefHeight(sizeFromContent.getHeight());
				emailSwingView.prefWidth(sizeFromContent.getWidth());
			});
		});
	}
	
	@Override
	public void updateWidth(double width) {
		targetWidth = width - 20;
		SwingUtilities.invokeLater(() -> {
			swingEmailPane.setMinimumSize(new Dimension((int) targetWidth, 100));
			emailSwingView.minWidth(width - 20);
		});
	}
}
