package modelo.criterios;

import modelo.datos.Cancion;

/**
 * Criterio de ordenación para clasificar canciones cronológicamente según su año de lanzamiento.
 * Implementa la interfaz {@link CriterioOrdenacion}.
 */
public class PorAnio implements CriterioOrdenacion {

    /**
     * Compara dos canciones por su año.
     * 
     * @param c1 Primera canción.
     * @param c2 Segunda canción.
     * @return 0 si tienen el mismo año, 1 si c1 es más reciente que c2, o -1 en caso contrario.
     */
    @Override
    public int comparar(Cancion c1, Cancion c2) {
        return c1.getAnio() == c2.getAnio() ? 0 : c1.getAnio() > c2.getAnio() ? 1 : -1;
    }
}
