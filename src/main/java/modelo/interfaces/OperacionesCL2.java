package modelo.interfaces;

/**
 * Interfaz que define las operaciones de consulta, eliminación y vaciado
 * de elementos en una estructura lineal de datos.
 */
public interface OperacionesCL2 {
    /**
     * Busca secuencialmente la posición de un elemento en la estructura.
     * 
     * @param elemento Objeto a buscar.
     * @return El índice (0-based) si se encuentra, o -1 si no existe.
     */
    public int buscar(Object elemento);

    /**
     * Devuelve la información almacenada en una posición específica de la estructura.
     * 
     * @param pos Índice del elemento solicitado (0-based).
     * @return El objeto de información en la posición indicada.
     */
    public Object devolver(int pos);

    /**
     * Elimina el elemento ubicado en una posición específica.
     * 
     * @param posicion Índice del elemento a eliminar.
     */
    public void eliminar(int posicion);

    /**
     * Elimina todos los elementos y limpia la estructura de datos por completo.
     */
    public void limpiar();

    /**
     * Determina si la estructura de datos no contiene elementos.
     * 
     * @return true si la estructura está vacía, false en caso contrario.
     */
    public boolean estaVacia();

    /**
     * Obtiene el tamaño o número total de elementos contenidos en la estructura.
     * 
     * @return Entero con la cantidad de elementos.
     */
    public int tam();
}