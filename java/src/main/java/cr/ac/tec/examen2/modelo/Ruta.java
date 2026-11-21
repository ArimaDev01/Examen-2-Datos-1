package cr.ac.tec.examen2.modelo;

/**
 * Resultado del planificador: tramos a seguir en orden, buses a tomar,
 * si hay que caminar y los minutos totales del viaje.
 */
public class Ruta {

    private final String origen;
    private final String destino;
    private final Tramo[] tramos;
    private final int minutos;

    public Ruta(String origen, String destino, Tramo[] tramos) {
        this.origen = origen;
        this.destino = destino;
        this.tramos = new Tramo[tramos.length];
        int total = 0;
        for (int i = 0; i < tramos.length; i++) {
            this.tramos[i] = tramos[i];
            total += tramos[i].getMinutos();
        }
        this.minutos = total;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    /** Copia de los tramos en el orden del viaje. */
    public Tramo[] getTramos() {
        Tramo[] copia = new Tramo[tramos.length];
        for (int i = 0; i < tramos.length; i++) {
            copia[i] = tramos[i];
        }
        return copia;
    }

    public int getMinutos() {
        return minutos;
    }

    /** Indica si algún tramo del viaje se hace caminando. */
    public boolean requiereCaminar() {
        for (Tramo t : tramos) {
            if (t.esCaminata()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Buses a tomar, en orden. Tramos consecutivos de la misma línea cuentan
     * como un solo bus; las caminatas no se incluyen.
     */
    public String[] getBuses() {
        String[] temporal = new String[tramos.length];
        int cantidad = 0;
        String anterior = null;
        for (Tramo t : tramos) {
            if (t.esCaminata()) {
                anterior = null;
                continue;
            }
            if (!t.getLinea().equals(anterior)) {
                temporal[cantidad++] = t.getLinea();
            }
            anterior = t.getLinea();
        }
        String[] buses = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            buses[i] = temporal[i];
        }
        return buses;
    }

    /**
     * Instrucciones legibles: un paso por bus (o caminata), agrupando tramos
     * consecutivos de la misma línea.
     */
    public String[] getInstrucciones() {
        String[] temporal = new String[tramos.length];
        int cantidad = 0;
        int i = 0;
        while (i < tramos.length) {
            Tramo inicio = tramos[i];
            int minutosPaso = inicio.getMinutos();
            int j = i + 1;
            while (j < tramos.length && tramos[j].getLinea().equals(inicio.getLinea())) {
                minutosPaso += tramos[j].getMinutos();
                j++;
            }
            String hacia = tramos[j - 1].getHacia();
            String accion = inicio.esCaminata() ? "Caminar" : "Tomar " + inicio.getLinea();
            temporal[cantidad++] = accion + " de " + inicio.getDesde() + " a " + hacia
                    + " (" + minutosPaso + " min)";
            i = j;
        }
        String[] pasos = new String[cantidad];
        for (int k = 0; k < cantidad; k++) {
            pasos[k] = temporal[k];
        }
        return pasos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Viaje de ").append(origen).append(" a ").append(destino)
          .append(": ").append(minutos).append(" minutos");
        String[] pasos = getInstrucciones();
        if (pasos.length == 0) {
            sb.append(" (ya está en el destino)");
        }
        for (String paso : pasos) {
            sb.append(System.lineSeparator()).append("  - ").append(paso);
        }
        sb.append(System.lineSeparator()).append("  Debe caminar: ")
          .append(requiereCaminar() ? "sí" : "no");
        return sb.toString();
    }
}
