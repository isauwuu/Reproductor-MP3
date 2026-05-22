module com.reproductor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens reproductor to javafx.fxml;

    exports reproductor;
}
