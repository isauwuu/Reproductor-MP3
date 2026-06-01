package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import modelo.datos.ListaIndices;
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

    private MainController mainController;
    private boolean shuffle = false;
    private boolean loop    = false;

    // Cola de shuffle: lista de índices en orden aleatorio
    private ListaIndices colaShuffle = new ListaIndices();
    private int posEnCola = -1;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public boolean isShuffle() { return shuffle; }
    public boolean isLoop()    { return loop; }

    // -----------------------------------------------------------------------
    // EVENTOS
    // -----------------------------------------------------------------------

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
        shuffle = !shuffle;
        loop = false;

        actualizarEstiloBoton(btnShuffle, shuffle);
        actualizarEstiloBoton(btnLoop, false);

        if (shuffle) {
            generarColaShuffle();
        } else {
            colaShuffle.limpiar();
            posEnCola = -1;
        }
    }

    @FXML void onLoop(ActionEvent event) {
        loop = !loop;
        shuffle = false;

        actualizarEstiloBoton(btnLoop, loop);
        actualizarEstiloBoton(btnShuffle, false);

        colaShuffle.limpiar();
        posEnCola = -1;
    }

    // -----------------------------------------------------------------------
    // LÓGICA DE SHUFFLE
    // -----------------------------------------------------------------------

    /**
     * Genera una permutación aleatoria de índices usando Fisher-Yates
     * sobre nuestra ListaIndices (sin ninguna colección de Java).
     */
    public void generarColaShuffle() {
        colaShuffle.limpiar();
        int tam = mainController.getListaCancion().tam();
        if (tam == 0) return;

        // 1. Llenamos la lista con 0, 1, 2, ... tam-1
        for (int i = 0; i < tam; i++) {
            colaShuffle.insertar(i, i);
        }

        // 2. Fisher-Yates shuffle usando reemplazar() para intercambiar
        Random r = new Random();
        for (int i = tam - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            // intercambiar posición i y j
            Integer valI = (Integer) colaShuffle.devolver(i);
            Integer valJ = (Integer) colaShuffle.devolver(j);
            colaShuffle.reemplazar(valJ, i);
            colaShuffle.reemplazar(valI, j);
        }

        // 3. Ponemos la canción actual al frente para no repetirla de entrada
        int actualIdx = mainController.getActualPos();
        int posActual = colaShuffle.buscar(actualIdx);
        if (posActual != -1 && posActual != 0) {
            Integer valFrente = (Integer) colaShuffle.devolver(0);
            colaShuffle.reemplazar(actualIdx, 0);
            colaShuffle.reemplazar(valFrente, posActual);
        }

        posEnCola = 0; // apuntamos a la canción actual (pos 0)
    }

    /**
     * Avanza en la cola shuffle. Cuando se agota, genera una nueva permutación.
     */
    public int nextShufflePos() {
        if (colaShuffle.estaVacia()) generarColaShuffle();

        posEnCola++;
        if (posEnCola >= colaShuffle.tam()) {
            generarColaShuffle();
            posEnCola = 1; // saltamos el 0 para no repetir la última
        }

        return (Integer) colaShuffle.devolver(posEnCola);
    }

    /**
     * Retrocede en la cola shuffle.
     */
    public int previousShufflePos() {
        if (colaShuffle.estaVacia()) return mainController.getActualPos();

        posEnCola--;
        if (posEnCola < 0) posEnCola = 0;

        return (Integer) colaShuffle.devolver(posEnCola);
    }

    // -----------------------------------------------------------------------
    // UI HELPERS
    // -----------------------------------------------------------------------

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