package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import modelo.criterios.PorAnio;
import modelo.criterios.PorArtista;
import modelo.criterios.PorNombre;
import modelo.datos.*;
import modelo.estructuras.NodoDoble;
import ui.ThemeManager;
import java.io.File;
import javafx.scene.layout.StackPane;
import services.ReproductorDeAudio;

public class MainController {

    private NodoDoble cancionActual;
    private File ultimaCarpeta;
    private ListaCancion listaCancion;
    private int actualPos;
    private String tiempoTotalStr;

    private ReproductorDeAudio reproductor;

    // --- CONTROLADORES Y GESTORES DE VISTA ---
    @FXML
    private TocadiscosController tocadiscosController;
    @FXML
    private ControlesController controlesController;
    private SvgController motorSvg;

    // --- NODOS FXML ---
    @FXML
    private Button btnAddSong;
    @FXML
    private Button btnRemoveSong;
    @FXML
    private MenuButton btnMenuOrdenamiento;
    @FXML
    private MenuItem btnOrdenarPorAnio;
    @FXML
    private MenuItem btnOrdenarPorArtista;
    @FXML
    private MenuItem btnOrdenarPorNombre;
    @FXML
    private ListView<String> lvListSong;
    @FXML
    private StackPane mainStackPane;

    public MainController() {
        this.ultimaCarpeta = null;
        this.actualPos = -1;
        this.listaCancion = new ListaCancion();
        this.tiempoTotalStr = "0:00";
        this.reproductor = new ReproductorDeAudio();
        this.cancionActual = null;
    }

    public NodoDoble getCancionActual() {
        return this.cancionActual;
    }

    public ListaCancion getListaCancion() {
        return this.listaCancion;
    }

    public int getActualPos() {
        return this.actualPos;
    }

    public void setActualPos(int actualPos) {
        this.actualPos = actualPos;
    }

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
        configurarEventosReproductor();
    }

    private void configurarEventosReproductor() {
        reproductor.setFinalizaCancion(() -> {
            Platform.runLater(() -> nextButtonEvent(null));
        });
        reproductor.tiempoActualProperty().addListener((obs, viejo, nuevo) -> {
            Platform.runLater(() -> {
                Duration total = reproductor.tiempoTotalProperty().get();
                if (total != null && total.toSeconds() > 0) {
                    double porcentaje = (nuevo.toSeconds() / total.toSeconds()) * 100;
                    if (!controlesController.progressSlider.isValueChanging()) {
                        controlesController.progressSlider.setValue(porcentaje);
                    }
                    int segsActuales = (int) nuevo.toSeconds();
                    controlesController.actualizarTiempos(
                            String.format("%d:%02d", segsActuales / 60, segsActuales % 60),
                            tiempoTotalStr
                    );
                }
            });
        });
        reproductor.tiempoTotalProperty().addListener((obs, viejo, nuevo) -> {
            Platform.runLater(() -> {
                int totalSecs = (int) nuevo.toSeconds();
                tiempoTotalStr = String.format("%d:%02d", totalSecs / 60, totalSecs % 60);
                controlesController.actualizarTiempos("0:00", tiempoTotalStr);
            });
        });
        reproductor.estadoProperty().addListener((obs, viejo, estado) -> {
            Platform.runLater(() -> {
                if (estado == MediaPlayer.Status.PLAYING) {
                    if (tocadiscosController != null) tocadiscosController.reproducirAnimacion();
                    if (controlesController != null) controlesController.cambiarTextoBotonPlay("▐▐");
                } else {
                    if (tocadiscosController != null) tocadiscosController.pausarAnimacion();
                    if (controlesController != null) controlesController.cambiarTextoBotonPlay("▶");
                }
            });
        });
        controlesController.progressSlider.setOnMouseReleased(e -> {
            reproductor.adelantar(controlesController.progressSlider.getValue() / 100.0);
        });
    }

    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canciones"); // Cambié el texto a plural
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));
        if (ultimaCarpeta != null) {
            fileChooser.setInitialDirectory(ultimaCarpeta);
        }
        java.util.List<File> archivos = fileChooser.showOpenMultipleDialog(btnAddSong.getScene().getWindow());
        // por si el usuario cerró la ventana o no selecciono nada
        if (archivos != null && !archivos.isEmpty()) {
            // Guardamos la carpeta del primer archivo seleccionado para la próxima vez
            ultimaCarpeta = archivos.get(0).getParentFile();
            // Iteramos sobre todos los archivos que seleccionó el usuario
            for (File archivo : archivos) {
                creaCancion(archivo);
            }
        }
    }

    private void creaCancion(File file) {
        Cancion cancion = new Cancion(file.getAbsolutePath(), listaCancion.tam());
        listaCancion.insertar(cancion, listaCancion.tam());
        lvListSong.getItems().add("♪ " + cancion.getTitulo() + " - " + cancion.getArtista());
        if (listaCancion.tam() == 1)
            actualPos = 0;
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
    private void reproducir(Cancion cancion) {
        if (cancion == null) return;
        reproductor.reproducirNueva(cancion);
    }

    public void playButtonEvent(ActionEvent event) {
        if (cancionActual == null && actualPos != -1) {
            actualizaCancionPorIndice(actualPos);
        } else {
            reproductor.alternarPausaReproduccion();
        }
    }

    public void nextButtonEvent(ActionEvent event) {
        if (cancionActual != null && cancionActual.getNextNodo() != null) {
            this.actualPos++;
            cargarDesdeNodo(cancionActual.getNextNodo(), actualPos);
        } else {
            // Llegamos al final de la lista. Chequeamos si hay Loop.
            if (controlesController.isLoop()) {
                actualizaCancionPorIndice(0); // Volvemos a la primera canción
            } else {
                reproductor.alternarPausaReproduccion();
                controlesController.progressSlider.setValue(0);
            }
        }
    }

    public void previousButtonEvent(ActionEvent event) {

        if (cancionActual != null && cancionActual.getPrevNodo() != null) {
            this.actualPos--;
            cargarDesdeNodo(cancionActual.getPrevNodo(), actualPos);
        } else {
            // Si apretamos Previous en la primera canción, la reiniciamos
            actualizaCancionPorIndice(0);
        }
    }

    private void cargarDesdeNodo(NodoDoble nodo, int posLista) {
        if (nodo == null)
            return;
        this.cancionActual = nodo;
        this.actualPos = posLista;
        Cancion cancionPura = (Cancion) nodo.getNodoInfo();

        if (controlesController != null) {
            controlesController.actualizarTextos(cancionPura.getTitulo(), cancionPura.getArtista());
        }
        actualizarTema(cancionPura);
        reproducir(cancionPura);
        lvListSong.getSelectionModel().select(actualPos);
    }
    private void actualizaCancionPorIndice(int pos) {
        if (pos >= 0 && pos < listaCancion.tam()) {
            NodoDoble nodoEncontrado = listaCancion.obtenerNodo(pos);
            cargarDesdeNodo(nodoEncontrado, pos);
        }
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
}