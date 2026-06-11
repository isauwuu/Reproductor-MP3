package modelo.estructuras;

import modelo.interfaces.OperacionesCL4;

/**
 * Clase abstracta que extiende {@link Lista0DLinkedL} e implementa {@link OperacionesCL4}.
 * Proporciona soporte para una lista con inserción ordenada automática basada en comparaciones
 * relativas de menor, mayor o iguales.
 */
public abstract class Lista2DLinkedL extends Lista0DLinkedL implements OperacionesCL4 {

    /**
     * Inserta un elemento en la lista en la posición ordenada correspondiente.
     * Busca secuencialmente el lugar adecuado comparándolo con los elementos existentes.
     * 
     * @param elemento Objeto a insertar de forma ordenada.
     */
    public void insertar(Object elemento) {
        if (estaVacia()) {
            this.ini = this.fin = new NodoDoble(elemento);
        } else {
            if (esMenor(elemento, this.ini.getNodoInfo())) {
                this.ini = new NodoDoble(elemento, null, this.ini);
                this.ini.getNextNodo().setPrevNodo(this.ini);
            } else {
                if (esMayor(elemento, this.fin.getNodoInfo()) || iguales(elemento, this.fin.getNodoInfo())) {
                    this.fin.setNextNodo(new NodoDoble(elemento, this.fin, null));
                    this.fin = this.fin.getNextNodo();
                } else {
                    NodoDoble aux = this.ini.getNextNodo();
                    while (esMayor(elemento, aux.getNodoInfo()) || iguales(elemento, aux.getNodoInfo())) {
                        aux = aux.getNextNodo();
                    }
                    NodoDoble nuevo = new NodoDoble(elemento, aux.getPrevNodo(), aux);
                    aux.getPrevNodo().setNextNodo(nuevo);
                    aux.setPrevNodo(nuevo);
                }
            }
        }
        this.ult++;
    }

    /**
     * Busca la posición lógica del elemento en la lista ordenada de forma optimizada.
     * 
     * @param elemento El elemento a buscar.
     * @return El índice (0-based) o -1 si no se encuentra.
     */
    public int buscar(Object elemento) {
        if (estaVacia())
            return -1;
        if (iguales(elemento, this.ini.getNodoInfo()))
            return 0;
        if (iguales(elemento, this.fin.getNodoInfo()))
            return this.tam() - 1;
        else {
            NodoDoble act = this.ini.getNextNodo();
            int c = 1;
            while (act != null && !iguales(elemento, act.getNodoInfo()) && esMenor(act.getNodoInfo(), elemento)) {
                act = act.getNextNodo();
                c++;
            }
            if (act != null && iguales(elemento, act.getNodoInfo())) {
                return c;
            } else {
                return -1;
            }
        }
    }

    /**
     * Compara si dos elementos son iguales.
     * 
     * @param e1 Primer elemento.
     * @param e2 Segundo elemento.
     * @return true si son iguales.
     */
    public abstract boolean iguales(Object e1, Object e2);

    /**
     * Compara si el primer elemento es menor que el segundo.
     * 
     * @param e1 Primer elemento.
     * @param e2 Segundo elemento.
     * @return true si e1 es menor que e2.
     */
    public abstract boolean esMenor(Object e1, Object e2);

    /**
     * Compara si el primer elemento es mayor que el segundo.
     * 
     * @param e1 Primer elemento.
     * @param e2 Segundo elemento.
     * @return true si e1 es mayor que e2.
     */
    public abstract boolean esMayor(Object e1, Object e2);
}
