package services;

import modelo.criterios.CriterioOrdenacion;
import modelo.datos.Cancion;

public class OrdenamientoService {

    private CriterioOrdenacion criterio;

    public void setCriterio(CriterioOrdenacion criterio) {
        this.criterio = criterio;
    }

    public int comparar(Cancion c1, Cancion c2) {
        return criterio.comparar(c1, c2);
    }
}