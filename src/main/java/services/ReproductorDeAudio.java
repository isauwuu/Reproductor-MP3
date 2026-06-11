package services;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import modelo.datos.Cancion;

/**
 * Servicio encargado de gestionar el ciclo de reproducción de los archivos MP3.
 * Encapsula la funcionalidad del componente {@link MediaPlayer} de JavaFX y expone propiedades reactivas
 * para el tiempo actual, tiempo total y el estado de la reproducción.
 */
public class ReproductorDeAudio {
    private MediaPlayer mediaPlayer;

    private final ObjectProperty<Duration> tiempoActual = new SimpleObjectProperty<>(Duration.ZERO);
    private final ObjectProperty<Duration> tiempoTotal = new SimpleObjectProperty<>(Duration.ZERO);
    private final ObjectProperty<MediaPlayer.Status> estado = new SimpleObjectProperty<>(MediaPlayer.Status.UNKNOWN);

    private Runnable finalizaCancion;

    /**
     * Constructor por defecto del reproductor de audio.
     */
    public ReproductorDeAudio() {}

    /**
     * Establece la acción que debe ejecutarse automáticamente cuando finaliza una canción.
     * 
     * @param accion Objeto Runnable conteniendo la lógica de cambio de pista.
     */
    public void setFinalizaCancion(Runnable accion) {
        this.finalizaCancion = accion;
    }

    /**
     * Carga y reproduce un nuevo archivo de audio a partir de la URI de la canción especificada.
     * Libera previamente cualquier recurso ocupado.
     * 
     * @param cancion Canción a reproducir.
     */
    public void reproducirNueva(Cancion cancion) {
        if (cancion == null || !cancion.isValida())
            return;
        limpiarMotor();
        Media media = new Media(cancion.getMediaURI());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            tiempoTotal.set(mediaPlayer.getTotalDuration());
        });
        mediaPlayer.currentTimeProperty().addListener((obs, viejo, nuevo) -> {
            tiempoActual.set(nuevo);
        });
        mediaPlayer.statusProperty().addListener((obs, viejo, nuevo) -> {
            estado.set(nuevo);
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            if (this.finalizaCancion != null) {
                this.finalizaCancion.run();
            }
        });
        mediaPlayer.play();
    }

    /**
     * Alterna entre reproducción y pausa de la pista cargada.
     */
    public void alternarPausaReproduccion() {
        if (mediaPlayer == null)
            return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }

    /**
     * Cambia la posición actual de reproducción a un porcentaje específico del total de la pista.
     * 
     * @param porcentaje Valor entre 0.0 y 1.0 indicando el punto de destino.
     */
    public void adelantar(double porcentaje) {
        if (mediaPlayer != null && tiempoTotal.get() != null) {
            double segundosTotales = tiempoTotal.get().toSeconds();
            mediaPlayer.seek(Duration.seconds(porcentaje * segundosTotales));
        }
    }

    /**
     * Detiene y libera los recursos del motor del MediaPlayer actual, reseteando las propiedades de tiempo.
     */
    private void limpiarMotor() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            tiempoActual.set(Duration.ZERO);
            tiempoTotal.set(Duration.ZERO);
        }
    }

    /**
     * Detiene la reproducción de la canción actual y rebobina el tiempo transcurrido a cero.
     */
    public void detener() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.seek(Duration.ZERO);
            tiempoActual.set(Duration.ZERO);
        }
    }

    /**
     * Libera el reproductor y establece el MediaPlayer a null.
     */
    public void liberar() {
        limpiarMotor();
        mediaPlayer = null;
    }

    /**
     * Comprueba si hay algún archivo de audio cargado en el reproductor.
     * 
     * @return true si tiene un objeto MediaPlayer asignado.
     */
    public boolean tieneMedia() {
        return mediaPlayer != null;
    }

    /**
     * Obtiene la propiedad observable del tiempo transcurrido actual de reproducción.
     * 
     * @return Propiedad reactiva de tipo {@link ObjectProperty}.
     */
    public ObjectProperty<Duration> tiempoActualProperty() {
        return this.tiempoActual;
    }

    /**
     * Obtiene la propiedad observable de la duración total de la pista activa.
     * 
     * @return Propiedad reactiva de tipo {@link ObjectProperty}.
     */
    public ObjectProperty<Duration> tiempoTotalProperty() {
        return this.tiempoTotal;
    }

    /**
     * Obtiene la propiedad observable del estado actual de la reproducción (PLAYING, PAUSED, etc).
     * 
     * @return Propiedad reactiva de tipo {@link ObjectProperty}.
     */
    public ObjectProperty<MediaPlayer.Status> estadoProperty() {
        return this.estado;
    }
}
