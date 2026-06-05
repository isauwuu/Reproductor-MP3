package controllers;

import javafx.application.Platform;
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
    @FXML private ListView<Cancion> lvListSong;
    @FXML private StackPane mainStackPane;
    @FXML private StackPane tocadiscos;
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
        
        mainStackPane.widthProperty().addListener((obs, oldVal, newVal) -> reposicionarTocadiscos());
        mainStackPane.heightProperty().addListener((obs, oldVal, newVal) -> reposicionarTocadiscos());
        Platform.runLater(this::reposicionarTocadiscos);

        lvListSong.setCellFactory(param -> new ListCell<Cancion>() {
            private final ImageView imageView = new ImageView();
            private final Label titleLabel = new Label();
            private final Label artistLabel = new Label();
            private final HBox hbox = new HBox(10);
            private final VBox vbox = new VBox(2);

            {
                imageView.setFitWidth(32);
                imageView.setFitHeight(32);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(false);

                titleLabel.setStyle("-fx-text-fill: inherit; -fx-font-family: 'Press Start 2P'; -fx-font-size: 10px; -fx-font-weight: bold;");
                artistLabel.setStyle("-fx-text-fill: inherit; -fx-font-family: 'Press Start 2P'; -fx-font-size: 8px; -fx-opacity: 0.7;");
                
                vbox.getChildren().addAll(titleLabel, artistLabel);
                vbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                hbox.getChildren().addAll(imageView, vbox);
                hbox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                hbox.setPadding(new javafx.geometry.Insets(4, 4, 4, 4));
            }

            @Override
            protected void updateItem(Cancion cancion, boolean empty) {
                super.updateItem(cancion, empty);
                if (empty || cancion == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().remove("list-cell-active");
                } else {
                    titleLabel.setText(cancion.getTitulo());
                    artistLabel.setText(cancion.getArtista());

                    Image portada = cancion.getPortada();
                    if (portada != null) {
                        imageView.setImage(portada);
                    } else {
                        imageView.setImage(crearPlaceholder8Bit(cancion));
                    }
                    setGraphic(hbox);

                    if (getIndex() == actualPos) {
                        if (!getStyleClass().contains("list-cell-active")) {
                            getStyleClass().add("list-cell-active");
                        }
                    } else {
                        getStyleClass().remove("list-cell-active");
                    }
                }
            }
        });

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
        lvListSong.getItems().add(cancion);
        if (listaCancion.tam() == 1) actualPos = 0;

        if (controlesController != null && controlesController.isShuffle())
            shuffleManager.generarCola(listaCancion.tam(), actualPos);
        lvListSong.refresh();
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
            lvListSong.getItems().add(cancion);
        }
        lvListSong.refresh();
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
        lvListSong.refresh();
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
                lvListSong.getItems().add(c);
            }
            lvListSong.getSelectionModel().select(actualPos);
            lvListSong.refresh();
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

        // --- POSICIONAMIENTO CORREGIDO ---
        // Subimos considerablemente el anclaje virtual (de 515 a 492)
        // Esto compensa el redimensionado y sube la base del aparato sobre la madera.
        double x_v = 675.0;
        double y_v = 490.0;

        double x_real = x_v * S - X_offset;
        double y_real = y_v * S - Y_offset;

        double transX = x_real - (W / 2.0);
        double transY = y_real - (H / 2.0);

        if (tocadiscos != null) {
            tocadiscos.setTranslateX(transX);
            tocadiscos.setTranslateY(transY);

            // --- ESCALA ASIMÉTRICA OPTIMIZADA ---
            // Usamos una escala base de 0.65 para que conserve buena presencia horizontal
            double escalaBaseSvg = 0.65;
            double factorEscalaBase = S / escalaBaseSvg;

            // Limitadores de seguridad comunes
            factorEscalaBase = Math.max(0.35, Math.min(factorEscalaBase, 1.8));

            // El ancho (X) se mantiene generoso para que sea más ancho que el monitor de la derecha
            tocadiscos.setScaleX(factorEscalaBase * 0.55);

            // La altura (Y) se "aplasta" multiplicándola por un factor menor (0.62)
            // Esto le quita el aspecto de bloque alto y le da la perspectiva correcta de la mesa
            tocadiscos.setScaleY(factorEscalaBase * 0.50);
        }
    }

    private Image crearPlaceholder8Bit(Cancion cancion) {
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