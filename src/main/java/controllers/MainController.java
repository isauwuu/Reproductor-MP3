package controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import modelo.criterios.PorAnio;
import modelo.criterios.PorArtista;
import modelo.criterios.PorNombre;
import modelo.datos.*;
import modelo.estructuras.NodoDoble;
import modelo.interfaces.ReproductorListener;
import ui.ShuffleManager;
import ui.ThemeManager;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.scene.layout.StackPane;
import services.ReproductorDeAudio;

public class MainController implements ReproductorListener {

    private NodoDoble cancionActual;
    private File ultimaCarpeta;
    private ListaCancion listaCancion;
    private int actualPos;
    private String tiempoTotalStr = "0:00";
    private SvgController motorSvg;
    private ShuffleManager shuffleManager = new ShuffleManager();
    private ReproductorDeAudio reproductor;
    private boolean loopSong;

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
        this.ultimaCarpeta = null;
        this.actualPos = -1;
        this.listaCancion = new ListaCancion();
        this.tiempoTotalStr = "0:00";
        this.reproductor = new ReproductorDeAudio();
        this.cancionActual = null;
        this.loopSong = false;
    }

    public NodoDoble getCancionActual() { return cancionActual; }
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
        configurarEventosReproductor();
    }

    private void configurarEventosReproductor() {
        reproductor.setFinalizaCancion(() -> Platform.runLater(this::onNext));

        reproductor.tiempoActualProperty().addListener((obs, viejo, nuevo) -> {
            Platform.runLater(() -> {
                var total = reproductor.tiempoTotalProperty().get();
                if (total != null && total.toSeconds() > 0) {
                    double porcentaje = (nuevo.toSeconds() / total.toSeconds()) * 100;
                    if (!controlesController.progressSlider.isValueChanging())
                        controlesController.progressSlider.setValue(porcentaje);
                    int segs = (int) nuevo.toSeconds();
                    controlesController.actualizarTiempos(
                            String.format("%d:%02d", segs / 60, segs % 60), tiempoTotalStr);
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

        controlesController.progressSlider.setOnMouseReleased(e ->
                reproductor.adelantar(controlesController.progressSlider.getValue() / 100.0));
    }

    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canciones");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));
        if (ultimaCarpeta != null) fileChooser.setInitialDirectory(ultimaCarpeta);
        java.util.List<File> archivos = fileChooser.showOpenMultipleDialog(btnAddSong.getScene().getWindow());
        if (archivos != null && !archivos.isEmpty()) {
            ultimaCarpeta = archivos.get(0).getParentFile();
            for (File archivo : archivos) creaCancion(archivo);
        }
    }

    private void creaCancion(File file) {
        Cancion cancion = new Cancion(file.getAbsolutePath(), listaCancion.tam());
        listaCancion.insertar(cancion, listaCancion.tam());
        lvListSong.getItems().add("♪ " + cancion.getTitulo() + " - " + cancion.getArtista());
        if (listaCancion.tam() == 1) actualPos = 0;
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
        reproductor.reproducirNueva(cancion);
    }

    private void cargarDesdeNodo(NodoDoble nodo, int posLista) {
        if (nodo == null) return;
        this.cancionActual = nodo;
        this.actualPos = posLista;
        Cancion cancionPura = (Cancion) nodo.getNodoInfo();
        if (controlesController != null)
            controlesController.actualizarTextos(cancionPura.getTitulo(), cancionPura.getArtista());
        actualizarTema(cancionPura);
        reproducir(cancionPura);
        lvListSong.getSelectionModel().select(actualPos);
    }

    private void actualizaCancionPorIndice(int pos) {
        if (pos >= 0 && pos < listaCancion.tam()) {
            NodoDoble nodo = listaCancion.obtenerNodo(pos);
            cargarDesdeNodo(nodo, pos);
        }
    }

    private void actualizarTema(Cancion cancion) {
        Image portada = cancion.getPortada();
        Paleta paletaActiva = ExtractorPaleta.extraerDe(portada);
        ThemeManager.aplicarPaleta(lvListSong.getScene(), paletaActiva);
        if (tocadiscosController != null)
            tocadiscosController.actualizarColoresDinamicos(paletaActiva.getAcento(), paletaActiva.getBrillante());
        if (motorSvg != null)
            motorSvg.actualizarFondo(paletaActiva, portada);
    }

    public void reordenarVista(boolean shuffleActivo) {
        Platform.runLater(() -> {
            lvListSong.getItems().clear();
            if (shuffleActivo) {
                ListaIndices cola = shuffleManager.getCola();
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

    @FXML void removeSongEvent(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Eliminar canciones");

        ListView<String> lista = new ListView<>();
        lista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (int i = 0; i < listaCancion.tam(); i++)
            lista.getItems().add(lvListSong.getItems().get(i));

        dialog.getDialogPane().setContent(lista);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            ObservableList<Integer> seleccionados = lista.getSelectionModel().getSelectedIndices();     //preguntar si se pueda

            ListaIndices indices = new ListaIndices();
            for (int i = 0; i < seleccionados.size(); i++)
                indices.insertar(seleccionados.get(i), i);

            for (int i = indices.tam() - 1; i >= 0; i--) {
                int idx = (Integer) indices.devolver(i);
                listaCancion.eliminar(idx);
                lvListSong.getItems().remove(idx);
            }
        }
    }

    @Override
    public void onPlay() {
        if (cancionActual == null && actualPos != -1) {
            actualizaCancionPorIndice(actualPos);
        } else {
            reproductor.alternarPausaReproduccion();
        }
    }

    @Override
    public void onNext() {
        if(cancionActual != null){                              //futuro try catch
            if (controlesController.isShuffle())
                actualizaCancionPorIndice(shuffleManager.siguiente(listaCancion.tam(), actualPos));
            else if (controlesController.isLoopSong()) {
                actualPos = actualPos+1 == listaCancion.tam() ? 0 : actualPos+1;
                actualizaCancionPorIndice(actualPos);
            }else if(controlesController.isLoop())
                actualizaCancionPorIndice(0);
            else {
                if (cancionActual.getNextNodo() != null) {
                    actualPos++;
                    cargarDesdeNodo(cancionActual.getNextNodo(), actualPos);
                } else {
                    reproductor.alternarPausaReproduccion();
                    controlesController.progressSlider.setValue(0);
                }
            }
        }
    }

    @Override
    public void onPrevious() {
        if(cancionActual!=null){
            if (controlesController.isShuffle()) {
                actualizaCancionPorIndice(shuffleManager.anterior(actualPos));
            }
            else if (controlesController.isLoopSong()) {
                actualPos = actualPos-1 == -1 ? listaCancion.tam()-1 : actualPos-1;
                actualizaCancionPorIndice(actualPos);
            }else if(controlesController.isLoop())
                actualizaCancionPorIndice(0);
            else {
                if (cancionActual.getPrevNodo() != null) {
                    --actualPos;
                    cargarDesdeNodo(cancionActual.getPrevNodo(), actualPos);
                } else {
                    reproductor.alternarPausaReproduccion();
                    controlesController.progressSlider.setValue(0);
                }
            }
        }
    }

    @Override
    public void onShuffleToggled(boolean activo) {
        if (activo) shuffleManager.generarCola(listaCancion.tam(), actualPos);
        else shuffleManager.limpiar();
        reordenarVista(activo);
    }

    @Override
    public void onLoopToggled(boolean activo) {
        shuffleManager.limpiar();
    }
}