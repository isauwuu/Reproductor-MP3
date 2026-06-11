package controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
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
import java.util.Optional;
import javafx.scene.layout.StackPane;

/**
 * Controlador principal de la interfaz de usuario del reproductor de MP3.
 * Implementa la interfaz {@link ReproductorListener} para responder a los eventos de la reproducción.
 */
public class MainController implements ReproductorListener {

    private NodoDoble cancionActual;
    private File ultimaCarpeta;
    private ListaCancion listaCancion;
    private int actualPos;
    private String tiempoTotalStr;
    private SvgController motorSvg;
    private ReproductorDeAudio reproductor;
    private ShuffleManager shuffleManager;
    boolean playingSongDeleted;

    @FXML private TocadiscosController tocadiscosController;
    @FXML private ControlesController controlesController;
    @FXML private Button btnRemoveSong;
    @FXML private MenuButton btnMenuOrdenamiento;
    @FXML private MenuItem btnOrdenarPorAnio;
    @FXML private MenuItem btnOrdenarPorArtista;
    @FXML private MenuItem btnOrdenarPorNombre;
    @FXML private ListView<Cancion> lvListSong;
    @FXML private StackPane mainStackPane;
    @FXML private StackPane tocadiscos;
    @FXML private Button btnAddSong;
    @FXML private Button btnAddFolder;

    /**
     * Constructor del controlador principal.
     * Inicializa las estructuras de datos y servicios.
     */
    public MainController() {
        this.listaCancion = new ListaCancion();
        this.reproductor = new ReproductorDeAudio();
        this.shuffleManager = new ShuffleManager();
        this.actualPos = -1;
        this.tiempoTotalStr = "0:00";
        this.playingSongDeleted = false;
    }

