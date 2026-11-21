package cr.ac.tec.examen2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cr.ac.tec.examen2.estructuras.Grafo;
import cr.ac.tec.examen2.modelo.Ruta;
import cr.ac.tec.examen2.modelo.Tramo;
import cr.ac.tec.examen2.servicio.PlanificadorViajes;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** F3. Planificador de viajes: requerimientos funcionales 8 a 13 y no funcional 1. */
class PlanificadorViajesTest {

    private PlanificadorViajes planificador;

    @BeforeEach
    void cargar() throws IOException {
        planificador = new PlanificadorViajes(DatosPrueba.grafo());
    }

    private static String[] recorrido(Ruta ruta) {
        Tramo[] tramos = ruta.getTramos();
        String[] paradas = new String[tramos.length + 1];
        paradas[0] = ruta.getOrigen();
        for (int i = 0; i < tramos.length; i++) {
            paradas[i + 1] = tramos[i].getHacia();
        }
        return paradas;
    }

    // ---------- RF 8: El pasajero selecciona una parada de salida y un destino ----------

    @Test
    @DisplayName("RF 8: la ruta respeta la salida y el destino seleccionados")
    void rf08SalidaYDestino() {
        Ruta ruta = planificador.planificar("Terminal", "Universidad");
        assertEquals("Terminal", ruta.getOrigen());
        assertEquals("Universidad", ruta.getDestino());
        assertEquals("Terminal", ruta.getTramos()[0].getDesde());
        assertEquals("Universidad", ruta.getTramos()[ruta.getTramos().length - 1].getHacia());
    }

    @Test
    @DisplayName("RF 8 (límite): salida igual al destino da 0 minutos y ningún tramo")
    void rf08OrigenIgualDestino() {
        Ruta ruta = planificador.planificar("Mercado", "Mercado");
        assertEquals(0, ruta.getMinutos());
        assertEquals(0, ruta.getTramos().length);
        assertEquals(0, ruta.getBuses().length);
        assertFalse(ruta.requiereCaminar());
    }

