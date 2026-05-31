package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class ControlesController {

    @FXML private Button btnPlayPause;
    @FXML private Label lblSongTitle;
    @FXML private Label lblSongArtist;
    @FXML private Label lblTimeCurrent;
    @FXML private Label lblTimeTotal;

    // Lo dejamos público para que el MainController pueda moverlo
    @FXML public Slider progressSlider;

    // Referencia al controlador principal para avisarle de los clics
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // --- EVENTOS DE CLIC (Le avisan al MainController) ---
    @FXML void onPlayPause(ActionEvent event) {
        if (mainController != null) mainController.playButtonEvent(null);
    }

    @FXML void onNext(ActionEvent event) {
        if (mainController != null) mainController.nextButtonEvent(null);
    }

    @FXML void onPrevious(ActionEvent event) {
        if (mainController != null) mainController.previousButtonEvent(null);
    }

    @FXML void onShuffle(ActionEvent event) {}
    @FXML void onLoop(ActionEvent event) {}

    // --- MÉTODOS PARA ACTUALIZAR LA PANTALLA ---
    public void cambiarTextoBotonPlay(String texto) {
        btnPlayPause.setText(texto);
    }

    public void actualizarTextos(String titulo, String artista) {
        lblSongTitle.setText(titulo);
        lblSongArtist.setText(artista);
    }

    public void actualizarTiempos(String actual, String total) {
        lblTimeCurrent.setText(actual);
        lblTimeTotal.setText(total);
    }
}