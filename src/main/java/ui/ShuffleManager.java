package ui;

import modelo.datos.ListaIndices;
import java.util.Random;

public class ShuffleManager {
    private ListaIndices colaShuffle = new ListaIndices();
    private int posEnCola = -1;

    public void generarCola(int cantidadCanciones, int posActual) {
        colaShuffle.limpiar();
        if (cantidadCanciones == 0) return;

        for (int i = 0; i < cantidadCanciones; i++) {
            colaShuffle.insertar(i, i);
        }

        fisherYates(cantidadCanciones, posActual);
        posEnCola = 0;
    }

    private void fisherYates(int tam, int posActual) {
        Random r = new Random();
        for (int i = tam - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            Integer valI = (Integer) colaShuffle.devolver(i);
            Integer valJ = (Integer) colaShuffle.devolver(j);
            colaShuffle.reemplazar(valJ, i);
            colaShuffle.reemplazar(valI, j);
        }

        // Ponemos la canción actual al frente
        int posDeLaActual = colaShuffle.buscar(posActual);
        if (posDeLaActual != -1 && posDeLaActual != 0) {
            Integer valFrente = (Integer) colaShuffle.devolver(0);
            colaShuffle.reemplazar(posActual, 0);
            colaShuffle.reemplazar(valFrente, posDeLaActual);
        }
    }

    public int siguiente(int cantidadCanciones, int posActual) {
        if (colaShuffle.estaVacia()) generarCola(cantidadCanciones, posActual);

        posEnCola++;
        if (posEnCola >= colaShuffle.tam()) {
            generarCola(cantidadCanciones, posActual);
            posEnCola = 1; // saltamos el 0 para no repetir la última canción
        }

        return (Integer) colaShuffle.devolver(posEnCola);
    }

    public int anterior(int posActual) {
        if (colaShuffle.estaVacia()) return posActual;

        posEnCola--;
        if (posEnCola < 0) posEnCola = 0;

        return (Integer) colaShuffle.devolver(posEnCola);
    }

    public ListaIndices getCola() {
        return colaShuffle;
    }

    public void limpiar() {
        colaShuffle.limpiar();
        posEnCola = -1;
    }
}