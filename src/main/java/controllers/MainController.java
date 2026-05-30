package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import modelo.datos.Cancion;
import modelo.datos.ExtractorPaleta;
import modelo.datos.Paleta;
import ui.ThemeManager;

import java.io.File;

public class MainController {
    private Cancion cancionActual;

    public MainController(){
        cancionActual = null;
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
    private TableColumn<?, ?> colArtist;
    @FXML
    private TableColumn<?, ?> colTitle;
    @FXML
    private TableColumn<?, ?> colYear;
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
    private TableView<?> tvPlaylist;

    @FXML
    void abrirArchivosEvent(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar canción");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos MP3","*.mp3")
        );

        File archivo = fileChooser.showOpenDialog(btnAddSong.getScene().getWindow());
        if (archivo != null) {
            Cancion cancion = new Cancion(archivo.getAbsolutePath());
            cargarCancion(cancion);
            System.out.println("año: " + cancion.getAnio());
            System.out.println("titulo: "+cancion.getTitulo());
            System.out.println("artista: "+cancion.getArtista());
            System.out.println("duracion: "+cancion.getDuracionFormateada());
        }
    }

    @FXML
    void loopButtonEvent(ActionEvent event) {

    }

    @FXML
    void nextButtonEvent(ActionEvent event) {

    }

    @FXML
    void playButtonEvent(ActionEvent event) {

    }

    @FXML
    void previousButtonEvent(ActionEvent event) {

    }

    @FXML
    void removeSongEvent(ActionEvent event) {

    }

    @FXML
    void shuffleButtonEvent(ActionEvent event) {

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
