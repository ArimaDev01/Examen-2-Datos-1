package cr.ac.tec.examen2.estructuras;

/**
 * Montículo binario de mínimos propio, usado como cola de prioridad en Dijkstra.
 * Guarda pares (vértice, distancia). Insertar y extraer son O(log n).
 */
public class MonticuloMin {

    private int[] vertices;
    private int[] distancias;
    private int tamano;

    public MonticuloMin(int capacidadInicial) {
        int capacidad = capacidadInicial < 1 ? 1 : capacidadInicial;
        vertices = new int[capacidad];
        distancias = new int[capacidad];
        tamano = 0;
    }

    public boolean estaVacio() {
        return tamano == 0;
    }

    public int tamano() {
        return tamano;
    }

    public void insertar(int vertice, int distancia) {
        if (tamano == vertices.length) {
            crecer();
        }
        vertices[tamano] = vertice;
        distancias[tamano] = distancia;
        subir(tamano);
        tamano++;
    }

    /** Distancia del elemento mínimo, sin extraerlo. */
    public int distanciaMinima() {
        if (tamano == 0) {
            throw new IllegalStateException("El montículo está vacío");
        }
        return distancias[0];
    }

    /** Extrae el elemento mínimo y devuelve su vértice. */
    public int extraerMinimo() {
        if (tamano == 0) {
            throw new IllegalStateException("El montículo está vacío");
        }
        int vertice = vertices[0];
        tamano--;
        vertices[0] = vertices[tamano];
        distancias[0] = distancias[tamano];
        bajar(0);
        return vertice;
    }

    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (distancias[i] >= distancias[padre]) {
                return;
            }
            intercambiar(i, padre);
            i = padre;
        }
    }

    private void bajar(int i) {
        while (true) {
            int izq = 2 * i + 1;
            int der = izq + 1;
            int menor = i;
            if (izq < tamano && distancias[izq] < distancias[menor]) {
                menor = izq;
            }
            if (der < tamano && distancias[der] < distancias[menor]) {
                menor = der;
            }
            if (menor == i) {
                return;
            }
            intercambiar(i, menor);
            i = menor;
        }
    }

    private void intercambiar(int a, int b) {
        int v = vertices[a];
        vertices[a] = vertices[b];
        vertices[b] = v;
        int d = distancias[a];
        distancias[a] = distancias[b];
        distancias[b] = d;
    }

    private void crecer() {
        int[] nv = new int[vertices.length * 2];
        int[] nd = new int[distancias.length * 2];
        for (int i = 0; i < tamano; i++) {
            nv[i] = vertices[i];
            nd[i] = distancias[i];
        }
        vertices = nv;
        distancias = nd;
    }
}