    @Test
    @DisplayName("RF 8 (inválido): parada inexistente, vacía o nula lanza error")
    void rf08ParadasInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> planificador.planificar("Aeropuerto", "Faro"));
        assertThrows(IllegalArgumentException.class, () -> planificador.planificar("Faro", "Aeropuerto"));
        assertThrows(IllegalArgumentException.class, () -> planificador.planificar("", "Faro"));
        assertThrows(IllegalArgumentException.class, () -> planificador.planificar("Faro", null));
        assertThrows(IllegalArgumentException.class, () -> new PlanificadorViajes(null));
    }

    // ---------- RF 9: Qué buses tomar para llegar lo antes posible ----------

    @Test
    @DisplayName("RF 9: Terminal → Universidad toma Bus 2 y luego Bus 4")
    void rf09BusesTerminalUniversidad() {
        Ruta ruta = planificador.planificar("Terminal", "Universidad");
        assertArrayEquals(new String[] {"Bus 2", "Bus 4"}, ruta.getBuses());
        assertArrayEquals(new String[] {"Terminal", "Estadio", "Parque", "Universidad"}, recorrido(ruta));
        assertArrayEquals(new String[] {
            "Tomar Bus 2 de Terminal a Parque (9 min)",
            "Tomar Bus 4 de Parque a Universidad (2 min)"}, ruta.getInstrucciones());
    }

    @Test
    @DisplayName("RF 9: Faro → Terminal toma solo el Bus 2")
    void rf09BusesFaroTerminal() {
        Ruta ruta = planificador.planificar("Faro", "Terminal");
        assertArrayEquals(new String[] {"Bus 2"}, ruta.getBuses());
        assertArrayEquals(new String[] {"Faro", "Playa", "Terminal"}, recorrido(ruta));
    }

    @Test
    @DisplayName("RF 9 (límite): ante rutas más lentas de 13 min (Bus 1 o Bus 1+3) elige la de 11")
    void rf09EligeLaMasRapida() {
        Ruta ruta = planificador.planificar("Terminal", "Universidad");
        assertEquals(11, ruta.getMinutos());
        for (String bus : ruta.getBuses()) {
            assertFalse(bus.equals("Bus 1") || bus.equals("Bus 3"));
        }
    }

    @Test
    @DisplayName("RF 9 (inválido): destino inalcanzable lanza error")
    void rf09DestinoInalcanzable() {
        Grafo grafo = new Grafo();
        grafo.agregarTramoBus("Bus 1", "A", "B", 5);
        grafo.agregarVertice("C");
        PlanificadorViajes p = new PlanificadorViajes(grafo);
        assertThrows(IllegalStateException.class, () -> p.planificar("A", "C"));
        assertThrows(IllegalStateException.class, () -> p.planificar("B", "A"));
    }

    // ---------- RF 10: Indicar si debe caminar para llegar a tomar el bus ----------

    @Test
    @DisplayName("RF 10: Estadio → Hospital camina de Parque a Mercado para tomar el Bus 3 (18 min)")
    void rf10CaminarParaTomarBus() {
        Ruta ruta = planificador.planificar("Estadio", "Hospital");
        assertTrue(ruta.requiereCaminar());
        assertEquals(18, ruta.getMinutos());
        assertArrayEquals(new String[] {"Estadio", "Parque", "Mercado", "Hospital"}, recorrido(ruta));
        assertArrayEquals(new String[] {
            "Tomar Bus 2 de Estadio a Parque (4 min)",
            "Caminar de Parque a Mercado (10 min)",
            "Tomar Bus 3 de Mercado a Hospital (4 min)"}, ruta.getInstrucciones());
        assertArrayEquals(new String[] {"Bus 2", "Bus 3"}, ruta.getBuses());
    }

    @Test
    @DisplayName("RF 10 (límite): Terminal → Universidad no requiere caminar")
    void rf10SinCaminar() {
        assertFalse(planificador.planificar("Terminal", "Universidad").requiereCaminar());
        assertFalse(planificador.planificar("Faro", "Terminal").requiereCaminar());
    }

    @Test
    @DisplayName("RF 10 (límite): Parque → Mercado es solo caminata, sin buses")
    void rf10SoloCaminata() {
        Ruta ruta = planificador.planificar("Parque", "Mercado");
        assertTrue(ruta.requiereCaminar());
        assertEquals(0, ruta.getBuses().length);
        assertEquals(10, ruta.getMinutos());
    }

    @Test
    @DisplayName("RF 10 (inválido): una caminata con minutos no positivos se rechaza")
    void rf10CaminataInvalida() {
        Grafo grafo = new Grafo();
        assertThrows(IllegalArgumentException.class, () -> grafo.agregarCaminata("A", "B", 0));
        assertThrows(IllegalArgumentException.class, () -> grafo.agregarCaminata("A", "", 5));
    }

    // ---------- RF 11: Tiempo que tardará en llegar ----------

    @Test
    @DisplayName("RF 11: Terminal → Universidad tarda 11 min y Faro → Terminal 17 min")
    void rf11Minutos() {
        assertEquals(11, planificador.planificar("Terminal", "Universidad").getMinutos());
        assertEquals(17, planificador.planificar("Faro", "Terminal").getMinutos());
    }

    @Test
    @DisplayName("RF 11: los minutos son la suma exacta de los tramos")
    void rf11SumaDeTramos() {
        Ruta ruta = planificador.planificar("Universidad", "Terminal");
        int suma = 0;
        for (Tramo t : ruta.getTramos()) {
            suma += t.getMinutos();
        }
        assertEquals(26, ruta.getMinutos());
        assertEquals(suma, ruta.getMinutos());
    }

    @Test
    @DisplayName("RF 11 (límite): empate entre dos rutas de igual tiempo devuelve ese tiempo")
    void rf11Empate() {
        Grafo grafo = new Grafo();
        grafo.agregarTramoBus("Bus A", "X", "Y", 3);
        grafo.agregarTramoBus("Bus A", "Y", "Z", 3);
        grafo.agregarTramoBus("Bus B", "X", "Z", 6);
        assertEquals(6, new PlanificadorViajes(grafo).planificar("X", "Z").getMinutos());
    }

    @Test
    @DisplayName("RF 11 (inválido): un tramo con minutos negativos se rechaza")
    void rf11MinutosInvalidos() {
        Grafo grafo = new Grafo();
        assertThrows(IllegalArgumentException.class, () -> grafo.agregarTramoBus("Bus 1", "A", "B", -4));
    }

    // ---------- RF 12 / RNF 1: Los buses solo circulan en el sentido de su línea ----------

    @Test
    @DisplayName("RF 12: Universidad → Hospital no usa el Bus 3 al revés y tarda 36 min")
    void rf12SentidoDeLinea() {
        Ruta ruta = planificador.planificar("Universidad", "Hospital");
        assertEquals(36, ruta.getMinutos());
        assertArrayEquals(new String[] {"Universidad", "Faro", "Playa", "Terminal", "Mercado", "Hospital"},
                recorrido(ruta));
        for (Tramo t : ruta.getTramos()) {
            assertFalse(t.getDesde().equals("Universidad") && t.getHacia().equals("Hospital"));
        }
    }

    @Test
    @DisplayName("RNF 1: cada tramo de bus del grafo existe solo en su sentido (12 tramos de bus + 2 de caminata)")
    void rnf01GrafoDirigido() throws IOException {
        Grafo grafo = DatosPrueba.grafo();
        assertEquals(8, grafo.cantidadVertices());
        assertEquals(14, grafo.cantidadAristas());
        Tramo[] desdeHospital = grafo.tramosDesde(grafo.indiceDe("Hospital"));
        assertEquals(1, desdeHospital.length);
        assertEquals("Universidad", desdeHospital[0].getHacia());
        assertEquals(0, contarHacia(grafo, "Universidad", "Hospital"));
    }

    @Test
    @DisplayName("RF 12 (límite): A → B por bus existe, pero B → A no tiene ruta")
    void rf12SinRegreso() {
        Grafo grafo = new Grafo();
        grafo.agregarTramoBus("Bus 9", "A", "B", 4);
        PlanificadorViajes p = new PlanificadorViajes(grafo);
        assertEquals(4, p.planificar("A", "B").getMinutos());
        assertThrows(IllegalStateException.class, () -> p.planificar("B", "A"));
    }

    // ---------- RF 13: Caminando se puede ir en ambos sentidos ----------

    @Test
    @DisplayName("RF 13: Mercado ↔ Parque se camina en ambos sentidos en 10 min")
    void rf13CaminataAmbosSentidos() {
        Ruta ida = planificador.planificar("Mercado", "Parque");
        Ruta vuelta = planificador.planificar("Parque", "Mercado");
        assertEquals(10, ida.getMinutos());
        assertEquals(10, vuelta.getMinutos());
        assertTrue(ida.getTramos()[0].esCaminata());
        assertTrue(vuelta.getTramos()[0].esCaminata());
    }

    @Test
    @DisplayName("RF 13: Estadio → Hospital camina Parque → Mercado (sentido inverso al CSV) para abordar el Bus 3")
    void rf13CaminataParaAbordar() {
        Ruta ruta = planificador.planificar("Estadio", "Hospital");
        Tramo caminata = ruta.getTramos()[1];
        assertTrue(caminata.esCaminata());
        assertEquals("Parque", caminata.getDesde());
        assertEquals("Mercado", caminata.getHacia());
        assertEquals("Bus 3", ruta.getTramos()[2].getLinea());
    }

    @Test
    @DisplayName("RF 13 (límite): Mercado → Faro empata en 16 min (Bus 1, o caminar + Bus 2)")
    void rf13EmpateConCaminata() {
        Ruta ruta = planificador.planificar("Mercado", "Faro");
        assertEquals(16, ruta.getMinutos());
        String[] r = recorrido(ruta);
        assertEquals(3, r.length);
        assertTrue(r[1].equals("Universidad") || r[1].equals("Parque"));
    }

    @Test
    @DisplayName("RF 13 (inválido): caminar a una parada inexistente lanza error")
    void rf13Invalido() {
        assertThrows(IllegalArgumentException.class, () -> planificador.planificar("Mercado", "Muelle"));
    }

    private static int contarHacia(Grafo grafo, String desde, String hacia) {
        int c = 0;
        for (Tramo t : grafo.tramosDesde(grafo.indiceDe(desde))) {
            if (t.getHacia().equals(hacia)) {
                c++;
            }
        }
        return c;
    }
}
