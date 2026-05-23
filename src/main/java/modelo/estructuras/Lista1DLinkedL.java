package modelo.estructuras;

import modelo.interfaces.OperacionesCL3;

public abstract class Lista1DLinkedL extends Lista0DLinkedL implements OperacionesCL3 {

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

    @Override
    public boolean estaVacia() {
        return super.estaVacia();
    }

    public void reemplazar(Object elemento, int pos) {
        if (estaVacia()) {
            throw new IllegalStateException("La lista está vacía");
        }
        NodoDoble nodo = obtenerNodo(pos);
        nodo.setNodoInfo(elemento);
    }

    private NodoDoble obtenerNodo(int pos) {
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

    public abstract boolean esIgual(Object a, Object b);
}