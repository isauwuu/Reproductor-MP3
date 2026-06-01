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
import modelo.criterios.PorAnio;
import modelo.criterios.PorArtista;
import modelo.criterios.PorNombre;
import modelo.datos.*;
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
    private SvgController motorSvg;

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
        actualPos = -1;
        mediaPlayer = null;
        listaCancion = new ListaCancion();
    }

    public Cancion getCancionActual() { return cancionActual; }
    public ListaCancion getListaCancion() { return listaCancion; }
    public int getActualPos() { return actualPos; }
    public void setActualPos(int actualPos) { this.actualPos = actualPos; }

    @FXML
    public void initialize() {
        if (controlesController != null) {
            controlesController.setMainController(this);
        }
        WebView bgWebView = new WebView();
        bgWebView.setMouseTransparent(true);
        mainStackPane.getChildren().add(0, bgWebView);

        motorSvg = new SvgController(bgWebView);
        motorSvg.actualizarFondo(ExtractorPaleta.PALETA_BASE, null);
    }

    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canción");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));
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
        // Si es la primera canción que agregamos, nos posicionamos en ella
        if (listaCancion.tam() == 1) {
            actualPos = 0;
        }
    }

    private void actualizaListaView(ListaCancionOrdenada l) {
        if (!listaCancion.estaVacia()) {
            for (int i = 0; i < listaCancion.tam(); i++)
                l.insertar(listaCancion.devolver(i));
            lvListSong.getItems().clear();
            for (int i = 0; i < l.tam(); i++) {
                Cancion cancion = (Cancion) l.devolver(i);
                lvListSong.getItems().add("♪ " + cancion.getTitulo() + " - " + cancion.getArtista());
            }
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
                controlesController.actualizarTiempos(
                        String.format("%d:%02d", segs / 60, segs % 60), tiempoTotalStr);
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
                mediaPlayer.seek(Duration.seconds(
                        controlesController.progressSlider.getValue() / 100 * total));
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

    public void nextButtonEvent(ActionEvent event) {
        if (controlesController.isShuffle()) {
            actualizaCancion(controlesController.nextShufflePos());
        } else if (controlesController.isLoop()) {
            reproducir(cancionActual);
        } else {
            actualizaCancion(actualPos + 1);
        }
    }

    public void previousButtonEvent(ActionEvent event) {
        if (controlesController.isShuffle()) {
            actualizaCancion(controlesController.previousShufflePos());
        } else {
            actualizaCancion(actualPos - 1);
        }
    }

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

        ThemeManager.aplicarPaleta(lvListSong.getScene(), paletaActiva);

        if (tocadiscosController != null) {
            tocadiscosController.actualizarColoresDinamicos(
                    paletaActiva.getAcento(), paletaActiva.getBrillante());
        }
        if (motorSvg != null) {
            motorSvg.actualizarFondo(paletaActiva, portada);
        }
    }

    // --- ORDENAMIENTO ---

    @FXML void ordenarPorAnioEvent(ActionEvent event) {
        ListaCancionOrdenada l = new ListaCancionOrdenada(new PorAnio());
        actualizaListaView(l);
    }

    @FXML void ordenarPorNombreEvent(ActionEvent event) {
        ListaCancionOrdenada l = new ListaCancionOrdenada(new PorNombre());
        actualizaListaView(l);
    }

    @FXML void ordenarPorArtistaEvent(ActionEvent event) {
        ListaCancionOrdenada l = new ListaCancionOrdenada(new PorArtista());
        actualizaListaView(l);
    }

    @FXML void removeSongEvent(ActionEvent event) { }
    @FXML void shuffleButtonEvent(ActionEvent event) { }
}