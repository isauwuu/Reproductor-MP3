module com.reproductor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires mp3agic;

    opens reproductor to javafx.fxml;
    exports reproductor;
}
