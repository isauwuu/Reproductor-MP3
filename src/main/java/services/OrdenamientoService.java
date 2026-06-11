package services;

import modelo.criterios.CriterioOrdenacion;
import modelo.datos.Cancion;

/**
 * Servicio encargado de comparar dos objetos {@link Cancion} delegando
 * la lógica de comparación a un objeto {@link CriterioOrdenacion} (patrón Strategy).
 */
public class OrdenamientoService {

    private CriterioOrdenacion criterio;

    /**
     * Establece el criterio de comparación.
     * 
     * @param criterio El criterio de ordenamiento (Nombre, Artista, Año).
     */
    public void setCriterio(CriterioOrdenacion criterio) {
        this.criterio = criterio;
    }

    /**
     * Compara dos canciones basándose en el criterio establecido.
     * 
     * @param c1 Primera canción.
     * @param c2 Segunda canción.
     * @return 0 si son iguales, un valor menor a 0 si c1 es menor que c2, o mayor a 0 en caso contrario.
     */
    public int comparar(Cancion c1, Cancion c2) {
        return criterio.comparar(c1, c2);
    }
}