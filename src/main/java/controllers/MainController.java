package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import modelo.criterios.PorAnio;
import modelo.criterios.PorArtista;
import modelo.criterios.PorNombre;
import modelo.datos.*;
import modelo.estructuras.NodoDoble;
import modelo.interfaces.ReproductorListener;
import services.ShuffleManager;
import services.ReproductorDeAudio;
import ui.ThemeManager;
import java.io.File;
import javafx.scene.layout.StackPane;

public class MainController implements ReproductorListener {

    private NodoDoble cancionActual;
    private File ultimaCarpeta;
    private ListaCancion listaCancion;
    private int actualPos;
    private ListaCancion listaVista; // refleja el orden actual de la vista
    private String tiempoTotalStr;
    private SvgController motorSvg;
    private ReproductorDeAudio reproductor;
    private ShuffleManager shuffleManager;

    @FXML private TocadiscosController tocadiscosController;
    @FXML private ControlesController controlesController;
    @FXML private Button btnRemoveSong;
    @FXML private MenuButton btnMenuOrdenamiento;
    @FXML private MenuItem btnOrdenarPorAnio;
    @FXML private MenuItem btnOrdenarPorArtista;
    @FXML private MenuItem btnOrdenarPorNombre;
    @FXML private ListView<String> lvListSong;
    @FXML private StackPane mainStackPane;
    @FXML private Button btnAddSong;
    @FXML private Button btnAddFolder;

    public MainController() {
        this.listaCancion = new ListaCancion();
        this.reproductor = new ReproductorDeAudio();
        this.shuffleManager = new ShuffleManager();
        this.actualPos = -1;
        this.tiempoTotalStr="0:00";
        this.listaVista = listaCancion;
    }

