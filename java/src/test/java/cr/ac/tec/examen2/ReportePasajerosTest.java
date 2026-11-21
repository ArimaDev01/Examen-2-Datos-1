package cr.ac.tec.examen2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cr.ac.tec.examen2.modelo.Parada;
import cr.ac.tec.examen2.servicio.ReportePasajeros;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** F2. Reporte de pasajeros: requerimientos funcionales 5 a 7 y no funcional 2. */
class ReportePasajerosTest {

    private final ReportePasajeros reporte = new ReportePasajeros();

    private static String[] codigos(Parada[] paradas) {
        String[] c = new String[paradas.length];
        for (int i = 0; i < paradas.length; i++) {
            c[i] = paradas[i].getCodigo();
        }
        return c;
    }

    private static String codigo(int i) {
        return String.format("P%04d", i);
    }

    /** n paradas con abordajes ascendentes: el reporte debe invertirlas por completo. */
    private static Parada[] ascendentes(int n) {
        Parada[] p = new Parada[n];
        for (int i = 0; i < n; i++) {
            p[i] = new Parada(codigo(i), "Parada " + i, "Centro", i);
        }
        return p;
    }

    private static void verificarOrden(Parada[] r) {
        for (int i = 1; i < r.length; i++) {
            Parada a = r[i - 1];
            Parada b = r[i];
            assertTrue(a.getAbordajes() > b.getAbordajes()
                    || (a.getAbordajes() == b.getAbordajes() && a.getCodigo().compareTo(b.getCodigo()) < 0),
                    "Orden incorrecto en la posición " + i + ": " + a + " / " + b);
        }
    }

    // ---------- RF 5: Cada noche se listan las 1.200 paradas de más a menos abordajes ----------

    @Test
    @DisplayName("RF 5: reporte exacto de las 8 paradas de los datos entregados")
    void rf05ReporteDatosReales() throws IOException {
        Parada[] r = reporte.generar(DatosPrueba.directorio());
        assertArrayEquals(new String[] {"S01", "S03", "S08", "S02", "S05", "S04", "S07", "S06"}, codigos(r));
        assertEquals(1520, r[0].getAbordajes());
        assertEquals(455, r[7].getAbordajes());
    }

    @Test
    @DisplayName("RF 5: con 1.200 paradas, la primera tiene 1199 abordajes y la última 0")
    void rf05MilDoscientasParadas() {
        Parada[] r = reporte.generar(ascendentes(1200));
        assertEquals(1200, r.length);
        assertEquals("P1199", r[0].getCodigo());
        assertEquals(1199, r[0].getAbordajes());
        assertEquals("P0000", r[1199].getCodigo());
        assertEquals(0, r[1199].getAbordajes());
        verificarOrden(r);
    }

    @Test
    @DisplayName("RF 5: el reporte no modifica el arreglo original")
    void rf05NoModificaEntrada() {
        Parada[] entrada = ascendentes(5);
        reporte.generar(entrada);
        assertEquals("P0000", entrada[0].getCodigo());
        assertEquals("P0004", entrada[4].getCodigo());
    }

    @Test
    @DisplayName("RF 5 (límite): sin paradas el reporte está vacío; con una, la devuelve")
    void rf05Limites() {
        assertEquals(0, reporte.generar(new Parada[0]).length);
        Parada unica = new Parada("S01", "Terminal", "Centro", 1520);
        assertArrayEquals(new Parada[] {unica}, reporte.generar(new Parada[] {unica}));
    }

    @Test
    @DisplayName("RF 5 (inválido): arreglo nulo, parada nula o directorio nulo lanzan error")
    void rf05Invalidos() {
        assertThrows(IllegalArgumentException.class, () -> reporte.generar((Parada[]) null));
        Parada[] conNulo = {new Parada("S01", "Terminal", "Centro", 1), null};
        assertThrows(IllegalArgumentException.class, () -> reporte.generar(conNulo));
        assertThrows(IllegalArgumentException.class,
                () -> reporte.generar((cr.ac.tec.examen2.estructuras.ArbolDirectorio) null));
    }

