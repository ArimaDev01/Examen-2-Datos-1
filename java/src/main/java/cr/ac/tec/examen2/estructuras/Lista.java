package cr.ac.tec.examen2.estructuras;

/**
 * Lista dinámica propia sobre un arreglo que duplica su capacidad al llenarse.
 * Sustituye a las colecciones de java.util, que el examen no permite.
 */
public class Lista<T> {

    private Object[] elementos;
    private int tamano;

    public Lista() {
        this(4);
    }

    public Lista(int capacidadInicial) {
        elementos = new Object[capacidadInicial < 1 ? 1 : capacidadInicial];
        tamano = 0;
    }

    /** Agrega al final. O(1) amortizado. */
    public void agregar(T elemento) {
        if (tamano == elementos.length) {
            Object[] nuevo = new Object[elementos.length * 2];
            for (int i = 0; i < tamano; i++) {
                nuevo[i] = elementos[i];
            }
            elementos = nuevo;
        }
        elementos[tamano++] = elemento;
    }

    @SuppressWarnings("unchecked")
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        return (T) elementos[indice];
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    /** Copia los elementos a un arreglo del tipo dado (por ejemplo new Parada[0]). */
    @SuppressWarnings("unchecked")
    public T[] aArreglo(T[] molde) {
        T[] resultado = (T[]) java.lang.reflect.Array.newInstance(
                molde.getClass().getComponentType(), tamano);
        for (int i = 0; i < tamano; i++) {
            resultado[i] = (T) elementos[i];
        }
        return resultado;
    }
}
