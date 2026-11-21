package cr.ac.tec.examen2.algoritmos;

import cr.ac.tec.examen2.estructuras.Grafo;
import cr.ac.tec.examen2.estructuras.MonticuloMin;
import cr.ac.tec.examen2.modelo.Ruta;
import cr.ac.tec.examen2.modelo.Tramo;

/**
 * F3. Dijkstra propio con montículo de mínimos: O((V + E) log V).
 * Encuentra la ruta de llegada más temprana (menor cantidad de minutos).
 */
public final class Dijkstra {

    private static final int INFINITO = Integer.MAX_VALUE;

    private Dijkstra() {
    }

    /**
     * @throws IllegalArgumentException si el origen o el destino no existen en el grafo.
     * @throws IllegalStateException si el destino no es alcanzable desde el origen.
     */
    public static Ruta rutaMasRapida(Grafo grafo, String origen, String destino) {
        int s = grafo.indiceDe(origen);
        if (s < 0) {
            throw new IllegalArgumentException("No existe la parada de salida: " + origen);
        }
        int t = grafo.indiceDe(destino);
        if (t < 0) {
            throw new IllegalArgumentException("No existe la parada de destino: " + destino);
        }
        int n = grafo.cantidadVertices();
        int[] distancia = new int[n];
        Tramo[] llegoPor = new Tramo[n];
        int[] anterior = new int[n];
        boolean[] visitado = new boolean[n];
        for (int i = 0; i < n; i++) {
            distancia[i] = INFINITO;
            anterior[i] = -1;
        }
        distancia[s] = 0;
        MonticuloMin cola = new MonticuloMin(n);
        cola.insertar(s, 0);

        while (!cola.estaVacio()) {
            int u = cola.extraerMinimo();
            if (visitado[u]) {
                continue; // entrada vieja del montículo
            }
            visitado[u] = true;
            if (u == t) {
                break;
            }
            for (Tramo tramo : grafo.tramosDesde(u)) {
                int v = grafo.indiceDe(tramo.getHacia());
                int nueva = distancia[u] + tramo.getMinutos();
                if (!visitado[v] && nueva < distancia[v]) {
                    distancia[v] = nueva;
                    anterior[v] = u;
                    llegoPor[v] = tramo;
                    cola.insertar(v, nueva);
                }
            }
        }

        if (distancia[t] == INFINITO) {
            throw new IllegalStateException("No hay ruta de " + origen + " a " + destino);
        }

        int pasos = 0;
        for (int v = t; v != s; v = anterior[v]) {
            pasos++;
        }
        Tramo[] tramos = new Tramo[pasos];
        int k = pasos - 1;
        for (int v = t; v != s; v = anterior[v]) {
            tramos[k--] = llegoPor[v];
        }
        return new Ruta(grafo.nombreDe(s), grafo.nombreDe(t), tramos);
    }
}
