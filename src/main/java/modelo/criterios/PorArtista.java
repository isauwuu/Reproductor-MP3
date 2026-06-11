package modelo.criterios;

import modelo.datos.Cancion;

/**
 * Criterio de ordenación para clasificar canciones alfabéticamente por el nombre del artista o banda.
 * Implementa la interfaz {@link CriterioOrdenacion}.
 */
public class PorArtista implements CriterioOrdenacion {

    /**
     * Compara dos canciones por el nombre de su artista alfabéticamente (ignorando mayúsculas y minúsculas).
     * 
     * @param c1 Primera canción.
     * @param c2 Segunda canción.
     * @return Entero menor a 0 si c1 es anterior alfabéticamente, 0 si son idénticos, o mayor a 0 en caso contrario.
     */
    @Override
    public int comparar(Cancion c1, Cancion c2) {
        return c1.getArtista().compareToIgnoreCase(c2.getArtista());
    }
}
