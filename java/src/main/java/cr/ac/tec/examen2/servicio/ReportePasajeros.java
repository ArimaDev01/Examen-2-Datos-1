package cr.ac.tec.examen2.servicio;

import cr.ac.tec.examen2.algoritmos.MergeSort;
import cr.ac.tec.examen2.estructuras.ArbolDirectorio;
import cr.ac.tec.examen2.modelo.Parada;

/**
 * F2. Reporte nocturno de pasajeros: todas las paradas de más a menos abordajes,
 * con empates por código de la A a la Z, usando Merge Sort.
 */
public class ReportePasajeros {

    private final MergeSort mergeSort = new MergeSort();

    public Parada[] generar(ArbolDirectorio directorio) {
        if (directorio == null) {
            throw new IllegalArgumentException("El directorio no puede ser nulo");
        }
        return mergeSort.ordenar(directorio.todasLasParadas());
    }

    public Parada[] generar(Parada[] paradas) {
        return mergeSort.ordenar(paradas);
    }

    /** Comparaciones de la última generación (sirve para comprobar el peor caso). */
    public long getComparaciones() {
        return mergeSort.getComparaciones();
    }

    /** Texto del reporte, una parada por línea con su posición. */
    public static String formatear(Parada[] reporte) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reporte.length; i++) {
            Parada p = reporte[i];
            sb.append(String.format("%4d. %s %-12s %-8s %6d%n",
                    i + 1, p.getCodigo(), p.getNombre(), p.getZona(), p.getAbordajes()));
        }
        return sb.toString();
    }
}
