package modelo.estructuras;

import modelo.interfaces.OperacionesCL3;

/**
 * Clase abstracta que extiende {@link Lista0DLinkedL} e implementa {@link OperacionesCL3}.
 * Proporciona implementaciones de inserción en posiciones específicas, búsquedas secuenciales
 * y obtención/reemplazo directo de nodos.
 */
public abstract class Lista1DLinkedL extends Lista0DLinkedL implements OperacionesCL3 {

    /**
     * Busca secuencialmente un elemento en la lista.
     * Utiliza el método abstracto {@link #esIgual(Object, Object)} para comparar.
     * 
     * @param elemento Objeto a buscar.
     * @return El índice (0-based) o -1 si no se encuentra.
     */
    @Override
    public int buscar(Object elemento) {
        NodoDoble actual = this.ini;
        int posicion = 0;
        while (actual != null && !esIgual(actual.getNodoInfo(), elemento)) {
            actual = actual.getNextNodo();
            posicion++;
        }
        return (actual != null) ? posicion : -1;
    }

    /**
     * Inserta un elemento en una posición específica de la lista, enlazando
     * correctamente los punteros anteriores y siguientes de los nodos colindantes.
     * 
     * @param elemento El objeto de información a insertar.
     * @param pos      El índice de destino (0-based).
     * @throws IndexOutOfBoundsException Si la posición de destino está fuera de límites.
     */
    @Override
    public void insertar(Object elemento, int pos) {
        int size = tam();
        if (pos < 0 || pos > size) {
            throw new IndexOutOfBoundsException("Posición inválida");
        }
        if (pos == 0) {
            if (estaVacia()) {
                this.ini = this.fin = new NodoDoble(elemento);
            } else {
                NodoDoble nuevo = new NodoDoble(elemento, null, this.ini);
                this.ini.setPrevNodo(nuevo);
                this.ini = nuevo;
            }
        }
        else if (pos == size) {
            NodoDoble nuevo = new NodoDoble(elemento, this.fin, null);
            this.fin.setNextNodo(nuevo);
            this.fin = nuevo;
        }
        else {
            NodoDoble siguiente = obtenerNodo(pos);
            NodoDoble anterior = siguiente.getPrevNodo();
            NodoDoble nuevo = new NodoDoble(elemento, anterior, siguiente);
            anterior.setNextNodo(nuevo);
            siguiente.setPrevNodo(nuevo);
        }
        this.ult++;
    }

    /**
     * Inserta un elemento al final de la lista.
     * 
     * @param elemento Objeto a insertar.
     */
    public void insertar(Object elemento) {
        int size = tam();
        if (size == 0) this.ini = this.fin = new NodoDoble(elemento);
        else {
            this.fin.setNextNodo(new NodoDoble(elemento, this.fin, null));
            this.fin = this.fin.getNextNodo();
        }
        this.ult++;
    }

    /**
     * Indica si la lista está vacía.
     * 
     * @return true si la lista está vacía.
     */
    @Override
    public boolean estaVacia() {
        return super.estaVacia();
    }

    /**
     * Reemplaza la información de un nodo en la posición física indicada.
     * 
     * @param elemento Nuevo objeto de información.
     * @param pos      Índice de la posición.
     * @throws IllegalStateException Si la lista está vacía.
     */
    public void reemplazar(Object elemento, int pos) {
        if (estaVacia()) {
            throw new IllegalStateException("La lista está vacía");
        }
        NodoDoble nodo = obtenerNodo(pos);
        nodo.setNodoInfo(elemento);
    }

    /**
     * Devuelve el objeto {@link NodoDoble} en la posición lógica indicada.
     * Optimiza la búsqueda empezando desde el inicio (ini) o fin (fin) de acuerdo
     * a cuál de los extremos esté más cerca del índice solicitado.
     * 
     * @param pos Índice (0-based) del nodo solicitado.
     * @return El objeto NodoDoble.
     * @throws IndexOutOfBoundsException Si el índice está fuera del tamaño de la lista.
     */
    public NodoDoble obtenerNodo(int pos) {
        int size = tam();
        if (pos < 0 || pos >= size) {
            throw new IndexOutOfBoundsException("Posición inválida");
        }
        NodoDoble actual;
        if (pos < size / 2) {
            actual = this.ini;
            for (int i = 0; i < pos; i++) {
                actual = actual.getNextNodo();
            }
        }
        else {
            actual = this.fin;
            for (int i = size - 1; i > pos; i--) {
                actual = actual.getPrevNodo();
            }
        }
        return actual;
    }

    /**
     * Método abstracto para verificar si dos elementos son idénticos.
     * Debe ser implementado por las subclases.
     * 
     * @param a Primer objeto.
     * @param b Segundo objeto.
     * @return true si se consideran equivalentes.
     */
    public abstract boolean esIgual(Object a, Object b);
}