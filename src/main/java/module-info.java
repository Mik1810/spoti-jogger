module sproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
	requires se.michaelthelin.spotify;
	requires org.apache.httpcomponents.core5.httpcore5;
	requires java.desktop;
	requires org.seleniumhq.selenium.api;
	requires org.seleniumhq.selenium.chrome_driver;
	requires nv.i18n;
    opens sproject.tinify to javafx.fxml;
    exports sproject.tinify;
}