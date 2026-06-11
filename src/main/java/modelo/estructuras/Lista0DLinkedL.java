package modelo.estructuras;

import modelo.interfaces.OperacionesCL2;

/**
 * Estructura abstracta base de lista lineal que hereda del contrato {@link OperacionesCL2}.
 * Gestiona el enlace inicial (ini), final (fin), el tamaño (ult) y operaciones básicas de
 * eliminación, limpieza y recuperación de elementos.
 */
public abstract class Lista0DLinkedL implements OperacionesCL2 {
    protected NodoDoble ini, fin;
    protected int ult;

    /**
     * Constructor de la lista. Inicializa el estado vacío.
     */
    public Lista0DLinkedL(){
        limpiar();
    }

    /**
     * Limpia completamente la lista reiniciando las referencias a null y el contador ult a -1.
     */
    public void limpiar(){
        this.ini = this.fin = null;
        this.ult = -1;
    }

    /**
     * Comprueba si la lista no contiene ningún elemento.
     * 
     * @return true si la lista está vacía.
     */
    public boolean estaVacia(){
        return this.ini == null;
    }

    /**
     * Obtiene el tamaño físico de la lista.
     * 
     * @return Cantidad de elementos almacenados en la lista.
     */
    public int tam(){
        return this.ult + 1;
    }

    /**
     * Elimina el elemento ubicado en la posición (índice) indicada y ajusta las referencias.
     * 
     * @param pos Índice (0-based) del elemento a eliminar.
     */
    public void eliminar(int pos){
        if (estaVacia()) {
            System.out.println("Error eliminar. Lista vacia...");
        } else {
            if (pos >= tam() || pos < 0) {
                System.out.println("Error eliminar. Posicion inexistente ");
            } else {
                if (pos == 0) {
                    if (this.ini == this.fin) {
                        limpiar();
                    } else {
                        this.ini = this.ini.getNextNodo();
                        this.ini.setPrevNodo(null);
                        this.ult--;
                    }
                } else {
                    if (pos == tam() - 1) {
                        this.fin = this.fin.getPrevNodo();
                        this.fin.setNextNodo(null);
                    } else {
                        NodoDoble obj = this.ini;
                        for (int cont = 0; cont < pos; cont++) {
                            obj = obj.getNextNodo();
                        }
                        NodoDoble ant = obj.getPrevNodo();
                        NodoDoble sig = obj.getNextNodo();
                        ant.setNextNodo(sig);
                        sig.setPrevNodo(ant);
                    }
                    this.ult--;
                }
            }
        }
    }

    /**
     * Devuelve el objeto almacenado en la posición física indicada.
     * 
     * @param pos Índice (0-based) del elemento.
     * @return El objeto de datos, o null si el índice es inválido o la lista está vacía.
     */
    public Object devolver(int pos){
        Object elemento = null;
        if (estaVacia()) {
            System.out.println("Error al devolver, Lista vacia...");
        } else {
            if (pos >= tam() || pos < 0) {
                System.out.println("Error al devolver, La posicion es invalida");
            } else {
                NodoDoble act;
                act = this.ini;

                for (int cont = 0; cont < pos; cont++) {
                    act = act.getNextNodo();
                }
                elemento = act.getNodoInfo();
            }
        }
        return elemento;
    }

    /**
     * Busca la posición de un elemento en la lista.
     * Deberá ser implementado en base al tipo de lista.
     * 
     * @param elemento Objeto a buscar.
     * @return El índice del elemento, o -1 si no se encuentra.
     */
    public abstract int buscar(Object elemento);
}