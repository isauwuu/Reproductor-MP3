package modelo.criterios;

import modelo.datos.Cancion;

/**
 * Interfaz que define la estrategia para comparar dos objetos {@link Cancion}
 * bajo criterios polimórficos de ordenamiento.
 */
public interface CriterioOrdenacion {

    /**
     * Compara dos canciones.
     * 
     * @param c1 Primera canción.
     * @param c2 Segunda canción.
     * @return 0 si son iguales, un valor menor a 0 si c1 es menor que c2, o mayor a 0 si c1 es mayor que c2.
     */
    int comparar(Cancion c1, Cancion c2);
}
