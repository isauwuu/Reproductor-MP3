package modelo.interfaces;

/**
 * Interfaz que define las acciones y eventos que responde el reproductor de audio,
 * permitiendo la comunicación entre la vista/controlador de reproducción y el motor de audio.
 */
public interface ReproductorListener {
    /**
     * Se ejecuta para alternar entre reproducir o pausar la pista actual.
     */
    void onPlay();

    /**
     * Se ejecuta para avanzar a la siguiente canción en la lista o cola de reproducción.
     */
    void onNext();

    /**
     * Se ejecuta para retroceder a la canción anterior en la lista o cola de reproducción.
     */
    void onPrevious();

    /**
     * Se ejecuta cuando se alterna el modo aleatorio (shuffle).
     * 
     * @param activo true si el shuffle se ha activado, false si se ha desactivado.
     */
    void onShuffleToggled(boolean activo);

    /**
     * Se ejecuta cuando se alterna el modo bucle general (reproducir toda la lista en bucle).
     * 
     * @param activo true si el bucle general está activado.
     */
    void onLoopToggled(boolean activo);

    /**
     * Se ejecuta cuando se alterna el modo bucle de una única canción.
     * 
     * @param activo true si el bucle de canción individual está activado.
     */
    void onLoopSongCircle(boolean activo);

    /**
     * Se ejecuta para detener la reproducción actual por completo.
     */
    void onStopSong();
}
