package modelo.datos;

import modelo.criterios.CriterioOrdenacion;
import modelo.estructuras.Lista2DLinkedL;
import services.OrdenamientoService;

/**
 * Representa una lista ordenada de canciones.
 * Extiende {@link Lista2DLinkedL} y utiliza un {@link OrdenamientoService}
 * configurado con un {@link CriterioOrdenacion} para realizar la inserción ordenada.
 */
public class ListaCancionOrdenada extends Lista2DLinkedL {
    private OrdenamientoService comparador;
    private CriterioOrdenacion criterio;

    /**
     * Crea una lista ordenada de canciones en base al criterio especificado.
     * 
     * @param criterio El criterio de ordenamiento.
     */
    public ListaCancionOrdenada(CriterioOrdenacion criterio){
        comparador = new OrdenamientoService();
        comparador.setCriterio(criterio);
        this.criterio = criterio;
    }

    /**
     * Compara si dos canciones son iguales según el criterio establecido.
     * 
     * @param e1 Primera canción.
     * @param e2 Segunda canción.
     * @return true si son iguales.
     */
    @Override
    public boolean iguales(Object e1, Object e2) {
        return comparador.comparar((Cancion) e1, (Cancion) e2) == 0;
    }

    /**
     * Compara si una canción es menor que otra según el criterio.
     * 
     * @param e1 Primera canción.
     * @param e2 Segunda canción.
     * @return true si e1 es menor que e2.
     */
    @Override
    public boolean esMenor(Object e1, Object e2) {
        return comparador.comparar((Cancion) e1, (Cancion) e2) < 0;
    }

    /**
     * Compara si una canción es mayor que otra según el criterio.
     * 
     * @param e1 Primera canción.
     * @param e2 Segunda canción.
     * @return true si e1 es mayor que e2.
     */
    @Override
    public boolean esMayor(Object e1, Object e2) {
        return comparador.comparar((Cancion) e1, (Cancion) e2) > 0;
    }
}
