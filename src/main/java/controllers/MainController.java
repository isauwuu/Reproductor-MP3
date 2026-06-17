package controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import modelo.datos.*;
import modelo.estructuras.NodoDoble;
import modelo.interfaces.ReproductorListener;
import services.ShuffleManager;
import services.ReproductorDeAudio;
import ui.ThemeManager;
import javafx.scene.layout.StackPane;

/**
 * Controlador principal de la aplicación.
 * Orquesta la reproducción de audio, las animaciones del tocadiscos,
 * la actualización de fondos SVG y delega la administración de la lista
 * de reproducción a {@link PlaylistController}.
 */
public class MainController implements ReproductorListener {

    private NodoDoble cancionActual;
    private int actualPos;
    private String tiempoTotalStr;
    private SvgController motorSvg;
    private final ReproductorDeAudio reproductor;
    private final ShuffleManager shuffleManager;
    boolean playingSongDeleted;

    @FXML private TocadiscosController tocadiscosController;
    @FXML private ControlesController controlesController;
    @FXML private PlaylistController playlistController; // FXML injected nested controller

    @FXML private StackPane mainStackPane;
    @FXML private StackPane tocadiscos;

    /**
     * Constructor del controlador principal.
     * Inicializa los gestores y servicios de reproducción.
     */
    public MainController() {
        this.reproductor = new ReproductorDeAudio();
        this.shuffleManager = new ShuffleManager();
        this.actualPos = -1;
        this.tiempoTotalStr = "0:00";
        this.playingSongDeleted = false;
    }

    /**
     * Inicializa el controlador vinculando los subcontroladores e iniciando las animaciones/redimensionamiento.
     */
    @FXML
    public void initialize() {
        if (controlesController != null) {
            controlesController.setListener(this);
        }
        if (playlistController != null) {
            playlistController.setMainController(this);
            playlistController.initCellFactory();
        }
        configuraFondo();
        configurarEventosReproductor();
        tocadiscoResponsive();
    }

    /**
     * Configura los listeners de redimensionamiento para hacer que el tocadiscos sea responsivo.
     */
    private void tocadiscoResponsive() {
        mainStackPane.widthProperty().addListener((obs, oldVal, newVal) -> reposicionarTocadiscos());
        mainStackPane.heightProperty().addListener((obs, oldVal, newVal) -> reposicionarTocadiscos());
        Platform.runLater(this::reposicionarTocadiscos);
    }

    /**
     * Inicializa y configura el fondo animado con el motor SVG.
     */
    private void configuraFondo() {
        WebView bgWebView = new WebView();
        bgWebView.setMouseTransparent(true);
        mainStackPane.getChildren().add(0, bgWebView);
        motorSvg = new SvgController(bgWebView);
        motorSvg.actualizarFondo(ExtractorPaleta.PALETA_BASE, null, false);
    }

    /**
     * Configura el listener del slider de progreso para el reproductor de audio.
     */
    private void configuraEventoSlider(ReproductorDeAudio reproductor) {
        reproductor.tiempoActualProperty().addListener((obs, viejo, nuevo) ->
                Platform.runLater(() -> {
                    var total = reproductor.tiempoTotalProperty().get();
                    if (total != null && total.toSeconds() > 0) {
                        double porcentaje = (nuevo.toSeconds() / total.toSeconds()) * 100;
                        if (!controlesController.isSliderCambiando()) {
                            controlesController.setProgreso(porcentaje);
                        }
                        int segs = (int) nuevo.toSeconds();
                        controlesController.actualizarTiempos(
                                String.format("%d:%02d", segs / 60, segs % 60), tiempoTotalStr);
                    }
                })
        );
    }

    /**
     * Configura el listener para obtener la duración total de la canción y actualizar las etiquetas.
     */
    private void configuraTiempoCancion(ReproductorDeAudio reproductor) {
        reproductor.tiempoTotalProperty().addListener((obs, viejo, nuevo) ->
                Platform.runLater(() -> {
                    int totalSecs = (int) nuevo.toSeconds();
                    tiempoTotalStr = String.format("%d:%02d", totalSecs / 60, totalSecs % 60);
                    controlesController.actualizarTiempos("0:00", tiempoTotalStr);
                })
        );
    }

