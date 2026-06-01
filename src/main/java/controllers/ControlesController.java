package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import modelo.datos.ListaIndices;
import modelo.interfaces.ReproductorListener;

import java.util.Random;

public class ControlesController {

    @FXML private Button btnPlayPause;
    @FXML private Button btnShuffle;
    @FXML private Button btnLoop;
    @FXML private Label lblSongTitle;
    @FXML private Label lblSongArtist;
    @FXML private Label lblTimeCurrent;
    @FXML private Label lblTimeTotal;
    @FXML public Slider progressSlider;

    private ReproductorListener listener;
    private boolean shuffle = false;
    private boolean loop    = false;

    public void setListener(ReproductorListener l) { this.listener = l; }

    public boolean isShuffle() { return shuffle; }
    public boolean isLoop()    { return loop; }

    @FXML void onPlayPause(ActionEvent event) {
        if (listener != null) listener.onPlay();
    }

    @FXML void onNext(ActionEvent event) { if (listener != null) listener.onNext(); }

    @FXML void onPrevious(ActionEvent event) { if (listener != null) listener.onPrevious(); }

    @FXML void onShuffle(ActionEvent event) {
        shuffle = !shuffle;
        loop = false;
        actualizarEstiloBoton(btnShuffle, shuffle);
        actualizarEstiloBoton(btnLoop, false);
        listener.onShuffleToggled(shuffle);
    }

    @FXML void onLoop(ActionEvent event) {
        loop = !loop;
        shuffle = false;
        actualizarEstiloBoton(btnLoop, loop);
        actualizarEstiloBoton(btnShuffle, false);
        listener.onLoopToggled(loop);
    }

    private void actualizarEstiloBoton(Button btn, boolean activo) {
        if (btn == null) return;
        if (activo) {
            if (!btn.getStyleClass().contains("pixel-button-active"))
                btn.getStyleClass().add("pixel-button-active");
        } else {
            btn.getStyleClass().remove("pixel-button-active");
        }
    }

    public void cambiarTextoBotonPlay(String texto) { btnPlayPause.setText(texto); }

    public void actualizarTextos(String titulo, String artista) {
        lblSongTitle.setText(titulo);
        lblSongArtist.setText(artista);
    }

    public void actualizarTiempos(String actual, String total) {
        lblTimeCurrent.setText(actual);
        lblTimeTotal.setText(total);
    }
}