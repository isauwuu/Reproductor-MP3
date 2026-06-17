package reproductor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Clase de entrada principal para el Reproductor MP3.
 * Configura la escena cargando el archivo FXML principal, los estilos CSS, 
 * las fuentes de texto personalizadas y lanza la interfaz de usuario en JavaFX.
 */
public class Main extends Application {

    /**
     * Inicializa y configura la ventana (Stage) principal de la aplicación JavaFX.
     * Carga el archivo FXML, enlaza los archivos CSS requeridos, inicializa las fuentes
     * y muestra la ventana maximizada.
     * 
     * @param stage El escenario (Stage) principal para esta aplicación.
     * @throws Exception Si ocurre un error al cargar el archivo FXML o los recursos.
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/views/main-view.fxml"));

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(
                Main.class.getResource("/styles/style.css").toExternalForm()
        );
        scene.getStylesheets().add(
                Main.class.getResource("/styles/controls.css").toExternalForm()
        );
        scene.getStylesheets().add(
                Main.class.getResource("/styles/player.css").toExternalForm()
        );
        scene.getStylesheets().add(
                Main.class.getResource("/styles/playlist.css").toExternalForm()
        );
        scene.getStylesheets().add(
                Main.class.getResource("/styles/themes/darkTheme.css").toExternalForm()
        );
        scene.getStylesheets().add(
                Main.class.getResource("/styles/themes/lightTheme.css").toExternalForm()
        );

        Font.loadFont(getClass().getResourceAsStream("/fonts/PressStart2P-Regular.ttf"), 10);

        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("Reproductor MP3");
        stage.show();
    }

    /**
     * Punto de entrada de la aplicación Java estándar.
     * Lanza la ejecución del ciclo de vida de la aplicación JavaFX.
     * 
     * @param args Argumentos de la línea de comandos pasados al programa.
     */
    public static void main(String[] args) {
        launch();
    }
}