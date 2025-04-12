package io.github.danthe1st.simple_mail_client.ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.danthe1st.simple_mail_client.email.EmailException;
import io.github.danthe1st.simple_mail_client.email.communication.EmailReceiver;
import io.github.danthe1st.simple_mail_client.email.data.Email;
import io.github.danthe1st.simple_mail_client.email.data.Email.EmailContent;
import io.github.danthe1st.simple_mail_client.email.data.EmailFolder;
import io.github.danthe1st.simple_mail_client.ui.webdisplay.WebDisplay;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class EmailListController extends AbstractController {
	
	@FXML
	private TableView<Email> emailView;
	
	@FXML
	private TableColumn<Email, String> fromColumn;
	
	@FXML
	private TableColumn<Email, String> subjectColumn;
	
	@FXML
	private TableColumn<Email, LocalDateTime> timestampColumn;
	
	@FXML
	private TableColumn<Email, String> toColumn;
	
	@FXML
	private VBox folderBox;
	
	@FXML
    private BorderPane root;
	
	@FXML
	private SplitPane emailSplitPane;
	
	@FXML
	private ScrollPane singleEmailWrapper;
	private final WebDisplay emailDisplay = WebDisplay.getDisplay();
	
	private Text currentlySelectedFolder;
	
	private final ExecutorService loaderPool = Executors.newFixedThreadPool(2);
	private EmailFolder currentFolder;// NOSONAR this is used to prevent a folder being set and then set back with different threads
	
	@FXML
	void newEmail(ActionEvent event) throws IOException {
		Stage stage = new Stage();
		getMain().loadFXMLIntoStage("editEmail", stage);
		stage.show();
	}
	
	@Override
	void init() {
		singleEmailWrapper.setContent(emailDisplay.getJavaFXNode());
		fromColumn.setCellValueFactory(createCellValueFactory(Email::from));
		toColumn.setCellValueFactory(createCellValueFactory(mail -> mail.to().stream().collect(Collectors.joining("; "))));
		subjectColumn.setCellValueFactory(createCellValueFactory(Email::subject));
		timestampColumn.setCellValueFactory(createCellValueFactory(Email::timestamp));
		timestampColumn.setSortType(SortType.DESCENDING);
		emailView.getSortOrder().add(timestampColumn);
		
		emailSplitPane.widthProperty().addListener((prop, old, newValue) -> {
			double width = newValue.doubleValue();
			updateEmailViewWidth(width);
		});
		updateEmailViewWidth(emailSplitPane.getWidth());
		
		emailView.getSelectionModel().selectedItemProperty().addListener((observable, old, newEmail) -> {
			if(newEmail == null){
				return;
			}
			try{
				// TODO allow switching between different contents in same E-Mail
				EmailContent content = newEmail.content().getValue().getFirst();
				String contentType = content.contentType().toLowerCase();
				int semicolonIndex = contentType.indexOf(';');
				if(semicolonIndex != -1){
					contentType = contentType.substring(0, semicolonIndex);
				}
				String actualContentType = contentType;
				setSingleEmailContent(content.content(), actualContentType);
			}catch(EmailException e){
				e.printStackTrace();
				setSingleEmailContent("Cannot load content: " + e.getMessage(), "text/plain");
			}
			
		});
		try{
			EmailReceiver receiver = getMain().getEmailReceiver();
			List<EmailFolder> folders = receiver.getFolders();
			for(EmailFolder emailFolder : folders){
				Text folderText = new Text(emailFolder.getName());
				Parent folderElement = new Pane(folderText);
				folderElement.setOnMouseClicked(e -> {
					selectFolder(emailFolder, folderText);
				});
				if("INBOX".equals(emailFolder.getName())){
					selectFolder(emailFolder, folderText);
				}
				folderBox.getChildren().add(folderElement);
			}
		}catch(EmailException e){
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR, e.getMessage());
			alert.showAndWait();
		}
	}

	private void setSingleEmailContent(String content, String actualContentType) {
		emailDisplay.open(content, actualContentType);
	}

	private void updateEmailViewWidth(double width) {
		emailDisplay.updateWidth(width);
	}

	private void selectFolder(EmailFolder emailFolder, Text folderView) {
		if(currentlySelectedFolder != null){
			Font prevFont = currentlySelectedFolder.getFont();
			currentlySelectedFolder.setFont(Font.font(prevFont.getFamily(), FontWeight.MEDIUM, prevFont.getSize()));
		}
		currentlySelectedFolder = folderView;
		setSingleEmailContent("", "text/plain");
		Font prevFont = currentlySelectedFolder.getFont();
		currentlySelectedFolder.setFont(Font.font(prevFont.getFamily(), FontWeight.BOLD, prevFont.getSize()));
		root.setCenter(new Text("Loading..."));
		openFolder(emailFolder);
	}

	private <T> Callback<CellDataFeatures<Email, T>, ObservableValue<T>> createCellValueFactory(Function<Email, T> mapper) {
		return f -> new SimpleObjectProperty<>(mapper.apply(f.getValue()));
	}
	
	private void openFolder(EmailFolder folder) {
		currentFolder = folder;
		loaderPool.execute(() -> {
			try{
				List<Email> emails = folder.getEmails();
				Platform.runLater(() -> {
					if(currentFolder != folder){
						return;
					}
					emailView.getItems().clear();
					for(Email email : emails){
						emailView.getItems().add(email);
					}
					emailView.sort();
					root.setCenter(emailSplitPane);
				});
			}catch(EmailException e){
				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR, e.getMessage());
					alert.showAndWait();
				});
			}
		});
	}
	
}
