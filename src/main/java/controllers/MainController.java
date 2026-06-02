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
import modelo.interfaces.ReproductorListener;
import ui.ShuffleManager;
import ui.ThemeManager;
import java.io.File;
import javafx.scene.layout.StackPane;

public class MainController implements ReproductorListener {
    private Cancion cancionActual;
    private File ultimaCarpeta;
    private ListaCancion listaCancion;
    private int actualPos;
    private MediaPlayer mediaPlayer;
    private String tiempoTotalStr = "0:00";
    private SvgController motorSvg;
    private ShuffleManager shuffleManager = new ShuffleManager();

    @FXML private TocadiscosController tocadiscosController;
    @FXML private ControlesController controlesController;
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
            controlesController.setListener(this);
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3","*.mp3"));
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
        if (listaCancion.tam() == 1) { actualPos = 0; }
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
                mediaPlayer.seek(Duration.seconds(controlesController.progressSlider.getValue() / 100 * total));
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> onNext());
        mediaPlayer.play();
        if (tocadiscosController != null) tocadiscosController.reproducirAnimacion();
        if (controlesController != null) controlesController.cambiarTextoBotonPlay("▐▐");
    }

    private void actualizaCancion(int pos) {
        if (pos >= 0 && pos < listaCancion.tam()) {
            this.actualPos = pos;
            this.cancionActual = (Cancion) listaCancion.devolver(pos);
            cargarCancion(cancionActual);
            reproducir(cancionActual);
            lvListSong.getSelectionModel().select(actualPos);
        } else if (pos >= listaCancion.tam()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                tocadiscosController.pausarAnimacion();
                controlesController.cambiarTextoBotonPlay("▶");
                controlesController.progressSlider.setValue(0);
            }
        }
    }

    public void actualizaCancionLoop(int pos){
        if(pos>=0 && pos<listaCancion.tam()) actualPos=pos;
        else if(pos>=listaCancion.tam()) actualPos = 0;
        else actualPos = listaCancion.tam()-1;

        this.cancionActual = (Cancion) listaCancion.devolver(actualPos);
        cargarCancion(cancionActual);
        reproducir(cancionActual);
        lvListSong.getSelectionModel().select(actualPos);
    }

    private void cargarCancion(Cancion cancion) {
        this.cancionActual = cancion;
        if (controlesController != null)
            controlesController.actualizarTextos(cancion.getTitulo(), cancion.getArtista());
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

    public void reordenarVista(boolean shuffleActivo) {
        Platform.runLater(() -> {
            lvListSong.getItems().clear();
            if (shuffleActivo) {
                ListaIndices cola = shuffleManager.getCola(); // ya no toca ControlesController
                for (int i = 0; i < cola.tam(); i++) {
                    int idx = (Integer) cola.devolver(i);
                    Cancion c = (Cancion) listaCancion.devolver(idx);
                    lvListSong.getItems().add("♪ " + c.getTitulo() + " - " + c.getArtista());
                }
            } else {
                for (int i = 0; i < listaCancion.tam(); i++) {
                    Cancion c = (Cancion) listaCancion.devolver(i);
                    lvListSong.getItems().add("♪ " + c.getTitulo() + " - " + c.getArtista());
                }
            }
        });
    }

    @FXML void ordenarPorAnioEvent(ActionEvent event) {
        ListaCancionOrdenada l = new ListaCancionOrdenada(new PorAnio());
        actualizaListaView(l);
        btnMenuOrdenamiento.setText("año ↑");
    }

    @FXML void ordenarPorNombreEvent(ActionEvent event) {
        ListaCancionOrdenada l = new ListaCancionOrdenada(new PorNombre());
        actualizaListaView(l);
        btnMenuOrdenamiento.setText("nombre ↑");
    }

    @FXML void ordenarPorArtistaEvent(ActionEvent event) {
        ListaCancionOrdenada l = new ListaCancionOrdenada(new PorArtista());
        actualizaListaView(l);
        btnMenuOrdenamiento.setText("artista ↑");
    }

    @FXML void removeSongEvent(ActionEvent event) { }

    @Override
    public void onPlay() {
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

    @Override
    public void onNext() {
        if (controlesController.isShuffle())
            actualizaCancion(shuffleManager.siguiente(listaCancion.tam(), actualPos));
        else if (controlesController.isLoop())
            reproducir(cancionActual);
        else
            actualizaCancion(actualPos + 1);
    }

    @Override
    public void onPrevious() {
        if (controlesController.isShuffle())
            actualizaCancion(shuffleManager.anterior(actualPos));
        else
            actualizaCancion(actualPos - 1);
    }

    @Override
    public void onShuffleToggled(boolean activo) {
        if (activo) {
            shuffleManager.generarCola(listaCancion.tam(), actualPos);
        } else {
            shuffleManager.limpiar();
        }
        reordenarVista(activo);
    }

    @Override
    public void onLoopToggled(boolean activo) {
        shuffleManager.limpiar(); // si activa loop, el shuffle se cancela
    }
}