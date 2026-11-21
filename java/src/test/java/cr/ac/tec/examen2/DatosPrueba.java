package cr.ac.tec.examen2;

import cr.ac.tec.examen2.datos.CargadorCsv;
import cr.ac.tec.examen2.estructuras.ArbolDirectorio;
import cr.ac.tec.examen2.estructuras.Grafo;
import java.io.IOException;

/** Carga los CSV reales de datos/version-a para las pruebas (Maven corre desde java/). */
final class DatosPrueba {

    static final String CARPETA = "../datos/version-a";

    private DatosPrueba() {
    }

    static ArbolDirectorio directorio() throws IOException {
        return CargadorCsv.cargarDirectorio(Main.CIUDAD, CARPETA + "/abordajes.csv");
    }

    static Grafo grafo() throws IOException {
        return CargadorCsv.cargarGrafo(CARPETA + "/tramos.csv");
    }
}
