package modelo.datos;

import modelo.estructuras.Lista1DLinkedL;
import modelo.estructuras.NodoDoble;

public class ListaCancion extends Lista1DLinkedL {

    @Override
    public boolean esIgual(Object a, Object b) {
        a = (Cancion)a;
        b = (Cancion)b;
        boolean c1 = ((Cancion) a).getAnio() == ((Cancion) b).getAnio();
        boolean c2 = ((Cancion) a).getArtista().equals(((Cancion) b).getArtista());
        boolean c3 = ((Cancion) a).getTitulo().equals(((Cancion) b).getTitulo());
        return c1 && c2 && c3;
    }

    public NodoDoble getPrimero(){
        return this.ini;
    }
}
