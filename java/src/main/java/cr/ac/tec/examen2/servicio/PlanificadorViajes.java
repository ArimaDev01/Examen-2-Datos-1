package cr.ac.tec.examen2.servicio;

import cr.ac.tec.examen2.algoritmos.Dijkstra;
import cr.ac.tec.examen2.estructuras.Grafo;
import cr.ac.tec.examen2.modelo.Ruta;

/**
 * F3. Planificador de viajes: el pasajero elige parada de salida y destino y
 * recibe los buses a tomar, si debe caminar y los minutos del viaje.
 */
public class PlanificadorViajes {

    private final Grafo grafo;

    public PlanificadorViajes(Grafo grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("El grafo no puede ser nulo");
        }
        this.grafo = grafo;
    }

    /**
     * @throws IllegalArgumentException si la salida o el destino están vacíos o no existen.
     * @throws IllegalStateException si no hay forma de llegar.
     */
    public Ruta planificar(String salida, String destino) {
        if (salida == null || salida.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una parada de salida");
        }
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una parada de destino");
        }
        return Dijkstra.rutaMasRapida(grafo, salida, destino);
    }

    public Grafo getGrafo() {
        return grafo;
    }
}
