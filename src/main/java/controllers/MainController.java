package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import javafx.stage.FileChooser;
import modelo.datos.Cancion;
import modelo.datos.ExtractorPaleta;
import modelo.datos.ListaCancion;
import modelo.datos.Paleta;
import modelo.estructuras.NodoDoble;
import ui.ThemeManager;

import java.io.File;

public class MainController {
    private Cancion cancionActual;
    private File ultimaCarpeta;
    private ListaCancion listaCancion;
    private int actualPos;
    private final MediaPlayerFactory factory;
    private final MediaPlayer mediaPlayer;

    public MainController(){
        cancionActual = null;
        ultimaCarpeta = null;
        listaCancion = new ListaCancion();
        actualPos = -1;
        factory = new MediaPlayerFactory();
        mediaPlayer = factory.mediaPlayers().newMediaPlayer();
    }

    @FXML
    private Button btnAddSong;
    @FXML
    private Button btnLoop;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnPlayPause;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnRemoveSong;
    @FXML
    private Button btnShuffle;
    @FXML
    private Label lblSongArtist;
    @FXML
    private Label lblSongTitle;
    @FXML
    private Label lblTime;
    @FXML
    private Pane leftDecorationPane;
    @FXML
    private Slider progressSlider;
    @FXML
    private VBox turntableVisual;
    @FXML
    private Pane PaneContol;
    @FXML
    private MenuButton btnMenuOrdenamiento;
    @FXML
    private MenuItem btnOrdenarPorArtista;
    @FXML
    private MenuItem btnOrdenarPorNombre;
    @FXML
    private ListView<String> lvListSong;

    @FXML
    public void initialize() {
    }

    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canción");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos MP3","*.mp3")
        );
        if (ultimaCarpeta != null)
            fileChooser.setInitialDirectory(ultimaCarpeta);
        File archivo = fileChooser.showOpenDialog(btnAddSong.getScene().getWindow());
        if (archivo != null) {
            ultimaCarpeta = archivo.getParentFile();
            creaCancion(archivo);
        }
    }

    private void creaCancion(File file){
        Cancion cancion = new Cancion(file.getAbsolutePath());
        listaCancion.insertar(cancion,listaCancion.tam());
        lvListSong.getItems().add("♪ " + cancion.getTitulo() + " - " + cancion.getArtista());
        if(listaCancion.tam()==1)actualPos=0;
    }

    private void reproducir(Cancion cancion) {

        if(cancion == null)
            return;

        mediaPlayer.controls().stop();

        mediaPlayer.media().play(
                cancion.getRutaArchivo()
        );
    }

    @FXML
    void loopButtonEvent(ActionEvent event) {

    }

    @FXML
    void nextButtonEvent(ActionEvent event) {
        actualizaCancion(actualPos+1);
    }

    @FXML
    void playButtonEvent(ActionEvent event) {
        if(mediaPlayer.status().isPlaying()) {

            mediaPlayer.controls().pause();

        } else {

            actualizaCancion(actualPos);

            if(cancionActual != null)
                reproducir(cancionActual);
        }
    }

    @FXML
    void previousButtonEvent(ActionEvent event) {
        actualizaCancion(actualPos-1);
    }

    private void actualizaCancion(int actualPos){
        if(actualPos<listaCancion.tam()&&actualPos>-1){
            this.actualPos = actualPos;
            this.cancionActual = (Cancion) listaCancion.devolver(actualPos);
            cargarCancion(cancionActual);
        }
    }

    @FXML
    void removeSongEvent(ActionEvent event) {

    }

    @FXML
    void shuffleButtonEvent(ActionEvent event) {

    }

    @FXML
    void ordenarPorAnioEvent(ActionEvent event) {

    }

    @FXML
    void ordenarPorArtistaEvent(ActionEvent event) {

    }

    @FXML
    void ordenarPorNombreEvent(ActionEvent event) {

    }

    private void cargarCancion(Cancion cancion){
        this.cancionActual = cancion;
        lblSongTitle.setText(cancion.getTitulo());
        lblSongArtist.setText(cancion.getArtista());

        actualizarTema(cancion);
    }

    private void actualizarTema(Cancion cancion) {
        Image portada = cancion.getPortada();
        Paleta paleta =ExtractorPaleta.extraerDe(portada);
        ThemeManager.aplicarPaleta(lblSongTitle.getScene(),paleta);
    }
}
