package cr.ac.tec.examen2.modelo;

/**
 * Tramo dirigido entre dos paradas: una línea de bus o una caminata.
 */
public class Tramo {

    /** Nombre de línea usado para los tramos a pie. */
    public static final String CAMINATA = "Caminata";

    private final String linea;
    private final String desde;
    private final String hacia;
    private final int minutos;

    public Tramo(String linea, String desde, String hacia, int minutos) {
        if (linea == null || linea.isBlank()) {
            throw new IllegalArgumentException("La línea del tramo no puede estar vacía");
        }
        if (desde == null || desde.isBlank() || hacia == null || hacia.isBlank()) {
            throw new IllegalArgumentException("El tramo debe tener origen y destino");
        }
        if (minutos <= 0) {
            throw new IllegalArgumentException("Los minutos del tramo deben ser positivos: " + minutos);
        }
        this.linea = linea.trim();
        this.desde = desde.trim();
        this.hacia = hacia.trim();
        this.minutos = minutos;
    }

    public String getLinea() {
        return linea;
    }

    public String getDesde() {
        return desde;
    }

    public String getHacia() {
        return hacia;
    }

    public int getMinutos() {
        return minutos;
    }

    public boolean esCaminata() {
        return CAMINATA.equals(linea);
    }

    @Override
    public String toString() {
        return linea + ": " + desde + " -> " + hacia + " (" + minutos + " min)";
    }
}
