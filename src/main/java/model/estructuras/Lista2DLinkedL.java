package model.estructuras;


import model.criterios.CriterioOrdenacion;
import interfaces.OperacionesCL4;

public abstract class Lista2DLinkedL extends Lista0DLinkedL implements OperacionesCL4 {
    private CriterioOrdenacion criterio;

    // insercion ordenada HAY Q TOMAR ESTO COMO BASE PERO CAMBIAR
    public void insertar(Object elemento) {
        NodoDoble nodo;
        if (estaVacia()) {
            this.ini = this.fin = new NodoDoble(elemento);

        } else{
            if (esMenor(elemento, this.ini.getNodoInfo())) {		//insercion al frente
                this.ini = new NodoDoble(elemento, null, this.ini); // nuevo frente
                this.ini.getNextNodo().setPrevNodo(this.ini); // vamos al 2do y conectamos con el 1ero

            }else{

                if (esMayor(elemento, this.fin.getNodoInfo()) || iguales(elemento, this.fin.getNodoInfo())) {	//insercion al final, si es igual no puede ponerse antes.
                    this.fin.setNextNodo(new NodoDoble(elemento,this.fin,null));
                    this.fin=this.fin.getNextNodo();
                }else{
                    // al medio
                    NodoDoble aux=this.ini.getNextNodo();
                    while (esMayor(elemento,aux.getNodoInfo())||iguales(elemento,aux.getNodoInfo()))
                        aux=aux.getNextNodo();
                    NodoDoble nuevo =new NodoDoble(elemento,aux.getPrevNodo(),aux);
                    aux.getPrevNodo().setNextNodo(nuevo);
                    aux.setPrevNodo(nuevo);
                }
            }
        }

        this.ult++; // incrementamos "ultima posicion" de lista
    }
    public int buscar(Object elemento) {
        if(estaVacia())
            return -1;
        if(iguales(elemento,this.ini.getNodoInfo()))
            return 0;
        if (iguales(elemento,this.fin.getNodoInfo()))
            return this.tam()-1;
        else{
            NodoDoble act=this.ini.getNextNodo();
            int c=1;
            while (act != null && !iguales(elemento, act.getNodoInfo()) && esMenor(act.getNodoInfo(), elemento)){
                act=act.getNextNodo();
                c++;
            }
            if (act != null && iguales(elemento, act.getNodoInfo())) {
                return c; // se encontro el elemento
            } else {
                return -1; // no está
            }
        }
    }

    public abstract boolean iguales(Object e1,Object e2);
    public abstract boolean esMenor(Object e1,Object e2);
    public abstract boolean esMayor(Object e1,Object e2);

//    public boolean iguales(Object elemento1, Object elemento2){
//        return criterio.comparar((Cancion)e1, (Cancion)e2) == 0;
//    }
//    public boolean esMenor(Object elemento1, Object elemento2){
//        return criterio.comparar((Cancion)e1, (Cancion)e2)<0;
//    }
//    public  boolean esMayor(Object elemento1, Object elemento2){
//        return criterio.comparar((Cancion)e1, (Cancion)e2) > 0;
//    }
    //Cuando el usuario haga clic en el botón "Ordenar por Artista", el controlador simplemente hace:
    //miLista.setCriterio(new PorArtista());
    //Como tu lista ya está cargada, si quisieran re-ordenar una lista existente, tendrían que vaciarla y volver a insertar
    // todo (es el costo de tener una lista ordenada), o implementar un algoritmo de ordenamiento (sort) in-place.
}
