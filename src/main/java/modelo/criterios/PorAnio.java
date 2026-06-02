package modelo.criterios;


import modelo.datos.Cancion;

public class PorAnio implements CriterioOrdenacion {
    public int comparar(Cancion c1, Cancion c2) {
        return c1.getAnio() == c2.getAnio() ? 0 : c1.getAnio() > c2.getAnio() ? 1 : -1;
    }
}
