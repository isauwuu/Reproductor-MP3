package modelo.datos;

import modelo.criterios.CriterioOrdenacion;
import modelo.estructuras.Lista2DLinkedL;

/**
 * Representa una lista ordenada de canciones.
 * Extiende {@link Lista2DLinkedL} y delega la comparación directamente
 * al {@link CriterioOrdenacion} configurado.
 */
public class ListaCancionOrdenada extends Lista2DLinkedL {
    private CriterioOrdenacion criterio;

    /**
     * Crea una lista ordenada de canciones en base al criterio especificado.
     *
     * @param criterio El criterio de ordenamiento.
     */
    public ListaCancionOrdenada(CriterioOrdenacion criterio) {
        this.criterio = criterio;
    }

    /**
     * Compara si dos canciones son iguales según el criterio establecido.
     */
    @Override
    public boolean iguales(Object e1, Object e2) {
        return criterio.comparar((Cancion) e1, (Cancion) e2) == 0;
    }

    /**
     * Compara si una canción es menor que otra según el criterio.
     */
    @Override
    public boolean esMenor(Object e1, Object e2) {
        return criterio.comparar((Cancion) e1, (Cancion) e2) < 0;
    }

    /**
     * Compara si una canción es mayor que otra según el criterio.
     */
    @Override
    public boolean esMayor(Object e1, Object e2) {
        return criterio.comparar((Cancion) e1, (Cancion) e2) > 0;
    }
}