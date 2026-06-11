package modelo.criterios;

import modelo.datos.Cancion;

/**
 * Criterio de ordenación para clasificar canciones alfabéticamente por su título.
 * Implementa la interfaz {@link CriterioOrdenacion}.
 */
public class PorNombre implements CriterioOrdenacion {

    /**
     * Compara dos canciones por su título alfabéticamente (ignorando mayúsculas y minúsculas).
     * 
     * @param c1 Primera canción.
     * @param c2 Segunda canción.
     * @return Entero menor a 0 si c1 es anterior alfabéticamente, 0 si son idénticos, o mayor a 0 en caso contrario.
     */
    @Override
    public int comparar(Cancion c1, Cancion c2) {
        return c1.getTitulo().compareToIgnoreCase(c2.getTitulo());
    }
}
