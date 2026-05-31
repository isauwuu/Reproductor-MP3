module com.reproductor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires mp3agic;
    requires color.thief;
    requires javafx.swing;
    requires javafx.web;

    opens controllers to javafx.fxml;
    opens modelo.datos to javafx.base;

    opens reproductor to javafx.fxml;
    exports reproductor;
}
