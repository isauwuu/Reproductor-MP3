package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import modelo.interfaces.ReproductorListener;

/**
 * Controlador de la barra de controles de reproducción de audio.
 * Maneja los botones de Play/Pausa, Detener, Shuffle, Loop, y la barra de progreso deslizadora.
 */
public class ControlesController {
    @FXML private Button btnPlayPause;
    @FXML private Button btnShuffle;
    @FXML private Button btnLoop;
    @FXML private Button btnStop;
    @FXML private Label lblSongTitle;
    @FXML private Label lblSongArtist;
    @FXML private Label lblTimeCurrent;
    @FXML private Label lblTimeTotal;
    @FXML private Slider progressSlider;

    private ReproductorListener listener;
    private boolean shuffle = false;
    private boolean loop    = false;
    private boolean userIsInteracting = false;

    /**
     * Inicializa el slider y configura los eventos de click y arrastre para
     * cambiar instantáneamente la posición de reproducción por coordenadas X.
     */
    @FXML
    public void initialize() {
        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double percentage = newVal.doubleValue();
            javafx.scene.Node track = progressSlider.lookup(".track");
            if (track != null) {
                track.setStyle("-fx-background-color: linear-gradient(to right, -acento 0%, -acento " 
                    + percentage + "%, -borde " + percentage + "%, -borde 100%);");
            }
        });

        progressSlider.setOnMousePressed(event -> {
            userIsInteracting = true;
            actualizarValorPorCoordenada(event.getX());
        });

        progressSlider.setOnMouseDragged(event -> {
            actualizarValorPorCoordenada(event.getX());
        });
    }

    /**
     * Calcula la posición del slider basándose en la coordenada X del cursor.
     * 
     * @param x Coordenada X relativa al Slider.
     */
    private void actualizarValorPorCoordenada(double x) {
        double width = progressSlider.getWidth();
        if (width <= 0) return;
        double min = progressSlider.getMin();
        double max = progressSlider.getMax();
        double newValue = (x / width) * (max - min) + min;
        if (newValue < min) newValue = min;
        if (newValue > max) newValue = max;
        progressSlider.setValue(newValue);
    }

    /**
     * Establece el listener de eventos de reproducción.
     * 
     * @param l Listener de tipo {@link ReproductorListener}.
     */
    public void setListener(ReproductorListener l) { this.listener = l; }

    /**
     * Indica si el modo shuffle está activo.
     * 
     * @return true si shuffle está encendido.
     */
    public boolean isShuffle() { return shuffle; }

    /**
     * Indica si el modo loop está activo.
     * 
     * @return true si loop está encendido.
     */
    public boolean isLoop()    { return loop; }

    @FXML void onPlayPause(ActionEvent event) { if (listener != null) listener.onPlay(); }
    @FXML void onNext(ActionEvent event) { if (listener != null) listener.onNext(); }
    @FXML void onPrevious(ActionEvent event) { if (listener != null) listener.onPrevious(); }

    /**
     * Evento ejecutado al pulsar el botón Shuffle. Alterna el modo shuffle
     * y desactiva el modo loop si estaba encendido.
     * 
     * @param event Evento de JavaFX.
     */
    @FXML void onShuffle(ActionEvent event) {
        shuffle = !shuffle;
        if (shuffle && loop) {
            loop = false;
            actualizarEstiloBoton(btnLoop, false);
            listener.onLoopToggled(false);
        }
        actualizarEstiloBoton(btnShuffle, shuffle);
        listener.onShuffleToggled(shuffle);
    }

    /**
     * Evento ejecutado al pulsar el botón Loop. Alterna el modo loop
     * y desactiva el modo shuffle si estaba encendido.
     * 
     * @param event Evento de JavaFX.
     */
    @FXML void onLoop(ActionEvent event) {
        loop = !loop;
        if (loop && shuffle) {
            shuffle = false;
            actualizarEstiloBoton(btnShuffle, false);
            listener.onShuffleToggled(false);
        }
        actualizarEstiloBoton(btnLoop, loop);
        listener.onLoopToggled(loop);
    }

    /**
     * Restablece el valor de la barra de progreso a cero.
     */
    public void resetearProgreso() {
        progressSlider.setValue(0);
    }

    /**
     * Determina si el usuario está interactuando activamente con el slider.
     * 
     * @return true si el slider está siendo modificado por el usuario.
     */
    public boolean isSliderCambiando() {
        return progressSlider.isValueChanging() || userIsInteracting;
    }

    /**
     * Establece el porcentaje actual de progreso del slider de reproducción.
     * 
     * @param porcentaje Valor de 0.0 a 100.0.
     */
    public void setProgreso(double porcentaje) {
        progressSlider.setValue(porcentaje);
    }

    /**
     * Establece la acción a ejecutar cuando el usuario suelta el slider tras un arrastre.
     * 
     * @param accion Runnable con la acción a ejecutar.
     */
    public void setOnProgresoSoltado(Runnable accion) {
        progressSlider.setOnMouseReleased(e -> {
            actualizarValorPorCoordenada(e.getX());
            accion.run();
            userIsInteracting = false;
        });
    }

    /**
     * Actualiza el estilo visual del botón para reflejar si está activo o inactivo.
     * 
     * @param btn    Botón a modificar.
     * @param activo true si se debe activar el botón.
     */
    private void actualizarEstiloBoton(Button btn, boolean activo) {
        if (btn == null) return;
        if (activo) {
            if (!btn.getStyleClass().contains("pixel-button-active"))
                btn.getStyleClass().add("pixel-button-active");
        } else {
            btn.getStyleClass().remove("pixel-button-active");
        }
    }

    /**
     * Cambia el texto o icono visual del botón de reproducción.
     * 
     * @param texto Texto a establecer (ej: "▶" o "||").
     */
    public void cambiarTextoBotonPlay(String texto) { btnPlayPause.setText(texto); }

    /**
     * Actualiza el título y artista del tema actual en la barra de controles.
     * 
     * @param titulo  Título de la canción.
     * @param artista Nombre del artista.
     */
    public void actualizarTextos(String titulo, String artista) {
        lblSongTitle.setText(titulo);
        lblSongArtist.setText(artista);
    }

    /**
     * Actualiza las etiquetas de tiempo actual y total de la canción en reproducción.
     * 
     * @param actual Tiempo transcurrido (ej: "1:42").
     * @param total  Tiempo total (ej: "3:14").
     */
    public void actualizarTiempos(String actual, String total) {
        lblTimeCurrent.setText(actual);
        lblTimeTotal.setText(total);
    }

    /**
     * Evento ejecutado al pulsar el botón de detener la reproducción.
     * 
     * @param event Evento de JavaFX.
     */
    @FXML
    void onStopSong(ActionEvent event) {
        if (listener != null) listener.onStopSong();
    }

    /**
     * Obtiene el valor actual de progreso del slider.
     * 
     * @return El progreso en formato double.
     */
    public double getProgreso() {
        return progressSlider.getValue();
    }
}