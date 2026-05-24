package modelo.datos;

import modelo.criterios.CriterioOrdenacion;
import modelo.estructuras.Lista2DLinkedL;
import services.OrdenamientoService;

public class ListaCancionOrdenada extends Lista2DLinkedL {
    OrdenamientoService comparador;
    public ListaCancionOrdenada(CriterioOrdenacion criterio){
        comparador = new OrdenamientoService();
        comparador.setCriterio(criterio);
    }

    @Override
    public boolean iguales(Object e1, Object e2) {
        return comparador.comparar((Cancion) e1,(Cancion) e2) == 0;
    }

    @Override
    public boolean esMenor(Object e1, Object e2) {
        return comparador.comparar((Cancion) e1,(Cancion) e2)<0;
    }

    @Override
    public boolean esMayor(Object e1, Object e2) {
        return comparador.comparar((Cancion) e1,(Cancion) e2)>0;
    }
}
