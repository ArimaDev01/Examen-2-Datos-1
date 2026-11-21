package cr.ac.tec.examen2;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cr.ac.tec.examen2.estructuras.ArbolDirectorio;
import cr.ac.tec.examen2.estructuras.NodoArbol;
import cr.ac.tec.examen2.modelo.Parada;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** F1. Directorio de paradas: requerimientos funcionales 1 a 4. */
class DirectorioParadasTest {

    private ArbolDirectorio directorio;

    @BeforeEach
    void cargar() throws IOException {
        directorio = DatosPrueba.directorio();
    }

    private static String[] nombres(Parada[] paradas) {
        String[] n = new String[paradas.length];
        for (int i = 0; i < paradas.length; i++) {
            n[i] = paradas[i].getNombre();
        }
        return n;
    }

    // ---------- RF 1: Jerarquía RutaBus → Ciudad → Zona → Parada (árbol de 4 niveles) ----------

    @Test
    @DisplayName("RF 1: el árbol tiene los 4 niveles RutaBus, Ciudad, Zona y Parada")
    void rf01CuatroNiveles() {
        NodoArbol raiz = directorio.getRaiz();
        assertEquals(NodoArbol.Nivel.RUTABUS, raiz.getNivel());
        assertEquals("RutaBus", raiz.getNombre());
        assertEquals(1, raiz.cantidadHijos());

        NodoArbol ciudad = raiz.getHijo(0);
        assertEquals(NodoArbol.Nivel.CIUDAD, ciudad.getNivel());
        assertEquals("Puerto Celeste", ciudad.getNombre());
        assertEquals(3, ciudad.cantidadHijos());

        NodoArbol centro = ciudad.getHijo(0);
        assertEquals(NodoArbol.Nivel.ZONA, centro.getNivel());
        assertEquals("Centro", centro.getNombre());
        assertEquals(3, centro.cantidadHijos());

        NodoArbol terminal = centro.getHijo(0);
        assertEquals(NodoArbol.Nivel.PARADA, terminal.getNivel());
        assertEquals("S01", terminal.getParada().getCodigo());
        assertEquals(0, terminal.cantidadHijos());
    }

    @Test
    @DisplayName("RF 1: cada parada queda bajo la zona que le corresponde")
    void rf01ParadaBajoSuZona() {
        assertArrayEquals(new String[] {"Centro", "Norte", "Costa"}, directorio.nombresZonas());
        for (String zona : directorio.nombresZonas()) {
            for (Parada p : directorio.listarParadas(zona)) {
                assertEquals(zona, p.getZona());
            }
        }
    }

    @Test
    @DisplayName("RF 1 (límite): una ciudad recién creada solo tiene raíz y ciudad, sin zonas")
    void rf01CiudadVacia() {
        ArbolDirectorio vacio = new ArbolDirectorio("Puerto Celeste");
        assertEquals(1, vacio.getRaiz().cantidadHijos());
        assertEquals(0, vacio.getCiudad().cantidadHijos());
        assertEquals(0, vacio.nombresZonas().length);
    }

    @Test
    @DisplayName("RF 1 (inválido): una ciudad sin nombre se rechaza")
    void rf01CiudadSinNombre() {
        assertThrows(IllegalArgumentException.class, () -> new ArbolDirectorio(" "));
        assertThrows(IllegalArgumentException.class, () -> new ArbolDirectorio(null));
    }

    // ---------- RF 2: Agregar paradas a las zonas ----------

    @Test
    @DisplayName("RF 2: agregar una parada a Costa la deja de última en la zona")
    void rf02AgregarParada() {
        Parada muelle = new Parada("S09", "Muelle", "Costa", 300);
        directorio.agregarParada("Costa", muelle);

        Parada[] costa = directorio.listarParadas("Costa");
        assertEquals(3, costa.length);
        assertSame(muelle, costa[2]);
        assertEquals(1580, directorio.totalAbordajesZona("Costa"));
        assertSame(muelle, directorio.buscarParada("S09"));
    }

