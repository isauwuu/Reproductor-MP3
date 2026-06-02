package modelo.interfaces;

public interface ReproductorListener {
    void onPlay();
    void onNext();
    void onPrevious();
    void onShuffleToggled(boolean activo);
    void onLoopToggled(boolean activo);
    void onLoopSongCircle(boolean activo);
    void onStopSong();
}