    // ---------- RF 6 / RNF 2: Empates por código de la A a la Z ----------

    @Test
    @DisplayName("RF 6: los empates de los datos entregados se ordenan por código")
    void rf06EmpatesDatosReales() throws IOException {
        Parada[] r = reporte.generar(DatosPrueba.directorio());
        // 1310: S03 antes que S08; 980: S02 antes que S05; 640: S04 antes que S07
        assertEquals("S03", r[1].getCodigo());
        assertEquals("S08", r[2].getCodigo());
        assertEquals("S02", r[3].getCodigo());
        assertEquals("S05", r[4].getCodigo());
        assertEquals("S04", r[5].getCodigo());
        assertEquals("S07", r[6].getCodigo());
    }

    @Test
    @DisplayName("RF 6 (límite): todas empatadas y en orden Z-A quedan en orden A-Z")
    void rf06TodasEmpatadas() {
        Parada[] entrada = {
            new Parada("C", "c", "Centro", 100),
            new Parada("B", "b", "Centro", 100),
            new Parada("A", "a", "Centro", 100),
        };
        assertArrayEquals(new String[] {"A", "B", "C"}, codigos(reporte.generar(entrada)));
    }

    @Test
    @DisplayName("RNF 2: en caso de empate al listar cada noche se ordena por código A-Z (1.200 paradas)")
    void rnf02EmpatesMilDoscientas() {
        Parada[] entrada = new Parada[1200];
        for (int i = 0; i < 1200; i++) {
            // código descendente y solo 3 valores de abordajes: muchos empates
            entrada[i] = new Parada(codigo(1199 - i), "p", "Norte", i % 3);
        }
        Parada[] r = reporte.generar(entrada);
        verificarOrden(r);
        assertEquals(2, r[0].getAbordajes());
        assertEquals("P0000", r[0].getCodigo());
        assertEquals(0, r[1199].getAbordajes());
    }

    // ---------- RF 7: Tiempo garantizado incluso para la peor entrada ----------

    @Test
    @DisplayName("RF 7: con 1.200 paradas, las comparaciones nunca superan n·⌈log2 n⌉ = 13.200")
    void rf07CotaPeorCaso() {
        int n = 1200;
        long cota = (long) n * 11; // ⌈log2 1200⌉ = 11
        assertEquals(13200, cota);

        Parada[][] entradas = new Parada[4][];
        entradas[0] = ascendentes(n);                    // completamente invertida
        entradas[1] = reporte.generar(ascendentes(n));   // ya ordenada
        entradas[2] = new Parada[n];                     // todas empatadas
        entradas[3] = new Parada[n];                     // alternada (intercalada)
        for (int i = 0; i < n; i++) {
            entradas[2][i] = new Parada(codigo(n - 1 - i), "p", "Centro", 7);
            entradas[3][i] = new Parada(codigo(i), "p", "Centro", (i % 2 == 0) ? i : n - i);
        }
        for (Parada[] entrada : entradas) {
            Parada[] r = reporte.generar(entrada);
            verificarOrden(r);
            assertTrue(reporte.getComparaciones() <= cota,
                    "Comparaciones: " + reporte.getComparaciones());
        }
    }

    @Test
    @DisplayName("RF 7: número exacto de comparaciones para 8 paradas en orden inverso (12)")
    void rf07ComparacionesExactas() {
        reporte.generar(ascendentes(8));
        // Merge Sort sobre una entrada invertida compara n/2·log2 n = 4·3 = 12 veces
        assertEquals(12, reporte.getComparaciones());
    }

    @Test
    @DisplayName("RF 7 (límite): el peor caso de 1.200 paradas termina en menos de 1 segundo")
    void rf07TiempoPeorCaso() {
        Parada[] entrada = ascendentes(1200);
        long inicio = System.nanoTime();
        reporte.generar(entrada);
        long ms = (System.nanoTime() - inicio) / 1_000_000;
        assertTrue(ms < 1000, "Tardó " + ms + " ms");
    }
}
