package modelo.datos;

import modelo.estructuras.Lista1DLinkedL;

/**
 * Lista de índices enteros para la cola de shuffle.
 * Extiende Lista1DLinkedL con igualdad por valor entero.
 */
public class ListaIndices extends Lista1DLinkedL {

    /**
     * Compara dos elementos de tipo Integer para verificar si son iguales.
     * 
     * @param a Primer objeto (Integer).
     * @param b Segundo objeto (Integer).
     * @return true si ambos enteros son iguales, false en caso contrario.
     */
    @Override
    public boolean esIgual(Object a, Object b) {
        if (a == null || b == null) return false;
        return ((Integer) a).equals((Integer) b);
    }
}