    @FXML
    public void initialize() {
        if (controlesController != null)
            controlesController.setListener(this);

        WebView bgWebView = new WebView();
        bgWebView.setMouseTransparent(true);
        mainStackPane.getChildren().add(0, bgWebView);
        motorSvg = new SvgController(bgWebView);
        motorSvg.actualizarFondo(ExtractorPaleta.PALETA_BASE, null, false);
        configurarEventosReproductor();
        lvListSong.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                int seleccionada = lvListSong.getSelectionModel().getSelectedIndex();
                if (seleccionada >= 0)
                    actualizaCancionPorIndice(seleccionada);
            }
        });
    }

    private void configurarEventosReproductor() {
        reproductor.setFinalizaCancion(() -> Platform.runLater(this::onNext));

        reproductor.tiempoActualProperty().addListener((obs, viejo, nuevo) ->
                Platform.runLater(() -> {
                    var total = reproductor.tiempoTotalProperty().get();
                    if (total != null && total.toSeconds() > 0) {
                        double porcentaje = (nuevo.toSeconds() / total.toSeconds()) * 100;
                        if (!controlesController.isSliderCambiando())
                            controlesController.setProgreso(porcentaje);
                        int segs = (int) nuevo.toSeconds();
                        controlesController.actualizarTiempos(
                                String.format("%d:%02d", segs / 60, segs % 60), tiempoTotalStr);
                    }
                })
        );

        reproductor.tiempoTotalProperty().addListener((obs, viejo, nuevo) ->
                Platform.runLater(() -> {
                    int totalSecs = (int) nuevo.toSeconds();
                    tiempoTotalStr = String.format("%d:%02d", totalSecs / 60, totalSecs % 60);
                    controlesController.actualizarTiempos("0:00", tiempoTotalStr);
                })
        );

        reproductor.estadoProperty().addListener((obs, viejo, estado) -> {
            Platform.runLater(() -> {
                if (estado == MediaPlayer.Status.PLAYING) {
                    if (tocadiscosController != null) tocadiscosController.reproducirAnimacion();
                    if (controlesController != null) controlesController.cambiarTextoBotonPlay("▐▐");

                    // PRENDER NOTAS MUSICALES
                    if (motorSvg != null) motorSvg.alternarNotasAnimadas(true);

                } else {
                    if (tocadiscosController != null) tocadiscosController.pausarAnimacion();
                    if (controlesController != null) controlesController.cambiarTextoBotonPlay("▶");

                    // APAGAR NOTAS MUSICALES
                    if (motorSvg != null) motorSvg.alternarNotasAnimadas(false);
                }
            });
        });

        controlesController.setOnProgresoSoltado(() ->
                reproductor.adelantar(controlesController.getProgreso() / 100.0));
    }

    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canciones");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));
        if (ultimaCarpeta != null)
            fileChooser.setInitialDirectory(ultimaCarpeta);

        java.util.List<File> archivos = fileChooser.showOpenMultipleDialog(btnAddSong.getScene().getWindow());
        if (archivos != null && !archivos.isEmpty()) {
            ultimaCarpeta = archivos.get(0).getParentFile();
            for (File archivo : archivos) creaCancion(archivo);
        }
    }
    @FXML
    void abrirCarpetaEvent(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta de música");
        if (ultimaCarpeta != null)
            directoryChooser.setInitialDirectory(ultimaCarpeta);

        File carpeta = directoryChooser.showDialog(btnAddSong.getScene().getWindow());
        if (carpeta == null) return;

        ultimaCarpeta = carpeta;
        File[] archivos = carpeta.listFiles();
        if (archivos == null) return;

        for (File archivo : archivos) {
            if (archivo.getName().toLowerCase().endsWith(".mp3"))
                creaCancion(archivo);
        }
    }

    private void creaCancion(File file) {
        Cancion cancion = new Cancion(file.getAbsolutePath(), listaCancion.tam());
        listaCancion.insertar(cancion, listaCancion.tam());
        listaVista = listaCancion;
        btnMenuOrdenamiento.setText("ordenar");
        lvListSong.getItems().add("♪ " + cancion.getTitulo() + " - " + cancion.getArtista());
        if (listaCancion.tam() == 1) actualPos = 0;

        if (controlesController != null && controlesController.isShuffle())
            shuffleManager.generarCola(listaCancion.tam(), actualPos);
    }

    private void actualizaListaView(ListaCancionOrdenada l) {
        if (listaCancion.estaVacia()) return;
        for (int i = 0; i < listaCancion.tam(); i++)
            l.insertar(listaCancion.devolver(i));
        listaVista = new ListaCancion();
        lvListSong.getItems().clear();
        for (int i = 0; i < l.tam(); i++) {
            Cancion cancion = (Cancion) l.devolver(i);
            listaVista.insertar(cancion, i);
            lvListSong.getItems().add("♪ " + cancion.getTitulo() + " - " + cancion.getArtista());
        }
    }

    private void cargarDesdeNodo(NodoDoble nodo, int posLista) {
        if (nodo == null) return;
        this.cancionActual = nodo;
        this.actualPos = posLista;
        Cancion cancion = (Cancion) nodo.getNodoInfo();
        if (controlesController != null)
            controlesController.actualizarTextos(cancion.getTitulo(), cancion.getArtista());

        actualizarTema(cancion);

        reproductor.reproducirNueva(cancion);
        lvListSong.getSelectionModel().select(actualPos);
    }

    private void actualizaCancionPorIndice(int pos) {
        if (pos >= 0 && pos < listaVista.tam())
            cargarDesdeNodo(listaVista.obtenerNodo(pos), pos);
    }

    private void actualizarTema(Cancion cancion) {
        Image portada = cancion.getPortada();
        Paleta paleta = ExtractorPaleta.extraerDe(portada);
        ThemeManager.aplicarPaleta(lvListSong.getScene(), paleta);

        if (tocadiscosController != null)
            tocadiscosController.actualizarColoresDinamicos(paleta.getAcento(), paleta.getBrillante());

        if (motorSvg != null) {
            boolean isPlaying = (reproductor.estadoProperty().get() == MediaPlayer.Status.PLAYING);
            // Ya no le pasamos el BPM, solo isPlaying
            motorSvg.actualizarFondo(paleta, portada, isPlaying);
        }
    }

    private void reordenarVista() {
        Platform.runLater(() -> {
            lvListSong.getItems().clear();
            listaVista = listaCancion;
            for (int i = 0; i < listaCancion.tam(); i++) {
                Cancion c = (Cancion) listaCancion.devolver(i);
                lvListSong.getItems().add("♪ " + c.getTitulo() + " - " + c.getArtista());
            }
            lvListSong.getSelectionModel().select(actualPos);
        });
    }

    @FXML void ordenarPorAnioEvent(ActionEvent event) {
        actualizaListaView(new ListaCancionOrdenada(new PorAnio()));
        btnMenuOrdenamiento.setText("ordenar");
    }

    @FXML void ordenarPorNombreEvent(ActionEvent event) {
        actualizaListaView(new ListaCancionOrdenada(new PorNombre()));
        btnMenuOrdenamiento.setText("ordenar");
    }

    @FXML void ordenarPorArtistaEvent(ActionEvent event) {
        actualizaListaView(new ListaCancionOrdenada(new PorArtista()));
        btnMenuOrdenamiento.setText("ordenar");
    }

    @FXML void removeSongEvent(ActionEvent event) {
        int seleccionada = lvListSong.getSelectionModel().getSelectedIndex();
        if (seleccionada < 0 || listaCancion.estaVacia()) return;
        listaCancion.eliminar(seleccionada);
        if (seleccionada == actualPos) {
            onStopSong();
            cancionActual = null;
            actualPos = -1;
        } else if (seleccionada < actualPos) {
            actualPos--;
        }
        if (controlesController.isShuffle())
            shuffleManager.generarCola(listaCancion.tam(), actualPos);
        reordenarVista();
    }

    @Override
    public void onPlay() {
        if (cancionActual == null && actualPos != -1)
            actualizaCancionPorIndice(actualPos);
        else
            reproductor.alternarPausaReproduccion();
    }

    @Override
    public void onNext() {
        if (listaCancion.estaVacia()) return;

        if (controlesController.isShuffle()) {
            actualizaCancionPorIndice(
                    shuffleManager.siguiente(listaCancion.tam(), actualPos));
        } else if (controlesController.isLoop()) {
            if (cancionActual != null && cancionActual.getNextNodo() != null)
                cargarDesdeNodo(cancionActual.getNextNodo(), ++actualPos);
            else
                actualizaCancionPorIndice(0);
        } else {
            if (cancionActual != null && cancionActual.getNextNodo() != null)
                cargarDesdeNodo(cancionActual.getNextNodo(), ++actualPos);
            else
                onStopSong();
        }
    }

    @Override
    public void onPrevious() {
        if (listaCancion.estaVacia()) return;

        if (controlesController.isShuffle()) {
            actualizaCancionPorIndice(shuffleManager.anterior(actualPos));
        } else if (controlesController.isLoop()) {
            if (cancionActual != null && cancionActual.getPrevNodo() != null)
                cargarDesdeNodo(cancionActual.getPrevNodo(), --actualPos);
            else
                actualizaCancionPorIndice(listaCancion.tam() - 1);
        } else {
            if (cancionActual != null && cancionActual.getPrevNodo() != null)
                cargarDesdeNodo(cancionActual.getPrevNodo(), --actualPos);
            else
                actualizaCancionPorIndice(0);
        }
    }

    @Override
    public void onShuffleToggled(boolean activo) {
        if (activo) shuffleManager.generarCola(listaCancion.tam(), actualPos);
        else shuffleManager.limpiar();
    }

    @Override
    public void onLoopToggled(boolean activo) { }

    @Override
    public void onLoopSongCircle(boolean activo) { }

    @Override
    public void onStopSong() {
        reproductor.detener();
        Platform.runLater(() -> {
            if (controlesController != null) {
                controlesController.cambiarTextoBotonPlay("▶");
                controlesController.resetearProgreso();
                controlesController.actualizarTiempos("0:00", tiempoTotalStr);
            }
            if (tocadiscosController != null)
                tocadiscosController.pausarAnimacion();
        });
    }
}