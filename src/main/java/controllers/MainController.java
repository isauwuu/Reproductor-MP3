package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainController {

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

}
