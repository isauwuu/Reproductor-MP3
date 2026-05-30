package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import modelo.datos.Cancion;
import modelo.datos.ExtractorPaleta;
import modelo.datos.ListaCancion;
import modelo.datos.Paleta;
import ui.ThemeManager;

import java.io.File;

public class MainController {
    private Cancion cancionActual;
    private File ultimaCarpeta;
    private ListaCancion listaCancion;
    private int actualPos;
    private MediaPlayer mediaPlayer;

    public MainController() {
        cancionActual = null;
        ultimaCarpeta = null;
        listaCancion = new ListaCancion();
        actualPos = -1;
        mediaPlayer = null;
    }

    @FXML private Button btnAddSong;
    @FXML private Button btnLoop;
    @FXML private Button btnNext;
    @FXML private Button btnPlayPause;
    @FXML private Button btnPrevious;
    @FXML private Button btnRemoveSong;
    @FXML private Button btnShuffle;
    @FXML private Label lblSongArtist;
    @FXML private Label lblSongTitle;
    @FXML private Label lblTime;
    @FXML private Pane leftDecorationPane;
    @FXML private Slider progressSlider;
    @FXML private VBox turntableVisual;
    @FXML private Pane PaneContol;
    @FXML private MenuButton btnMenuOrdenamiento;
    @FXML private MenuItem btnOrdenarPorArtista;
    @FXML private MenuItem btnOrdenarPorNombre;
    @FXML private ListView<String> lvListSong;

    @FXML
    public void initialize() {
    }

    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canción");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3")
        );
        if (ultimaCarpeta != null)
            fileChooser.setInitialDirectory(ultimaCarpeta);
        File archivo = fileChooser.showOpenDialog(btnAddSong.getScene().getWindow());
        if (archivo != null) {
            ultimaCarpeta = archivo.getParentFile();
            creaCancion(archivo);
        }
    }

    private void creaCancion(File file) {
        Cancion cancion = new Cancion(file.getAbsolutePath());
        listaCancion.insertar(cancion, listaCancion.tam());
        lvListSong.getItems().add("♪ " + cancion.getTitulo() + " - " + cancion.getArtista());
        if (listaCancion.tam() == 1) actualPos = 0;
    }

    private void reproducir(Cancion cancion) {
        if (cancion == null) return;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        String uri = new File(cancion.getRutaArchivo()).toURI().toString();
        Media media = new Media(uri);
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                double total = mediaPlayer.getTotalDuration().toSeconds();
                if (total > 0) {
                    progressSlider.setValue((newVal.toSeconds() / total) * 100);
                }
                int segs = (int) newVal.toSeconds();
                lblTime.setText(String.format("%d:%02d", segs / 60, segs % 60));
            });
        });

        progressSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                double total = mediaPlayer.getTotalDuration().toSeconds();
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue() / 100 * total));
            }
        });
        // Cuando la canción termine, simulamos un clic en el botón "Siguiente"
        mediaPlayer.setOnEndOfMedia(() -> {
            nextButtonEvent(null);
        });
        mediaPlayer.play();
    }

    @FXML
    void loopButtonEvent(ActionEvent event) {
    }

    @FXML
    void nextButtonEvent(ActionEvent event) {
        actualizaCancion(actualPos + 1);
    }

    @FXML
    void playButtonEvent(ActionEvent event) {
        // Si el reproductor ya existe
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause(); // Pausamos
            } else {
                mediaPlayer.play();  // Reanudamos desde donde se quedó
            }
        } else {
            // Si el reproductor es null (todavía no le dimos play por primera vez)
            if (actualPos != -1) {
                actualizaCancion(actualPos);
            }
        }
    }

    @FXML
    void previousButtonEvent(ActionEvent event) {
        actualizaCancion(actualPos - 1);
    }

    private void actualizaCancion(int pos) {
        if (pos >= 0 && pos < listaCancion.tam()) {
            this.actualPos = pos;
            this.cancionActual = (Cancion) listaCancion.devolver(pos);
            cargarCancion(cancionActual);
            reproducir(cancionActual);
        }
    }

    @FXML void removeSongEvent(ActionEvent event) { }
    @FXML void shuffleButtonEvent(ActionEvent event) { }
    @FXML void ordenarPorAnioEvent(ActionEvent event) { }
    @FXML void ordenarPorArtistaEvent(ActionEvent event) { }
    @FXML void ordenarPorNombreEvent(ActionEvent event) { }

    private void cargarCancion(Cancion cancion) {
        this.cancionActual = cancion;
        lblSongTitle.setText(cancion.getTitulo());
        lblSongArtist.setText(cancion.getArtista());
        actualizarTema(cancion);
    }

    private void actualizarTema(Cancion cancion) {
        Image portada = cancion.getPortada();
        Paleta paleta = ExtractorPaleta.extraerDe(portada);
        ThemeManager.aplicarPaleta(lblSongTitle.getScene(), paleta);
    }
}