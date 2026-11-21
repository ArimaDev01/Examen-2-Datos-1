package cr.ac.tec.examen2.estructuras;

import cr.ac.tec.examen2.modelo.Parada;

/**
 * Nodo del árbol n-ario del directorio. Cada nodo tiene un nivel
 * (RutaBus, Ciudad, Zona o Parada), un nombre y una lista de hijos.
 * Solo los nodos de nivel PARADA guardan una {@link Parada}.
 */
public class NodoArbol {

    /** Niveles del árbol, de la raíz a las hojas. */
    public enum Nivel { RUTABUS, CIUDAD, ZONA, PARADA }

    private final Nivel nivel;
    private final String nombre;
    private final Parada parada;
    private final Lista<NodoArbol> hijos = new Lista<>();

    public NodoArbol(Nivel nivel, String nombre) {
        this(nivel, nombre, null);
    }

    public NodoArbol(Nivel nivel, String nombre, Parada parada) {
        this.nivel = nivel;
        this.nombre = nombre;
        this.parada = parada;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public String getNombre() {
        return nombre;
    }

    public Parada getParada() {
        return parada;
    }

    public void agregarHijo(NodoArbol hijo) {
        hijos.agregar(hijo);
    }

    public int cantidadHijos() {
        return hijos.tamano();
    }

    public NodoArbol getHijo(int indice) {
        return hijos.obtener(indice);
    }

    /** Busca un hijo directo por nombre; devuelve null si no existe. */
    public NodoArbol buscarHijo(String nombreHijo) {
        for (int i = 0; i < hijos.tamano(); i++) {
            NodoArbol hijo = hijos.obtener(i);
            if (hijo.getNombre().equals(nombreHijo)) {
                return hijo;
            }
        }
        return null;
    }

    /** Suma los abordajes de todas las paradas del subárbol. */
    public long totalAbordajes() {
        if (nivel == Nivel.PARADA) {
            return parada.getAbordajes();
        }
        long total = 0;
        for (int i = 0; i < hijos.tamano(); i++) {
            total += hijos.obtener(i).totalAbordajes();
        }
        return total;
    }
}
