package modelo.criterios;


import modelo.datos.Cancion;

public class PorArtista implements CriterioOrdenacion{

    public int comparar(Cancion c1, Cancion c2) {
        return c1.getArtista().compareToIgnoreCase(c2.getArtista());
    }
}
