package services;

import modelo.datos.ListaIndices;
import java.util.Random;

public class ShuffleManager {

    private final ListaIndices cola = new ListaIndices();
    private final Random random = new Random();
    private int posEnCola = -1;

    public void generarCola(int cantidadCanciones, int posActual) {
        cola.limpiar();
        posEnCola = -1;
        if (cantidadCanciones == 0) return;

        for (int i = 0; i < cantidadCanciones; i++)
            cola.insertar(i, i);

        fisherYates(cantidadCanciones);
        ponerAlFrente(posActual);
        posEnCola = 0;
    }

    private void fisherYates(int tam) {
        for (int i = tam - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Integer valI = (Integer) cola.devolver(i);
            Integer valJ = (Integer) cola.devolver(j);
            cola.reemplazar(valJ, i);
            cola.reemplazar(valI, j);
        }
    }

    private void ponerAlFrente(int posActual) {
        int posDeLaActual = cola.buscar(posActual);
        if (posDeLaActual > 0) {
            Integer valFrente = (Integer) cola.devolver(0);
            cola.reemplazar(posActual, 0);
            cola.reemplazar(valFrente, posDeLaActual);
        }
    }

    public int siguiente(int cantidadCanciones, int posActual) {
        if (cola.estaVacia()) generarCola(cantidadCanciones, posActual);
        posEnCola++;
        if (posEnCola >= cola.tam()) {
            generarCola(cantidadCanciones, posActual);
            posEnCola = 1; // evita repetir la última canción al regenerar
        }
        return (Integer) cola.devolver(posEnCola);
    }

    public int anterior(int posActual) {
        if (cola.estaVacia()) return posActual;
        if (posEnCola > 0) posEnCola--;
        return (Integer) cola.devolver(posEnCola);
    }

    public boolean estaVacia() {
        return cola.estaVacia();
    }

    public ListaIndices getCola() {
        return cola;
    }

    public void limpiar() {
        cola.limpiar();
        posEnCola = -1;
    }
}