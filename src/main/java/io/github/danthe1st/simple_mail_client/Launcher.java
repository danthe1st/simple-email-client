package io.github.danthe1st.simple_mail_client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.danthe1st.simple_mail_client.ui.SimpleMailClient;
import javafx.application.Application;

public class Launcher {
	public static void main(String[] args) throws URISyntaxException, IOException {
		// need java.library.path=$PWD/target/native
//		System.out.println("load manually");
//		System.load(Path.of("target/native/libjfxwebkit.so").toAbsolutePath().toString());
//		System.out.println("load");
//		System.loadLibrary("jfxwebkit");
//		System.load(Path.of("target/native/libprism_es2.so").toAbsolutePath().toString());
//		System.out.println("start");
//		Class.forName("com.sun.javafx.tk.quantum.QuantumToolkit");
//		System.out.println("quantum loaded");
//		Class.forName("com.sun.prism.shader.Texture_Color_Loader");
		if(System.getProperty("java.home") == null){
			// find native-image location by ozkanpakdil:
			// https://stackoverflow.com/a/77736422/10871900
			Path path = Path.of(
					Launcher.class.getProtectionDomain()
						.getCodeSource()
						.getLocation()
						.toURI()
			).getParent().resolve("fake_home");
			// https://github.com/openjdk/jdk/pull/20169
			Files.createDirectories(path.resolve("lib"));
			Files.createDirectories(path.resolve("conf/fonts"));
			System.setProperty("java.home", path.toString());
		}
		Application.launch(SimpleMailClient.class, args);
	}
}
