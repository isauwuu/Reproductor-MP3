package modelo.datos;

import modelo.estructuras.Lista1DLinkedL;
import java.io.File;

/**
 * Lista de archivos de tipo File.
 * Extiende {@link Lista1DLinkedL} e implementa la comparación por la ruta absoluta de los archivos.
 */
public class ListaArchivos extends Lista1DLinkedL {

    /**
     * Compara dos objetos File para verificar si son iguales según su ruta absoluta.
     * 
     * @param a Primer objeto (File).
     * @param b Segundo objeto (File).
     * @return true si las rutas absolutas son iguales, false de lo contrario.
     */
    @Override
    public boolean esIgual(Object a, Object b) {
        if (a == null || b == null) return false;
        if (!(a instanceof File) || !(b instanceof File)) return false;
        return ((File) a).getAbsolutePath().equals(((File) b).getAbsolutePath());
    }
}
