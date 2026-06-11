package modelo.datos;

import modelo.criterios.CriterioOrdenacion;
import modelo.estructuras.Lista1DLinkedL;
import modelo.estructuras.NodoDoble;

/**
 * Representa una lista lineal de canciones (playlist) que hereda de una lista simple enlazada
 * e implementa la ordenación física utilizando el patrón Strategy.
 */
public class ListaCancion extends Lista1DLinkedL {

    /**
     * Compara dos canciones por su URI multimedia única para determinar su igualdad.
     * 
     * @param a Primer objeto canción.
     * @param b Segundo objeto canción.
     * @return true si los archivos corresponden a la misma URI en disco.
     */
    @Override
    public boolean esIgual(Object a, Object b) {
        if (a == null || b == null) return false;
        return ((Cancion) a).getMediaURI().equals(((Cancion) b).getMediaURI());
    }

    /**
     * Ordena físicamente la lista de canciones en base al criterio seleccionado
     * utilizando una estructura de lista ordenada de inserción.
     * 
     * @param criterio Criterio de ordenamiento (Por Nombre, Por Artista, Por Año).
     */
    public void ordenar(CriterioOrdenacion criterio) {
        ListaCancionOrdenada cancionOrdenada = new ListaCancionOrdenada(criterio);
        NodoDoble act = this.ini;
        
        // Copiar las canciones a la lista que realiza inserción ordenada
        while (act != null) {
            cancionOrdenada.insertar(act.getNodoInfo());
            act = act.getNextNodo();
        }
        
        // Sobrescribir en orden físico los nodos de esta lista con las canciones ordenadas
        act = this.ini;
        for (int i = 0; i < tam(); i++) {
            if (act != null) {
                act.setNodoInfo(cancionOrdenada.devolver(i));
                act = act.getNextNodo();
            }
        }
    }
}
