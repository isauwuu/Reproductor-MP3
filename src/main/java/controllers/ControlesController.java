package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class ControlesController {

    @FXML private Button btnPlayPause;
    @FXML private Button btnShuffle;
    @FXML private Button btnLoop;
    @FXML private Label lblSongTitle;
    @FXML private Label lblSongArtist;
    @FXML private Label lblTimeCurrent;
    @FXML private Label lblTimeTotal;
    @FXML public Slider progressSlider;

    private MainController mainController;

    // Solo guardamos el estado visual (prendido/apagado)
    private boolean shuffleActivo = false;
    private boolean loopActivo    = false;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    // Getters para que el MainController sepa qué botón está presionado
    public boolean isShuffle() { return shuffleActivo; }
    public boolean isLoop()    { return loopActivo; }


  //Solo delegan el trabajo al MainController)

    @FXML void onPlayPause(ActionEvent event) {
        if (mainController != null) mainController.playButtonEvent(event);
    }

    @FXML void onNext(ActionEvent event) {
        if (mainController != null) mainController.nextButtonEvent(event);
    }

    @FXML void onPrevious(ActionEvent event) {
        if (mainController != null) mainController.previousButtonEvent(event);
    }


    @FXML void onShuffle(ActionEvent event) {
        shuffleActivo = !shuffleActivo;
        loopActivo = false; // El shuffle apaga el loop por defecto

        actualizarEstiloBoton(btnShuffle, shuffleActivo);
        actualizarEstiloBoton(btnLoop, false);

        // (En el futuro, acá le avisaremos al MainController que arme la nueva lógica aleatoria)
    }

    @FXML void onLoop(ActionEvent event) {
        loopActivo = !loopActivo;
        shuffleActivo = false; // El loop apaga el shuffle por defecto

        actualizarEstiloBoton(btnLoop, loopActivo);
        actualizarEstiloBoton(btnShuffle, false);
    }

    // -----------------------------------------------------------------------
    // UI HELPERS (Modifican elementos gráficos de la pantalla)
    // -----------------------------------------------------------------------

    private void actualizarEstiloBoton(Button btn, boolean activo) {
        if (btn == null) return;
        if (activo) {
            if (!btn.getStyleClass().contains("pixel-button-active")) {
                btn.getStyleClass().add("pixel-button-active");
            }
        } else {
            btn.getStyleClass().remove("pixel-button-active");
        }
    }

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