    /**
     * Método de inicialización llamado automáticamente por JavaFX después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        if (controlesController != null) {
            controlesController.setListener(this);
        }
        configuraFondo();
        configurarEventosReproductor();
        tocadiscoResponsive();

        lvListSong.setCellFactory(param -> new CancionListCell(this));

        lvListSong.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                actualPos = lvListSong.getSelectionModel().getSelectedIndex();
                onPlay();
            }
        });
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
     * 
     * @param reproductor Instancia del reproductor de audio.
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
     * 
     * @param reproductor Instancia del reproductor de audio.
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
     * 
     * @param reproductor Instancia del reproductor de audio.
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
     * Evento al pulsar el botón de añadir archivos individuales.
     * 
     * @param event Evento de acción de la interfaz.
     */
    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canciones");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MP3", "*.mp3"));
        if (ultimaCarpeta != null) {
            fileChooser.setInitialDirectory(ultimaCarpeta);
        }

        java.util.List<File> archivos = fileChooser.showOpenMultipleDialog(btnAddSong.getScene().getWindow());
        if (archivos != null && !archivos.isEmpty()) {
            ultimaCarpeta = archivos.get(0).getParentFile();
            for (File archivo : archivos) {
                creaCancion(archivo);
            }
        }
    }

    /**
     * Evento al pulsar el botón de añadir una carpeta completa de música.
     * 
     * @param event Evento de acción de la interfaz.
     */
    @FXML
    void abrirCarpetaEvent(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta de música");
        if (ultimaCarpeta != null) {
            directoryChooser.setInitialDirectory(ultimaCarpeta);
        }

        File carpeta = directoryChooser.showDialog(btnAddSong.getScene().getWindow());
        if (carpeta == null) return;

        ultimaCarpeta = carpeta;
        File[] archivos = carpeta.listFiles();
        if (archivos == null) return;

        for (File archivo : archivos) {
            if (archivo.getName().toLowerCase().endsWith(".mp3")) {
                creaCancion(archivo);
            }
        }
    }

    /**
     * Instancia una canción a partir de un archivo en disco y la añade a la lista.
     * 
     * @param file Archivo MP3.
     */
    private void creaCancion(File file) {
        Cancion cancion = new Cancion(file.getAbsolutePath(), listaCancion.tam());
        listaCancion.insertar(cancion, listaCancion.tam());
        btnMenuOrdenamiento.setText("ordenar");
        lvListSong.getItems().add(cancion);
        if (listaCancion.tam() == 1) {
            actualPos = 0;
        }
        if (controlesController != null && controlesController.isShuffle()) {
            shuffleManager.generarCola(listaCancion.tam(), actualPos);
        }
        lvListSong.refresh();
    }

    /**
     * Refresca la lista visual en la interfaz de usuario en base al orden de listaCancion.
     */
    private void actualizaListaView() {
        lvListSong.getItems().clear();
        for (int i = 0; i < listaCancion.tam(); i++) {
            Cancion cancion = (Cancion) listaCancion.devolver(i);
            lvListSong.getItems().add(cancion);
        }
        lvListSong.refresh();
    }

    /**
     * Carga una canción en los controladores de la aplicación partiendo de su NodoDoble de la lista física.
     * 
     * @param nodo     NodoDoble que contiene la información de la canción.
     * @param posLista Posición en la que se encuentra en la playlist.
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
        lvListSong.getSelectionModel().select(actualPos);
        lvListSong.refresh();
    }

    /**
     * Carga y reproduce la canción según el índice indicado.
     * 
     * @param pos Índice de la canción.
     */
    private void actualizaCancionPorIndice(int pos) {
        if (pos >= 0 && pos < listaCancion.tam()) {
            cargarDesdeNodo(listaCancion.obtenerNodo(pos), pos);
        }
    }

    /**
     * Actualiza el tema de colores y visualizaciones en base a la paleta de la portada del tema actual.
     * 
     * @param cancion Canción actual para extraer metadatos.
     */
    private void actualizarTema(Cancion cancion) {
        Image portada = cancion.getPortada();
        Paleta paleta = ExtractorPaleta.extraerDe(portada);
        ThemeManager.aplicarPaleta(lvListSong.getScene(), paleta);

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
     */
    private void actualizarPosicionTrasOrdenamiento() {
        if (cancionActual != null) {
            Cancion song = (Cancion) cancionActual.getNodoInfo();
            int idx = listaCancion.buscar(song);
            if (idx != -1) {
                cancionActual = listaCancion.obtenerNodo(idx);
                actualPos = idx;
            }
        }
        if (controlesController != null && controlesController.isShuffle()) {
            shuffleManager.generarCola(listaCancion.tam(), actualPos);
        }
    }

    /**
     * Evento para ordenar la lista física de canciones por año.
     * 
     * @param event Evento de JavaFX.
     */
    @FXML
    void ordenarPorAnioEvent(ActionEvent event) {
        listaCancion.ordenar(new PorAnio());
        actualizarPosicionTrasOrdenamiento();
        actualizaListaView();
        btnMenuOrdenamiento.setText("año");
    }

    /**
     * Evento para ordenar la lista física de canciones por nombre.
     * 
     * @param event Evento de JavaFX.
     */
    @FXML
    void ordenarPorNombreEvent(ActionEvent event) {
        listaCancion.ordenar(new PorNombre());
        actualizarPosicionTrasOrdenamiento();
        actualizaListaView();
        btnMenuOrdenamiento.setText("nombre");
    }

    /**
     * Evento para ordenar la lista física de canciones por artista.
     * 
     * @param event Evento de JavaFX.
     */
    @FXML
    void ordenarPorArtistaEvent(ActionEvent event) {
        listaCancion.ordenar(new PorArtista());
        actualizarPosicionTrasOrdenamiento();
        actualizaListaView();
        btnMenuOrdenamiento.setText("artista");
    }

    /**
     * Configura el cuadro de diálogo para la selección múltiple de canciones a eliminar.
     * 
     * @param dialog Cuadro de diálogo de confirmación.
     * @param lista  Lista de cadenas en pantalla.
     */
    private void createDialogError(Dialog<ButtonType> dialog, ListView<String> lista) {
        dialog.setTitle("Eliminar canciones");
        lista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (int i = 0; i < lvListSong.getItems().size(); i++) {
            lista.getItems().add(String.valueOf(lvListSong.getItems().get(i)));
        }
        dialog.getDialogPane().setContent(lista);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }

    /**
     * Copia los índices seleccionados de la ListView visual a una estructura de ListaIndices propia.
     * 
     * @param seleccionados Lista observable de índices de JavaFX.
     * @return Una ListaIndices que contiene los índices seleccionados.
     */
    private ListaIndices copiarIndicesSeleccionados(ObservableList<Integer> seleccionados) {
        ListaIndices indices = new ListaIndices();
        for (Integer indice : seleccionados) {
            indices.insertar(indice);
        }
        return indices;
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
     * Elimina las canciones de la lista física en los índices indicados, ajustando la canción
     * actualmente en reproducción y liberando el reproductor si esta es eliminada.
     * 
     * @param indices Estructura ListaIndices con los índices de las canciones a eliminar.
     */
    private void eliminarCancionesFisicas(ListaIndices indices) {
        int posActual = cancionActual != null ? listaCancion.buscar(cancionActual.getNodoInfo()) : -1;

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
            
            listaCancion.eliminar(targetIdx);
        }
    }

    /**
     * Sincroniza y actualiza la interfaz gráfica y los metadatos de la canción actual
     * después de que se haya realizado una eliminación de pistas.
     */
    private void actualizarUIPostEliminacion() {
        if (cancionActual != null) {
            int newIdx = listaCancion.buscar(cancionActual.getNodoInfo());
            if (newIdx != -1) {
                actualPos = newIdx;
                Cancion cancion = (Cancion) cancionActual.getNodoInfo();
                if (controlesController != null) {
                    controlesController.actualizarTextos(cancion.getTitulo(), cancion.getArtista());
                }
                actualizarTema(cancion);
                lvListSong.getSelectionModel().select(actualPos);
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
     * Evento al pulsar el botón de eliminar canciones. Abre un modal con opciones de selección múltiple.
     * 
     * @param event Evento de acción de la interfaz.
     */
    @FXML
    void removeSongEvent(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        ListView<String> lista = new ListView<>();
        createDialogError(dialog, lista);

        Optional<ButtonType> resultado = dialog.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            ObservableList<Integer> seleccionados = lista.getSelectionModel().getSelectedIndices();
            
            ListaIndices indices = copiarIndicesSeleccionados(seleccionados);
            eliminarCancionesFisicas(indices);
            actualizarUIPostEliminacion();
            
            if (controlesController != null && controlesController.isShuffle()) {
                shuffleManager.generarCola(listaCancion.tam(), actualPos);
            }
            
            actualizaListaView();
        }
    }

    /**
     * Callback de reproducción y pausa.
     */
    @Override
    public void onPlay() {
        if (listaCancion.estaVacia()) return;
        
        int posDeLaActual = cancionActual != null ? listaCancion.buscar(cancionActual.getNodoInfo()) : -1;
        
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
        if (listaCancion.estaVacia()) return;

        if (controlesController.isShuffle()) {
            int pos = shuffleManager.siguiente(listaCancion.tam(), actualPos);
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
        if (listaCancion.estaVacia()) return;

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
                actualizaCancionPorIndice(listaCancion.tam() - 1);
            } else {
                actualizaCancionPorIndice(0);
            }
        }
    }

    /**
     * Callback que se ejecuta cuando el modo shuffle es alternado.
     * 
     * @param activo Indica si el modo aleatorio está encendido.
     */
    @Override
    public void onShuffleToggled(boolean activo) {
        if (activo) {
            shuffleManager.generarCola(listaCancion.tam(), actualPos);
        } else {
            shuffleManager.limpiar();
        }
    }

    /**
     * Callback para alternar modo bucle completo.
     * 
     * @param activo true si está activo.
     */
    @Override
    public void onLoopToggled(boolean activo) { }

    /**
     * Callback para alternar el bucle de una pista individual.
     * 
     * @param activo true si está activo.
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

        double x_v = 675.0;
        double y_v = 490.0;

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

    /**
     * Genera una imagen pixelada de 8 bits a partir del título y artista de la canción.
     * Funciona como portada provisional cuando no se dispone de metadatos integrados.
     * 
     * @param cancion Canción para la cual generar la portada.
     * @return WritableImage que contiene la portada pixelada generada de 8 bits.
     */
    Image crearPlaceholder8Bit(Cancion cancion) {
        int w = 32, h = 32;
        WritableImage img = new WritableImage(w, h);
        PixelWriter pw = img.getPixelWriter();
        
        int hash = cancion.getTitulo().hashCode() + cancion.getArtista().hashCode();
        Color bgColor = Color.rgb(
            Math.abs((hash) % 100) + 20,
            Math.abs((hash >> 8) % 100) + 20,
            Math.abs((hash >> 16) % 100) + 20
        );
        
        Color fgColor = Color.rgb(
            Math.abs((hash >> 4) % 120) + 130,
            Math.abs((hash >> 12) % 120) + 130,
            Math.abs((hash >> 20) % 120) + 130
        );

        int[][] note = {
            {0,0,0,0,0,0,0,0},
            {0,0,0,1,1,1,1,0},
            {0,0,0,1,0,0,1,0},
            {0,0,0,1,0,0,1,0},
            {0,0,1,1,0,1,1,0},
            {0,1,1,1,0,1,1,1},
            {0,1,1,1,0,1,1,1},
            {0,0,1,1,0,0,1,1}
        };

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x < 2 || x >= w - 2 || y < 2 || y >= h - 2) {
                    pw.setColor(x, y, fgColor.darker());
                } else {
                    int nx = (x - 4) / 3;
                    int ny = (y - 4) / 3;
                    if (nx >= 0 && nx < 8 && ny >= 0 && ny < 8 && note[ny][nx] == 1) {
                        pw.setColor(x, y, fgColor);
                    } else {
                        pw.setColor(x, y, bgColor);
                    }
                }
            }
        }
        return img;
    }
}