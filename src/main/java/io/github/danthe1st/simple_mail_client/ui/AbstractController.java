package io.github.danthe1st.simple_mail_client.ui;

import java.util.Objects;

import javafx.stage.Stage;

public abstract class AbstractController {
	private SimpleMailClient main;
	private Stage stage;
	
	protected final SimpleMailClient getMain() {
		return main;
	}
	
	protected Stage getStage() {
		return stage;
	}
	
	final void init(SimpleMailClient main, Stage stage) {
		Objects.requireNonNull(main);
		Objects.requireNonNull(stage);
		if(this.main != null){
			throw new IllegalStateException("initialized multiple times");
		}
		this.main = main;
		this.stage = stage;
		init();
	}
	
	void init() {
	}
}
