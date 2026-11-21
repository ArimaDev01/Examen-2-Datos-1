package cr.ac.tec.examen2.estructuras;

import cr.ac.tec.examen2.modelo.Tramo;

/**
 * Grafo dirigido y ponderado con listas de adyacencia propias.
 * Vértices = paradas (por nombre); aristas = tramos.
 * Los tramos de bus se agregan solo en el sentido de su línea; las caminatas
 * se agregan en ambos sentidos.
 */
public class Grafo {

    private final Lista<String> vertices = new Lista<>();
    private final Lista<Lista<Tramo>> adyacencia = new Lista<>();
    private int cantidadAristas;

    /** Agrega un vértice si no existe y devuelve su índice. */
    public int agregarVertice(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del vértice no puede estar vacío");
        }
        int indice = indiceDe(nombre);
        if (indice >= 0) {
            return indice;
        }
        vertices.agregar(nombre.trim());
        adyacencia.agregar(new Lista<>());
        return vertices.tamano() - 1;
    }

    /** Arista dirigida (tramo de bus): solo en el sentido de la línea. */
    public void agregarTramoBus(String linea, String desde, String hacia, int minutos) {
        Tramo tramo = new Tramo(linea, desde, hacia, minutos);
        int origen = agregarVertice(tramo.getDesde());
        agregarVertice(tramo.getHacia());
        adyacencia.obtener(origen).agregar(tramo);
        cantidadAristas++;
    }

    /** Caminata: se agrega una arista en cada sentido. */
    public void agregarCaminata(String a, String b, int minutos) {
        Tramo ida = new Tramo(Tramo.CAMINATA, a, b, minutos);
        Tramo vuelta = new Tramo(Tramo.CAMINATA, b, a, minutos);
        int ia = agregarVertice(ida.getDesde());
        int ib = agregarVertice(ida.getHacia());
        adyacencia.obtener(ia).agregar(ida);
        adyacencia.obtener(ib).agregar(vuelta);
        cantidadAristas += 2;
    }

    /** Índice del vértice o -1 si no existe. */
    public int indiceDe(String nombre) {
        if (nombre == null) {
            return -1;
        }
        String buscado = nombre.trim();
        for (int i = 0; i < vertices.tamano(); i++) {
            if (vertices.obtener(i).equals(buscado)) {
                return i;
            }
        }
        return -1;
    }

    public String nombreDe(int indice) {
        return vertices.obtener(indice);
    }

    public int cantidadVertices() {
        return vertices.tamano();
    }

    public int cantidadAristas() {
        return cantidadAristas;
    }

    /** Tramos que salen del vértice dado. */
    public Tramo[] tramosDesde(int indice) {
        return adyacencia.obtener(indice).aArreglo(new Tramo[0]);
    }
}
