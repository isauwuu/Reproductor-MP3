package modelo.criterios;


import modelo.datos.Cancion;

public class PorAnio implements CriterioOrdenacion {
    public int comparar(Cancion c1, Cancion c2) {
        if (c1.getAnio() == c2.getAnio())
            return 0;
        else {
            if (c1.getAnio() > c2.getAnio())
                return 1;
            return -1;
        }
    }
}
