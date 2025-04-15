# Simple Email Client

The name sais it all. This is a simple E-Mail client written with JavaFX.

![Login](img/login.png)  
![View E-mails](img/simplemail.png)  
![View E-mails](img/showmail.png)  
![View E-mails](img/sendmail.png)

To reload the E-Mails in a folder, click the name of the folder on the left.

### Download

Executables are available as build artifacts [using GitHub Actions](https://github.com/danthe1st/simple-email-client/actions). These builds include a minimal JRE and do not need any additional Java installation to run.  
To use them, just download the zip file for your operating system and run the `simple-email-client` executable in the `bin` directory.

The builds include executables built with [native-image](https://www.graalvm.org/latest/reference-manual/native-image/) for Linux.
These binaries do not include a JRE. These native executables are using Swing to display E-Mails due to javafx-web not supporting native-image and can therefore only display simple E-Mails.

### Building from source

To run this project, first install [JDK 21 or later](https://adoptium.net/download/) and a recent [Maven](https://maven.apache.org/download.cgi) version.  
Then, run the project using `mvn compile javafx:run`.

### Setting up an E-Mail server for testing

[Greenmail](https://greenmail-mail-test.github.io/greenmail/) can be used to set up an E-Mail server for testing.

To run Greenmail via Docker, execute the following command (assuming Docker is installed):
```bash
docker run -it --rm -p 3025:3025 -p 3110:3110 -p 3143:3143 -p 3465:3465 -p 3993:3993 -p 3995:3995 -p 8080:8080 greenmail/standalone:2.1.3
```

With that, you can log in with both server addresses set to `localhost`, the SMTP port being `3025` (no STARTLS), the IMAP port being 3143 (not secured) and an arbitrary E-Mail address as a username. No password is necessary for that.
This corresponds to the following `connection.json:
```json
{
	"incoming":{
		"serverAddress":"localhost",
		"port":3143,
		"secure":false
	},
	"outgoing":{
		"serverAddress":"localhost",
		"port":3025,
		"secure":false
	},
	"senderAddress":"test@test.local",
	"username":"test@test.local",
	"password":""
}
```

The Greenmail server can also be accessed via a web UI at `localhost:8080`.