    /**
     * Configura las animaciones del tocadiscos y el fondo en base al estado del reproductor.
     */
    private void configuraTocadiscoAnimaciones(ReproductorDeAudio reproductor) {
        reproductor.estadoProperty().addListener((obs, viejo, estado) -> {
            Platform.runLater(() -> {
                if (estado == MediaPlayer.Status.PLAYING) {
                    if (tocadiscosController != null) tocadiscosController.reproducirAnimacion();
                    if (controlesController != null) controlesController.cambiarTextoBotonPlay("||");
                    if (motorSvg != null) motorSvg.alternarNotasAnimadas(true);
                } else {
                    if (tocadiscosController != null) tocadiscosController.pausarAnimacion();
                    if (controlesController != null) controlesController.cambiarTextoBotonPlay("▶");
                    if (motorSvg != null) motorSvg.alternarNotasAnimadas(false);
                }
            });
        });
    }

    /**
     * Vincula todos los listeners y propiedades del reproductor al controlador y la UI.
     */
    private void configurarEventosReproductor() {
        reproductor.setFinalizaCancion(() -> Platform.runLater(this::onNext));
        configuraEventoSlider(reproductor);
        configuraTiempoCancion(reproductor);
        configuraTocadiscoAnimaciones(reproductor);
        controlesController.setOnProgresoSoltado(() -> reproductor.adelantar(controlesController.getProgreso() / 100.0));
    }

    /**
     * Notificación de que se agregó una canción a la lista.
     */
    public void onCancionAgregada() {
        if (playlistController.getListaCancion().tam() == 1) {
            actualPos = 0;
        }
        if (controlesController != null && controlesController.isShuffle()) {
            shuffleManager.generarCola(playlistController.getListaCancion().tam(), actualPos);
        }
    }

    /**
     * Reproduce una canción por su índice en la lista.
     * 
     * @param index Índice de la canción en la playlist.
     */
    public void reproducirCancionPorIndice(int index) {
        actualPos = index;
        onPlay();
    }

    /**
     * Carga una canción en los controladores de la aplicación partiendo de su NodoDoble.
     */
    private void cargarDesdeNodo(NodoDoble nodo, int posLista) {
        if (nodo == null) return;
        this.cancionActual = nodo;
        this.actualPos = posLista;
        Cancion cancion = (Cancion) nodo.getNodoInfo();
        if (controlesController != null) {
            controlesController.actualizarTextos(cancion.getTitulo(), cancion.getArtista());
        }

        actualizarTema(cancion);
        reproductor.reproducirNueva(cancion);
        playlistController.selectIndex(actualPos);
    }

    /**
     * Carga y reproduce la canción según el índice indicado.
     */
    private void actualizaCancionPorIndice(int pos) {
        if (playlistController != null && pos >= 0 && pos < playlistController.getListaCancion().tam()) {
            cargarDesdeNodo(playlistController.getListaCancion().obtenerNodo(pos), pos);
        }
    }

    /**
     * Actualiza el tema de colores y visualizaciones en base a la paleta de la portada.
     */
    private void actualizarTema(Cancion cancion) {
        Image portada = cancion.getPortada();
        Paleta paleta = ExtractorPaleta.extraerDe(portada);
        ThemeManager.aplicarPaleta(playlistController.getLvListSong().getScene(), paleta);

        if (tocadiscosController != null) {
            tocadiscosController.actualizarColoresDinamicos(paleta.getAcento(), paleta.getBrillante());
        }

        if (motorSvg != null) {
            boolean isPlaying = (reproductor.estadoProperty().get() == MediaPlayer.Status.PLAYING);
            motorSvg.actualizarFondo(paleta, portada, isPlaying);
        }
    }

