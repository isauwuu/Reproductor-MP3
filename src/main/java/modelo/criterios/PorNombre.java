package modelo.criterios;


import modelo.datos.Cancion;

public class PorNombre implements CriterioOrdenacion{
    public int comparar(Cancion c1, Cancion c2){
        return c1.getTitulo().compareToIgnoreCase(c2.getTitulo());
    }
}
