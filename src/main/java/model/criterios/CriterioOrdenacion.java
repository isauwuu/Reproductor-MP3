package model.criterios;


import model.datos.Cancion;

public interface CriterioOrdenacion {
    public int comparar(Cancion c1, Cancion c2);
}
