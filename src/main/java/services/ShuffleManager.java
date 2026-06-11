package services;

import modelo.datos.ListaIndices;
import java.util.Random;

/**
 * Administra el orden aleatorio (shuffle) para la reproducción de canciones.
 * Utiliza el algoritmo Fisher-Yates para barajar los índices y mantiene el estado de reproducción
 * de la cola aleatoria actual en una estructura {@link ListaIndices}.
 */
public class ShuffleManager {

    private final ListaIndices cola = new ListaIndices();
    private final Random random = new Random();
    private int posEnCola = -1;

    /**
     * Genera una nueva cola de reproducción aleatoria de tamaño especificado,
     * ubicando la canción actualmente activa en la primera posición de la cola.
     * 
     * @param cantidadCanciones Número total de canciones de la playlist.
     * @param posActual         Posición en la playlist de la canción actualmente activa.
     */
    public void generarCola(int cantidadCanciones, int posActual) {
        cola.limpiar();
        posEnCola = -1;
        if (cantidadCanciones == 0) return;

        // Rellenar con los índices secuenciales
        for (int i = 0; i < cantidadCanciones; i++)
            cola.insertar(i, i);

        // Barajar usando Fisher-Yates
        fisherYates(cantidadCanciones);
        // Garantizar que la canción que ya está sonando quede al principio
        ponerAlFrente(posActual);
        posEnCola = 0;
    }

    /**
     * Algoritmo Fisher-Yates para desordenar aleatoriamente los elementos de la cola.
     * 
     * @param tam Tamaño de la cola.
     */
    private void fisherYates(int tam) {
        for (int i = tam - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Integer valI = (Integer) cola.devolver(i);
            Integer valJ = (Integer) cola.devolver(j);
            cola.reemplazar(valJ, i);
            cola.reemplazar(valI, j);
        }
    }

    /**
     * Intercambia el índice de la canción actual para que quede en el índice 0 de la cola barajada.
     * 
     * @param posActual Posición física de la canción actual.
     */
    private void ponerAlFrente(int posActual) {
        int posDeLaActual = cola.buscar(posActual);
        if (posDeLaActual > 0) {
            Integer valFrente = (Integer) cola.devolver(0);
            cola.reemplazar(posActual, 0);
            cola.reemplazar(valFrente, posDeLaActual);
        }
    }

    /**
     * Obtiene el índice de la siguiente canción a reproducir en el orden aleatorio.
     * Si la cola es completada o está vacía, se vuelve a generar.
     * 
     * @param cantidadCanciones Cantidad de canciones de la playlist.
     * @param posActual         Índice de la canción actual.
     * @return El índice de la siguiente canción.
     */
    public int siguiente(int cantidadCanciones, int posActual) {
        if (cola.estaVacia()) generarCola(cantidadCanciones, posActual);
        posEnCola++;
        if (posEnCola >= cola.tam()) {
            generarCola(cantidadCanciones, posActual);
            posEnCola = 1; // Evita repetir de inmediato la última canción barajada al regenerar
        }
        return (Integer) cola.devolver(posEnCola);
    }

    /**
     * Obtiene el índice de la canción anterior en el orden aleatorio.
     * 
     * @param posActual Índice de la canción actual.
     * @return El índice de la canción anterior.
     */
    public int anterior(int posActual) {
        if (cola.estaVacia()) return posActual;
        if (posEnCola > 0) posEnCola--;
        return (Integer) cola.devolver(posEnCola);
    }

    /**
     * Obtiene si la cola aleatoria está vacía.
     * 
     * @return true si la cola de reproducción está vacía.
     */
    public boolean estaVacia() {
        return cola.estaVacia();
    }

    /**
     * Obtiene el objeto de lista interna que representa la cola.
     * 
     * @return La lista de índices barajados.
     */
    public ListaIndices getCola() {
        return cola;
    }

    /**
     * Limpia la cola aleatoria y reinicia el estado.
     */
    public void limpiar() {
        cola.limpiar();
        posEnCola = -1;
    }
}