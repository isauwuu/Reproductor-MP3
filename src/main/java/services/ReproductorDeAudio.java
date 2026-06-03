package services;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import modelo.datos.Cancion;


//Su única responsabilidad es manejar la decodificación del archivo MP3 y avisar
// al controlador cuando el tiempo o el estado cambian.
public class ReproductorDeAudio {
    private MediaPlayer mediaPlayer;

    // variables q emiten un aviso automático cada vez que su valor cambia (son observadas podria decirse(?)
    //En lugar de variables comunes (ej. Duration tiempoActual), usamos ObjectProperty.
    // SimpleObjectProperty es la implementación concreta que envuelve al dato.
    // Al inicializarlas con Duration.ZERO y MediaPlayer.Status.UNKNOWN, estamos creando
    // la "caja" con un valor por defecto, lista para gritar cuando su contenido cambie.
    private final ObjectProperty<Duration> tiempoActual = new SimpleObjectProperty<>(Duration.ZERO);
    private final ObjectProperty<Duration> tiempoTotal = new SimpleObjectProperty<>(Duration.ZERO);
    private final ObjectProperty<MediaPlayer.Status> estado = new SimpleObjectProperty<>(MediaPlayer.Status.UNKNOWN);

    // Esta variable(la Runnable) no guarda un dato, guarda una ACCIÓN (un bloque de código).
    // El Controlador va a meter código acá adentro, y esta clase solo se va a
    // limitar a ejecutarlo (.run()) cuando la pista termine.
    private Runnable finalizaCancion;

    public ReproductorDeAudio() {}

    public void setFinalizaCancion(Runnable accion){
        this.finalizaCancion=accion;
    }
    public void reproducirNueva(Cancion cancion) {
        if (cancion == null || !cancion.isValida())
            return;
        limpiarMotor();
        Media media = new Media(cancion.getMediaURI());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> {
            tiempoTotal.set(mediaPlayer.getTotalDuration());});
        //el getTotalDuration es el equivalente a getDuracionEnSegundos de la clase cancion pero es mil veces mas preciso(milisegundos), es el q hay q usar
        // en el Slider (el de la clase cancion puede usarse para mostrar en pantalla de manera estatica
        mediaPlayer.currentTimeProperty().addListener((obs, viejo, nuevo) -> {
            //si el tiempo llega a cambiar, se ejecuta esto
            tiempoActual.set(nuevo);
        });
        mediaPlayer.statusProperty().addListener((obs, viejo, nuevo) -> {
            estado.set(nuevo);
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            // se ejecuta cuando termina la cancion
            if (this.finalizaCancion != null) {
                this.finalizaCancion.run();
            }
        });
        mediaPlayer.play();
    }
    public void alternarPausaReproduccion() {
        if (mediaPlayer == null)
            return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.play();
        }
    }
    public void adelantar(double porcentaje) {
        if (mediaPlayer != null && tiempoTotal.get() != null) {
            double segundosTotales = tiempoTotal.get().toSeconds();
            mediaPlayer.seek(Duration.seconds(porcentaje * segundosTotales));
        }
    }
    private void limpiarMotor() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            // Reseteamos las cajas a cero para limpiar la pantalla entre canciones
            tiempoActual.set(Duration.ZERO);
            tiempoTotal.set(Duration.ZERO);
        }
    }
    public void detener() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.seek(Duration.ZERO);
            tiempoActual.set(Duration.ZERO);
        }
    }
    public ObjectProperty<Duration> tiempoActualProperty() {
        return this.tiempoActual;
    }

    public ObjectProperty<Duration> tiempoTotalProperty() {
        return this.tiempoTotal;
    }

    public ObjectProperty<MediaPlayer.Status> estadoProperty() {
        return this.estado;
    }
}
