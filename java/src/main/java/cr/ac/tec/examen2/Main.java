package cr.ac.tec.examen2;

import cr.ac.tec.examen2.datos.CargadorCsv;
import cr.ac.tec.examen2.estructuras.ArbolDirectorio;
import cr.ac.tec.examen2.estructuras.Grafo;
import cr.ac.tec.examen2.modelo.Parada;
import cr.ac.tec.examen2.servicio.PlanificadorViajes;
import cr.ac.tec.examen2.servicio.ReportePasajeros;
import java.io.File;
import java.io.IOException;

/**
 * RutaBus: carga los CSV de datos/version-a y ejecuta lo que pide el examen:
 * totales por zona y ciudad, reporte de pasajeros, Terminal → Universidad y Faro → Terminal.
 *
 * Uso: java -cp target/classes cr.ac.tec.examen2.Main [carpeta-de-datos]
 */
public class Main {

    public static final String CIUDAD = "Puerto Celeste";

    public static void main(String[] args) throws IOException {
        String carpeta = args.length > 0 ? args[0] : buscarCarpetaDatos();
        ArbolDirectorio directorio = CargadorCsv.cargarDirectorio(CIUDAD, carpeta + "/abordajes.csv");
        Grafo grafo = CargadorCsv.cargarGrafo(carpeta + "/tramos.csv");

        System.out.println("=== RutaBus - " + CIUDAD + " ===");
        System.out.println();
        System.out.println("--- F1. Total de abordajes por zona y de la ciudad ---");
        for (String zona : directorio.nombresZonas()) {
            System.out.print(zona + ": " + directorio.totalAbordajesZona(zona) + " abordajes  [");
            Parada[] paradas = directorio.listarParadas(zona);
            for (int i = 0; i < paradas.length; i++) {
                System.out.print((i > 0 ? ", " : "") + paradas[i].getNombre());
            }
            System.out.println("]");
        }
        System.out.println("Ciudad: " + directorio.totalAbordajesCiudad() + " abordajes");
        System.out.println();

        System.out.println("--- F2. Reporte de pasajeros ---");
        ReportePasajeros reporte = new ReportePasajeros();
        System.out.print(ReportePasajeros.formatear(reporte.generar(directorio)));
        System.out.println();

        System.out.println("--- F3. Planificador de viajes ---");
        PlanificadorViajes planificador = new PlanificadorViajes(grafo);
        System.out.println(planificador.planificar("Terminal", "Universidad"));
        System.out.println();
        System.out.println(planificador.planificar("Faro", "Terminal"));
    }

    private static String buscarCarpetaDatos() {
        String[] candidatas = {"../datos/version-a", "datos/version-a"};
        for (String c : candidatas) {
            if (new File(c, "abordajes.csv").isFile()) {
                return c;
            }
        }
        throw new IllegalStateException("No se encontró la carpeta datos/version-a; páse la ruta como argumento");
    }
}
