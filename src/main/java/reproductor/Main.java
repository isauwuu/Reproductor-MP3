package reproductor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/views/main-view.fxml"));

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(Main.class.getResource("/styles/style.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/styles/playlist.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/styles/player.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/styles/controls.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/styles/themes/darkTheme.css").toExternalForm());
        scene.getStylesheets().add(Main.class.getResource("/styles/themes/lightTheme.css").toExternalForm());

        stage.setTitle("MP3 Player");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
