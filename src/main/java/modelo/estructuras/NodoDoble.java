package modelo.estructuras;

/**
 * Representa un nodo de una lista doblemente enlazada.
 * Almacena la referencia a la información y enlaces al nodo anterior y siguiente.
 */
public class NodoDoble {
    private Object nodoInfo;
    private NodoDoble prevNodo, nextNodo;

    /**
     * Crea un nodo con información, sin enlaces.
     * 
     * @param nodoInfo La información a almacenar.
     */
    public NodoDoble(Object nodoInfo){
        this(nodoInfo, null, null);
    }

    /**
     * Crea un nodo con información y enlace al siguiente nodo.
     * 
     * @param nodoInfo La información a almacenar.
     * @param nextNodo Enlace al siguiente nodo.
     */
    public NodoDoble(Object nodoInfo, NodoDoble nextNodo){
        this(nodoInfo, null, nextNodo);
    }

    /**
     * Crea un nodo con información y enlaces al anterior y al siguiente.
     * 
     * @param nodoInfo La información a almacenar.
     * @param prevNodo Enlace al nodo anterior.
     * @param nextNodo Enlace al siguiente nodo.
     */
    public NodoDoble(Object nodoInfo, NodoDoble prevNodo, NodoDoble nextNodo){
        this.nodoInfo = nodoInfo;
        this.prevNodo = prevNodo;
        this.nextNodo = nextNodo;
    }

    /**
     * Establece la referencia al nodo anterior.
     * 
     * @param prevNodo El nodo anterior.
     */
    public void setPrevNodo(NodoDoble prevNodo){
        this.prevNodo = prevNodo;
    }

    /**
     * Obtiene la referencia al nodo anterior.
     * 
     * @return El nodo anterior.
     */
    public NodoDoble getPrevNodo(){
        return this.prevNodo;
    }

    /**
     * Establece la referencia al siguiente nodo.
     * 
     * @param nextNodo El siguiente nodo.
     */
    public void setNextNodo(NodoDoble nextNodo){
        this.nextNodo = nextNodo;
    }

    /**
     * Obtiene la referencia al siguiente nodo.
     * 
     * @return El siguiente nodo.
     */
    public NodoDoble getNextNodo(){
        return this.nextNodo;
    }

    /**
     * Establece la información contenida en el nodo.
     * 
     * @param nodoInfo Objeto de información.
     */
    public void setNodoInfo(Object nodoInfo){
        this.nodoInfo = nodoInfo;
    }

    /**
     * Obtiene la información contenida en el nodo.
     * 
     * @return El objeto de información.
     */
    public Object getNodoInfo(){
        return this.nodoInfo;
    }
}