    /**
     * Sincroniza la posición lógica y el nodo actual después de cambiar el orden de la colección física.
     * 
     * @param song Canción activa antes del ordenamiento.
     */
    public void actualizarPosicionTrasOrdenamiento(Cancion song) {
        if (song != null) {
            int idx = playlistController.getListaCancion().buscar(song);
            if (idx != -1) {
                cancionActual = playlistController.getListaCancion().obtenerNodo(idx);
                actualPos = idx;
            }
        }
        if (controlesController != null && controlesController.isShuffle()) {
            shuffleManager.generarCola(playlistController.getListaCancion().tam(), actualPos);
        }
    }

    /**
     * Desplaza la referencia de la canción actual a la siguiente o anterior disponible.
     */
    private void desplazarCancionActualTrasEliminacion() {
        if (cancionActual.getNextNodo() != null) {
            cancionActual = cancionActual.getNextNodo();
        } else if (cancionActual.getPrevNodo() != null) {
            cancionActual = cancionActual.getPrevNodo();
        } else {
            cancionActual = null;
        }
    }

    /**
     * Elimina las canciones físicas basándose en los índices seleccionados en el diálogo.
     * 
     * @param indices Colección de índices a eliminar.
     */
    public void eliminarCanciones(ListaIndices indices) {
        int posActual = cancionActual != null ? playlistController.getListaCancion().buscar(cancionActual.getNodoInfo()) : -1;

        // Iterar en orden inverso para evitar desfase de índices
        for (int i = indices.tam() - 1; i >= 0; i--) {
            int targetIdx = (Integer) indices.devolver(i);
            
            if (posActual != -1 && targetIdx == posActual) {
                reproductor.liberar();
                desplazarCancionActualTrasEliminacion();
            }
            
            if (posActual != -1 && targetIdx < posActual) {
                posActual--;
            }
            
            playlistController.getListaCancion().eliminar(targetIdx);
        }

        actualizarUIPostEliminacion();

        if (controlesController != null && controlesController.isShuffle()) {
            shuffleManager.generarCola(playlistController.getListaCancion().tam(), actualPos);
        }
    }

    /**
     * Sincroniza y actualiza la interfaz gráfica y los metadatos de la canción actual después de eliminar.
     */
    private void actualizarUIPostEliminacion() {
        if (cancionActual != null) {
            int newIdx = playlistController.getListaCancion().buscar(cancionActual.getNodoInfo());
            if (newIdx != -1) {
                actualPos = newIdx;
                Cancion cancion = (Cancion) cancionActual.getNodoInfo();
                if (controlesController != null) {
                    controlesController.actualizarTextos(cancion.getTitulo(), cancion.getArtista());
                }
                actualizarTema(cancion);
                playlistController.selectIndex(actualPos);
            }
        } else {
            actualPos = -1;
            if (controlesController != null) {
                controlesController.actualizarTextos("Sin canción", "Desconocido");
                controlesController.resetearProgreso();
                controlesController.actualizarTiempos("0:00", "0:00");
            }
            if (tocadiscosController != null) {
                tocadiscosController.pausarAnimacion();
            }
        }
    }

    /**
     * Callback de reproducción y pausa.
     */
    @Override
    public void onPlay() {
        if (playlistController.getListaCancion().estaVacia()) return;
        
        int posDeLaActual = cancionActual != null ? playlistController.getListaCancion().buscar(cancionActual.getNodoInfo()) : -1;
        
        if (cancionActual == null || posDeLaActual != actualPos || !reproductor.tieneMedia()) {
            actualizaCancionPorIndice(actualPos);
        } else {
            reproductor.alternarPausaReproduccion();
        }
    }

    /**
     * Callback para saltar a la siguiente canción en la cola de reproducción.
     */
    @Override
    public void onNext() {
        if (playlistController.getListaCancion().estaVacia()) return;

        if (controlesController.isShuffle()) {
            int pos = shuffleManager.siguiente(playlistController.getListaCancion().tam(), actualPos);
            actualizaCancionPorIndice(pos);
        } else {
            if (cancionActual == null) {
                actualizaCancionPorIndice(0);
                return;
            }
            NodoDoble siguiente = cancionActual.getNextNodo();
            if (siguiente != null) {
                cargarDesdeNodo(siguiente, actualPos + 1);
            } else if (controlesController.isLoop()) {
                actualizaCancionPorIndice(0);
            } else {
                onStopSong();
            }
        }
    }

