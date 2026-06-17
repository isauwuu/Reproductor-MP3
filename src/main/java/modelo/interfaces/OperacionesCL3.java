package modelo.interfaces;

/**
 * Interfaz que define las operaciones de inserción y reemplazo indexados
 * en una estructura lineal de datos.
 */
public interface OperacionesCL3 {
    /**
     * Inserta un elemento en una posición física específica, desplazando los elementos siguientes.
     * 
     * @param elemento El objeto de información a insertar.
     * @param posicion El índice de destino (0-based).
     */
    public void insertar(Object elemento, int posicion);

    /**
     * Reemplaza el objeto de información ubicado en la posición física indicada.
     * 
     * @param elemento El nuevo objeto de información.
     * @param posicion El índice del elemento a reemplazar.
     */
    public void reemplazar(Object elemento, int posicion);
}
