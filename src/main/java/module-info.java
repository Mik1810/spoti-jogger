module sproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
	requires se.michaelthelin.spotify;
	requires org.apache.httpcomponents.core5.httpcore5;
	requires java.desktop;
	requires nv.i18n;
    opens sproject.tinify to javafx.fxml;
    exports sproject.tinify;
}