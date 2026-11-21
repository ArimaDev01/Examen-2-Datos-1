package cr.ac.tec.examen2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cr.ac.tec.examen2.datos.CargadorCsv;
import cr.ac.tec.examen2.estructuras.ArbolDirectorio;
import cr.ac.tec.examen2.estructuras.Grafo;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Carga de los CSV de datos/version-a y rechazo de datos mal formados. */
class CargadorCsvTest {

    @Test
    @DisplayName("Carga abordajes.csv: 3 zonas y 8 paradas")
    void cargaAbordajes() throws IOException {
        ArbolDirectorio d = DatosPrueba.directorio();
        assertEquals(3, d.nombresZonas().length);
        assertEquals(8, d.todasLasParadas().length);
        assertEquals("Hospital", d.buscarParada("S08").getNombre());
    }

    @Test
    @DisplayName("Carga tramos.csv: 8 paradas y 14 aristas (12 de bus y 2 de caminata)")
    void cargaTramos() throws IOException {
        Grafo g = DatosPrueba.grafo();
        assertEquals(8, g.cantidadVertices());
        assertEquals(14, g.cantidadAristas());
    }

    @Test
    @DisplayName("Archivo con solo encabezado: directorio y grafo vacíos")
    void soloEncabezado() {
        assertEquals(0, CargadorCsv.directorioDesdeTexto("C", CargadorCsv.ENCABEZADO_ABORDAJES + "\n")
                .totalAbordajesCiudad());
        assertEquals(0, CargadorCsv.grafoDesdeTexto(CargadorCsv.ENCABEZADO_TRAMOS + "\n").cantidadVertices());
    }

    @Test
    @DisplayName("Dato mal formado: abordajes no numéricos indican la línea")
    void abordajesNoNumericos() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> CargadorCsv.directorioDesdeTexto("C",
                        "codigo,parada,zona,abordajes\nS01,Terminal,Centro,mil\n"));
        assertTrue(e.getMessage().contains("Línea 2"));
    }

    @Test
    @DisplayName("Dato mal formado: columnas faltantes, encabezado incorrecto, archivo vacío")
    void estructuraInvalida() {
        assertThrows(IllegalArgumentException.class, () -> CargadorCsv.directorioDesdeTexto("C",
                "codigo,parada,zona,abordajes\nS01,Terminal,1520\n"));
        assertThrows(IllegalArgumentException.class, () -> CargadorCsv.directorioDesdeTexto("C",
                "id,nombre\nS01,Terminal\n"));
        assertThrows(IllegalArgumentException.class, () -> CargadorCsv.grafoDesdeTexto(""));
    }

    @Test
    @DisplayName("Dato mal formado: código repetido o abordajes negativos")
    void paradasInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> CargadorCsv.directorioDesdeTexto("C",
                "codigo,parada,zona,abordajes\nS01,A,Centro,1\nS01,B,Centro,2\n"));
        assertThrows(IllegalArgumentException.class, () -> CargadorCsv.directorioDesdeTexto("C",
                "codigo,parada,zona,abordajes\nS01,A,Centro,-5\n"));
    }

    @Test
    @DisplayName("Dato mal formado: minutos no numéricos o en cero en tramos.csv")
    void tramosInvalidos() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> CargadorCsv.grafoDesdeTexto("linea,desde,hacia,minutos\nBus 1,A,B,seis\n"));
        assertTrue(e.getMessage().contains("Línea 2"));
        assertThrows(IllegalArgumentException.class,
                () -> CargadorCsv.grafoDesdeTexto("linea,desde,hacia,minutos\nBus 1,A,B,0\n"));
    }

    @Test
    @DisplayName("Archivo inexistente lanza IOException")
    void archivoInexistente() {
        assertThrows(IOException.class, () -> CargadorCsv.cargarGrafo("no-existe.csv"));
    }
}
