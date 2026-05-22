package modelo.estructuras;


import modelo.interfaces.OperacionesCL2;

//Hay que cambiar todos los printfs y ver como implementarlo en la gui(??//
public abstract class Lista0DLinkedL implements OperacionesCL2 {
    protected NodoDoble ini,fin;
    protected int ult;

    public Lista0DLinkedL(){
        limpiar();
    }
    public void limpiar(){
        this.ini=this.fin=null;
        this.ult=-1;
    }
    public boolean estaVacia(){
        return this.ini==null;
    }
    public int tam(){
        return this.ult+1;
    }
    public void eliminar(int pos){
        if (estaVacia()) {
            System.out.println("Error eliminar. Lista vacia...");
        }else {
            if (pos >= tam() || pos < 0) {
                System.out.println("Error eliminar. Posicion inexistente ");
            } else {
                if (pos == 0) {
                    //si la posicion es la primera
                    if (this.ini == this.fin) {
                        limpiar();
                    } else {
                        this.ini = this.ini.getNextNodo();
                        this.ini.setPrevNodo(null);
                        this.ult--;
                    }
                } else {
                    //si la posicion es la ultima
                    if (pos == tam() - 1) {
                        this.fin = this.fin.getPrevNodo();
                        this.fin.setNextNodo(null);
                    } else {
                        NodoDoble obj=this.ini;
                        for (int cont = 0; cont < pos; cont++) {
                            obj=obj.getNextNodo();
                        }
                        NodoDoble ant=obj.getPrevNodo();
                        NodoDoble sig=obj.getNextNodo();
                        ant.setNextNodo(sig);
                        sig.setPrevNodo(ant);
                    }
                    this.ult--;
                }
            }
        }
    }
    public Object devolver(int pos){
        Object elemento = null;
        if (estaVacia()) {
            System.out.println("Error al devolver, Lista vacia...");
        } else {
            if (pos >= tam() || pos < 0) {
                System.out.println("Error al devolver, La posicion es invalida");
            }else{
                NodoDoble act;
                act = this.ini;

                for (int cont = 0; cont < pos; cont++) {
                    act = act.getNextNodo();
                }
                elemento = act.getNodoInfo();
            }
        }
        return elemento;
    }
    public abstract int buscar(Object elemento);

}