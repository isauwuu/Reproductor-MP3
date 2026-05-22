package modelo.interfaces;

public interface OperacionesCL2 {
    public int buscar(Object elemento);
    public Object devolver(int pos);
    public void eliminar(int posicion);
    public void limpiar();
    public boolean estaVacia();
    public int tam();
}