    @Test
    @DisplayName("RF 2 (límite): agregar la primera parada a una zona vacía")
    void rf02PrimeraParadaEnZonaVacia() {
        directorio.agregarZona("Sur");
        assertEquals(0, directorio.listarParadas("Sur").length);
        directorio.agregarParada("Sur", new Parada("S10", "Aeropuerto", "Sur", 50));
        assertEquals(1, directorio.listarParadas("Sur").length);
        assertEquals(50, directorio.totalAbordajesZona("Sur"));
    }

    @Test
    @DisplayName("RF 2 (inválido): zona inexistente, código repetido, zona distinta o parada nula")
    void rf02Invalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> directorio.agregarParada("Oeste", new Parada("S20", "X", "Oeste", 1)));
        assertThrows(IllegalArgumentException.class,
                () -> directorio.agregarParada("Centro", new Parada("S01", "Otra", "Centro", 1)));
        assertThrows(IllegalArgumentException.class,
                () -> directorio.agregarParada("Centro", new Parada("S21", "Y", "Norte", 1)));
        assertThrows(IllegalArgumentException.class,
                () -> directorio.agregarParada("Centro", null));
        assertEquals(3, directorio.listarParadas("Centro").length);
    }

    @Test
    @DisplayName("RF 2 (inválido): una parada con abordajes negativos o sin código no se puede crear")
    void rf02ParadaMalFormada() {
        assertThrows(IllegalArgumentException.class, () -> new Parada("S30", "Z", "Centro", -1));
        assertThrows(IllegalArgumentException.class, () -> new Parada("", "Z", "Centro", 1));
    }

    // ---------- RF 3: Listar las paradas registradas en una zona ----------

    @Test
    @DisplayName("RF 3: las paradas de cada zona, en el orden en que se registraron")
    void rf03ListarParadas() {
        assertArrayEquals(new String[] {"Terminal", "Mercado", "Hospital"},
                nombres(directorio.listarParadas("Centro")));
        assertArrayEquals(new String[] {"Universidad", "Estadio", "Parque"},
                nombres(directorio.listarParadas("Norte")));
        assertArrayEquals(new String[] {"Faro", "Playa"},
                nombres(directorio.listarParadas("Costa")));
    }

    @Test
    @DisplayName("RF 3 (límite): una zona vacía devuelve un arreglo de largo 0")
    void rf03ZonaVacia() {
        directorio.agregarZona("Sur");
        assertEquals(0, directorio.listarParadas("Sur").length);
    }

    @Test
    @DisplayName("RF 3 (inválido): listar una zona inexistente o vacía de nombre lanza error")
    void rf03ZonaInexistente() {
        assertThrows(IllegalArgumentException.class, () -> directorio.listarParadas("Oeste"));
        assertThrows(IllegalArgumentException.class, () -> directorio.listarParadas(""));
        assertNull(directorio.buscarParada("S99"));
    }

    // ---------- RF 4: Total de abordajes diarios de una zona o de toda la ciudad ----------

    @Test
    @DisplayName("RF 4: totales exactos por zona y de la ciudad")
    void rf04Totales() {
        assertEquals(3810, directorio.totalAbordajesZona("Centro"));
        assertEquals(2745, directorio.totalAbordajesZona("Norte"));
        assertEquals(1280, directorio.totalAbordajesZona("Costa"));
        assertEquals(7835, directorio.totalAbordajesCiudad());
    }

    @Test
    @DisplayName("RF 4 (límite): zona vacía suma 0 y una ciudad sin zonas suma 0")
    void rf04TotalesVacios() {
        directorio.agregarZona("Sur");
        assertEquals(0, directorio.totalAbordajesZona("Sur"));
        assertEquals(7835, directorio.totalAbordajesCiudad());
        assertEquals(0, new ArbolDirectorio("Vacía").totalAbordajesCiudad());
    }

    @Test
    @DisplayName("RF 4 (inválido): total de una zona inexistente lanza error")
    void rf04ZonaInexistente() {
        assertThrows(IllegalArgumentException.class, () -> directorio.totalAbordajesZona("Oeste"));
        assertThrows(IllegalArgumentException.class, () -> directorio.totalAbordajesZona(null));
    }
}
