# Simple Email Client

The name sais it all. This is a simple E-Mail client written with JavaFX.

![Login](img/login.png)  
![View E-mails](img/showmail.png)  
![View E-mails](img/sendmail.png)

### Download

Executables are available as build artifacts [using GitHub Actions](https://github.com/danthe1st/simple-email-client/actions). These builds include a minimal JRE and do not need any additional Java installation to run.  
To use them, just download the zip file for your operating system and run the `simple-email-client` executable in the `bin` directory.

The builds include executables built with [native-image](https://www.graalvm.org/latest/reference-manual/native-image/) for Linux.
These binaries do not include a JRE. These native executables are using Swing to display E-Mails due to javafx-web not supporting native-image and can therefore only display simple E-Mails.

### Building from source

To run this project, first install [JDK 21 or later](https://adoptium.net/download/) and a recent [Maven](https://maven.apache.org/download.cgi) version.  
Then, run the project using `mvn compile javafx:run`.