    /**
     * Callback para retroceder a la canción anterior en la cola de reproducción.
     */
    @Override
    public void onPrevious() {
        if (playlistController.getListaCancion().estaVacia()) return;

        if (controlesController.isShuffle()) {
            int pos = shuffleManager.anterior(actualPos);
            actualizaCancionPorIndice(pos);
        } else {
            if (cancionActual == null) {
                actualizaCancionPorIndice(0);
                return;
            }
            NodoDoble anterior = cancionActual.getPrevNodo();
            if (anterior != null) {
                cargarDesdeNodo(anterior, actualPos - 1);
            } else if (controlesController.isLoop()) {
                actualizaCancionPorIndice(playlistController.getListaCancion().tam() - 1);
            } else {
                actualizaCancionPorIndice(0);
            }
        }
    }

    /**
     * Callback que se ejecuta cuando el modo shuffle es alternado.
     */
    @Override
    public void onShuffleToggled(boolean activo) {
        if (activo) {
            shuffleManager.generarCola(playlistController.getListaCancion().tam(), actualPos);
        } else {
            shuffleManager.limpiar();
        }
    }

    /**
     * Callback para alternar modo bucle completo.
     */
    @Override
    public void onLoopToggled(boolean activo) { }

    /**
     * Callback para alternar el bucle de una pista individual.
     */
    @Override
    public void onLoopSongCircle(boolean activo) { }

    /**
     * Callback para detener la pista musical actual y resetear la aguja e interfaz gráfica.
     */
    @Override
    public void onStopSong() {
        reproductor.detener();
        Platform.runLater(() -> {
            if (controlesController != null) {
                controlesController.cambiarTextoBotonPlay("▶");
                controlesController.resetearProgreso();
                controlesController.actualizarTiempos("0:00", tiempoTotalStr);
            }
            if (tocadiscosController != null) {
                tocadiscosController.pausarAnimacion();
            }
        });
    }

    /**
     * Reposiciona el panel del tocadiscos en la ventana en base a las relaciones de tamaño y escala asimétrica.
     */
    private void reposicionarTocadiscos() {
        double W = mainStackPane.getWidth();
        double H = mainStackPane.getHeight();
        if (W <= 0 || H <= 0) return;

        double V_w = 1200.0;
        double V_h = 700.0;
        double R_v = V_w / V_h;
        double R_a = W / H;

        double S;
        double Y_offset = 0;
        double X_offset = 0;

        if (R_a > R_v) {
            S = W / V_w;
            double H_scaled = V_h * S;
            Y_offset = (H_scaled - H) / 2.0;
        } else {
            S = H / V_h;
            double W_scaled = V_w * S;
            X_offset = (W_scaled - W) / 2.0;
        }

        double x_v = 630.0;
        double y_v = 450.0;

        double x_real = x_v * S - X_offset;
        double y_real = y_v * S - Y_offset;

        double transX = x_real - (W / 2.0);
        double transY = y_real - (H / 2.0);

        if (tocadiscos != null) {
            tocadiscos.setTranslateX(transX);
            tocadiscos.setTranslateY(transY);

            double escalaBaseSvg = 0.65;
            double factorEscalaBase = S / escalaBaseSvg;

            factorEscalaBase = Math.max(0.35, Math.min(factorEscalaBase, 1.8));

            tocadiscos.setScaleX(factorEscalaBase * 0.55);
            tocadiscos.setScaleY(factorEscalaBase * 0.50);
        }
    }

    /**
     * Devuelve el índice de la canción activa en el reproductor.
     * 
     * @return Entero con la posición.
     */
    public int getActualPos() { return actualPos; }
    public Cancion getCancionActual() {
        return cancionActual != null ? (Cancion) cancionActual.getNodoInfo() : null;
    }
}