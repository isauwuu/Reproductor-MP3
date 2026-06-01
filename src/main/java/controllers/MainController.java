package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import modelo.datos.Cancion;
import modelo.datos.ExtractorPaleta;
import modelo.datos.ListaCancion;
import modelo.datos.Paleta;
import ui.ThemeManager;

import java.io.File;
import javafx.scene.layout.StackPane;

public class MainController {

    // --- LÓGICA DE NEGOCIO Y ESTRUCTURAS ---
    private Cancion cancionActual;
    private File ultimaCarpeta;
    private ListaCancion listaCancion;
    private int actualPos;
    private MediaPlayer mediaPlayer;
    private String tiempoTotalStr = "0:00";

    // --- CONTROLADORES Y GESTORES DE VISTA ---
    @FXML private TocadiscosController tocadiscosController;
    @FXML private ControlesController controlesController;
    private SvgController motorSvg; // Nuestro nuevo motor delegado

    // --- NODOS FXML ---
    @FXML private Button btnAddSong;
    @FXML private Button btnRemoveSong;
    @FXML private MenuButton btnMenuOrdenamiento;
    @FXML private MenuItem btnOrdenarPorAnio;
    @FXML private MenuItem btnOrdenarPorArtista;
    @FXML private MenuItem btnOrdenarPorNombre;
    @FXML private ListView<String> lvListSong;
    @FXML private StackPane mainStackPane;

    public MainController() {
        cancionActual = null;
        ultimaCarpeta = null;
        listaCancion = new ListaCancion();
        actualPos = -1;
        mediaPlayer = null;
    }

    @FXML
    public void initialize() {
        if (controlesController != null) {
            controlesController.setMainController(this);
        }

        // 1. Configurar lienzo web
        WebView bgWebView = new WebView();
        bgWebView.setMouseTransparent(true);
        mainStackPane.getChildren().add(0, bgWebView);

        // 2. Inicializar el motor SVG y cargar estado en frío
        motorSvg = new SvgController(bgWebView);
        motorSvg.actualizarFondo(ExtractorPaleta.PALETA_BASE, null);
    }

    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canción");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));

        if (ultimaCarpeta != null) fileChooser.setInitialDirectory(ultimaCarpeta);

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

        if (listaCancion.tam() == 1) {
            actualPos = 0;
        }
    }

    private void reproducir(Cancion cancion) {
        if (cancion == null) return;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Media media = new Media(new File(cancion.getRutaArchivo()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.currentTimeProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                double total = mediaPlayer.getTotalDuration().toSeconds();
                if (total > 0) {
                    controlesController.progressSlider.setValue((newVal.toSeconds() / total) * 100);
                }
                int segs = (int) newVal.toSeconds();
                String tiempoActual = String.format("%d:%02d", segs / 60, segs % 60);
                controlesController.actualizarTiempos(tiempoActual, tiempoTotalStr);
            });
        });

        mediaPlayer.setOnReady(() -> {
            int total = (int) mediaPlayer.getTotalDuration().toSeconds();
            tiempoTotalStr = String.format("%d:%02d", total / 60, total % 60);
            Platform.runLater(() -> controlesController.actualizarTiempos("0:00", tiempoTotalStr));
        });

        controlesController.progressSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                double total = mediaPlayer.getTotalDuration().toSeconds();
                mediaPlayer.seek(Duration.seconds(controlesController.progressSlider.getValue() / 100 * total));
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> nextButtonEvent(null));
        mediaPlayer.play();

        if (tocadiscosController != null) tocadiscosController.reproducirAnimacion();
        if (controlesController != null) controlesController.cambiarTextoBotonPlay("▐▐");
    }

    public void playButtonEvent(ActionEvent event) {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            tocadiscosController.pausarAnimacion();
            controlesController.cambiarTextoBotonPlay("▶");
        } else {
            if (cancionActual == null && actualPos != -1) {
                actualizaCancion(actualPos);
            } else if (cancionActual != null) {
                if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                    mediaPlayer.play();
                    tocadiscosController.reproducirAnimacion();
                    controlesController.cambiarTextoBotonPlay("▐▐");
                } else {
                    reproducir(cancionActual);
                }
            }
        }
    }

    public void nextButtonEvent(ActionEvent event) { actualizaCancion(actualPos + 1); }
    public void previousButtonEvent(ActionEvent event) { actualizaCancion(actualPos - 1); }

    private void actualizaCancion(int pos) {
        if (pos >= 0 && pos < listaCancion.tam()) {
            this.actualPos = pos;
            this.cancionActual = (Cancion) listaCancion.devolver(pos);
            cargarCancion(cancionActual);
            reproducir(cancionActual);
        } else if (pos >= listaCancion.tam()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                tocadiscosController.pausarAnimacion();
                controlesController.cambiarTextoBotonPlay("▶");
                controlesController.progressSlider.setValue(0);
            }
        }
    }

    private void cargarCancion(Cancion cancion) {
        this.cancionActual = cancion;
        if (controlesController != null) {
            controlesController.actualizarTextos(cancion.getTitulo(), cancion.getArtista());
        }
        actualizarTema(cancion);
    }

    private void actualizarTema(Cancion cancion) {
        Image portada = cancion.getPortada();
        Paleta paletaActiva = ExtractorPaleta.extraerDe(portada);

        // 1. Delegar a gestor nativo de UI
        ThemeManager.aplicarPaleta(lvListSong.getScene(), paletaActiva);

        // 2. Delegar a controlador de tocadiscos
        if (tocadiscosController != null) {
            tocadiscosController.actualizarColoresDinamicos(paletaActiva.getAcento(), paletaActiva.getBrillante());
        }

        // 3. Delegar a motor web/SVG
        if (motorSvg != null) {
            motorSvg.actualizarFondo(paletaActiva, portada);
        }
    }

    // --- METODOS VACIOS ---
    @FXML void removeSongEvent(ActionEvent event) { }
    @FXML void shuffleButtonEvent(ActionEvent event) { }
    @FXML void ordenarPorAnioEvent(ActionEvent event) { }
    @FXML void ordenarPorArtistaEvent(ActionEvent event) { }
    @FXML void ordenarPorNombreEvent(ActionEvent event) { }
}