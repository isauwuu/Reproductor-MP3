package controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import modelo.criterios.CriterioOrdenacion;
import modelo.criterios.PorAnio;
import modelo.criterios.PorArtista;
import modelo.criterios.PorNombre;
import modelo.datos.Cancion;
import modelo.datos.ListaCancion;
import modelo.datos.ListaIndices;
import services.FileImportService;
import ui.DeleteSongsDialog;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la sección de lista de reproducción (Playlist).
 * Coordina la lista de canciones en pantalla delegando las tareas de importación a
 * {@link FileImportService} y las pantallas modales a {@link DeleteSongsDialog}.
 */
public class PlaylistController {

    private final ListaCancion listaCancion;
    private final FileImportService importService;
    private MainController mainController;

    @FXML private Button btnAddSong;
    @FXML private Button btnRemoveSong;
    @FXML private Button btnAddFolder;
    @FXML private MenuButton btnMenuOrdenamiento;
    @FXML private ListView<Cancion> lvListSong;

    /**
     * Constructor del controlador de playlist.
     * Inicializa la lista de canciones y el servicio de importación de archivos.
     */
    public PlaylistController() {
        this.listaCancion = new ListaCancion();
        this.importService = new FileImportService();
    }

    /**
     * Vincula el controlador principal (MainController) de la aplicación.
     * 
     * @param mainController Instancia del MainController.
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Inicializa el controlador estableciendo eventos del ListView.
     */
    @FXML
    public void initialize() {
        lvListSong.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                int selectedIndex = lvListSong.getSelectionModel().getSelectedIndex();
                if (selectedIndex != -1 && mainController != null) {
                    mainController.reproducirCancionPorIndice(selectedIndex);
                }
            }
        });
    }

    /**
     * Inicializa el factory de celdas personalizadas para la lista.
     * Esto debe ser llamado después de inyectar MainController para asegurar su referencia.
     */
    public void initCellFactory() {
        lvListSong.setCellFactory(param -> new CancionListCell(mainController));
    }

    /**
     * Obtiene el modelo de la lista física de canciones.
     * 
     * @return Instancia de {@link ListaCancion}.
     */
    public ListaCancion getListaCancion() {
        return listaCancion;
    }

    /**
     * Obtiene el componente ListView gráfico de canciones.
     * 
     * @return Instancia de {@link ListView}.
     */
    public ListView<Cancion> getLvListSong() {
        return lvListSong;
    }

    /**
     * Fuerza el refresco visual de los elementos de la ListView.
     */
    public void refreshListView() {
        lvListSong.refresh();
    }

    /**
     * Selecciona visualmente un índice en la playlist.
     * 
     * @param index Índice de la canción.
     */
    public void selectIndex(int index) {
        if (index >= 0 && index < listaCancion.tam()) {
            lvListSong.getSelectionModel().select(index);
            lvListSong.scrollTo(index);  // también scrollea al elemento activo
            lvListSong.refresh();        // fuerza updateItem en todas las celdas visibles
        }
    }

    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        List<File> archivos = importService.seleccionarArchivosMp3(btnAddSong.getScene().getWindow());
        for (File archivo : archivos) {
            creaCancion(archivo);
        }
    }

    @FXML
    void abrirCarpetaEvent(ActionEvent event) {
        List<File> archivos = importService.seleccionarCarpetaMp3(btnAddSong.getScene().getWindow());
        for (File archivo : archivos) {
            creaCancion(archivo);
        }
    }

    private void creaCancion(File file) {
        Cancion cancion = new Cancion(file.getAbsolutePath());
        listaCancion.insertar(cancion, listaCancion.tam());
        btnMenuOrdenamiento.setText("ordenar");
        lvListSong.getItems().add(cancion);
        
        if (mainController != null) {
            mainController.onCancionAgregada();
        }
        lvListSong.refresh();
    }

    public void actualizaListaView() {
        lvListSong.getItems().clear();
        for (int i = 0; i < listaCancion.tam(); i++) {
            Cancion cancion = (Cancion) listaCancion.devolver(i);
            lvListSong.getItems().add(cancion);
        }

        if (mainController != null && mainController.getActualPos() != -1) {
            selectIndex(mainController.getActualPos());
        } else {
            lvListSong.refresh();
        }
    }

    @FXML
    void removeSongEvent(ActionEvent event) {
        DeleteSongsDialog dialog = new DeleteSongsDialog(
                lvListSong.getItems(),
                lvListSong.getScene().getStylesheets(),
                lvListSong.getScene().getRoot().getStyle(),
                mainController
        );

        Optional<ListaIndices> resultado = dialog.showAndWait();
        if (resultado.isPresent()) {
            if (mainController != null) {
                mainController.eliminarCanciones(resultado.get());
            }
            actualizaListaView();
        }
    }

    private void ordenarYActualizar(CriterioOrdenacion criterio, String label) {
        Cancion cancionActual = (mainController != null) ? mainController.getCancionActual() : null;
        listaCancion.ordenar(criterio);
        if (mainController != null)
            mainController.actualizarPosicionTrasOrdenamiento(cancionActual);
        actualizaListaView();
        btnMenuOrdenamiento.setText(label);
    }

    @FXML void ordenarPorAnioEvent(ActionEvent event)    { ordenarYActualizar(new PorAnio(),    "año"); }
    @FXML void ordenarPorNombreEvent(ActionEvent event)  { ordenarYActualizar(new PorNombre(),  "nombre"); }
    @FXML void ordenarPorArtistaEvent(ActionEvent event) { ordenarYActualizar(new PorArtista(), "artista"); }
}
