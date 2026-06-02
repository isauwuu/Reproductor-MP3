package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import modelo.interfaces.ReproductorListener;

public class ControlesController {
    @FXML private Button btnPlayPause;
    @FXML private Button btnShuffle;
    @FXML private Button btnLoop;
    @FXML private Label lblSongTitle;
    @FXML private Label lblSongArtist;
    @FXML private Label lblTimeCurrent;
    @FXML private Label lblTimeTotal;
    @FXML public Slider progressSlider;
    @FXML private Button btnLoopSongs;
    @FXML private Button btnStop;

    private ReproductorListener listener;
    private boolean loopSong = false;
    private boolean shuffle  = false;
    private boolean loop     = false;
    private boolean stop     = false;

    public void setListener(ReproductorListener l) { this.listener = l; }
    public boolean isShuffle() { return shuffle; }
    public boolean isLoop()    { return loop; }
    public boolean isLoopSong() { return loopSong; }
    public boolean isStop() { return stop; }

    @FXML void onPlayPause(ActionEvent event) { if (listener != null) listener.onPlay(); }
    @FXML void onNext(ActionEvent event) { if (listener != null) listener.onNext(); }
    @FXML void onPrevious(ActionEvent event) { if (listener != null) listener.onPrevious(); }

    @FXML void onShuffle(ActionEvent event) {
        shuffle = !shuffle;
        loop = loopSong = false;

        actualizarEstiloBoton(btnLoopSongs,loopSong);
        actualizarEstiloBoton(btnShuffle, shuffle);
        actualizarEstiloBoton(btnLoop, loop);
        listener.onShuffleToggled(shuffle);
    }

    @FXML void onLoop(ActionEvent event) {
        loop = !loop;
        shuffle = false;
        actualizarEstiloBoton(btnLoop, loop);
        actualizarEstiloBoton(btnShuffle, false);
        listener.onLoopToggled(loop);
    }

    @FXML
    void onRebootSong(ActionEvent event) {
        stop = !stop;
        actualizarEstiloBoton(btnStop,stop);
        if(stop){
            actualizarEstiloBoton(btnLoopSongs,false);
            actualizarEstiloBoton(btnShuffle,false);
            actualizarEstiloBoton(btnLoop,false);

            shuffle = loop = loopSong = false;
        }
    }

    @FXML
    void onLoopSongs(ActionEvent event) {
        loopSong = !loopSong;
        actualizarEstiloBoton(btnLoopSongs,loopSong);
        actualizarEstiloBoton(btnLoop,false);
        loop = false;
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