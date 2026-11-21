package cr.ac.tec.examen2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cr.ac.tec.examen2.estructuras.Lista;
import cr.ac.tec.examen2.estructuras.MonticuloMin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Estructuras propias que sustituyen a java.util. */
class EstructurasTest {

    @Test
    @DisplayName("Lista: crece más allá de su capacidad inicial y conserva el orden")
    void listaCrece() {
        Lista<Integer> lista = new Lista<>(1);
        for (int i = 0; i < 100; i++) {
            lista.agregar(i);
        }
        assertEquals(100, lista.tamano());
        assertEquals(0, lista.obtener(0));
        assertEquals(99, lista.obtener(99));
        assertEquals(100, lista.aArreglo(new Integer[0]).length);
    }

    @Test
    @DisplayName("Lista: vacía y acceso fuera de rango")
    void listaLimites() {
        Lista<String> lista = new Lista<>();
        assertTrue(lista.estaVacia());
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(0));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(-1));
    }

    @Test
    @DisplayName("Montículo: extrae en orden de distancia ascendente")
    void monticuloOrden() {
        MonticuloMin m = new MonticuloMin(1);
        int[] distancias = {9, 3, 7, 1, 5, 11, 0};
        for (int i = 0; i < distancias.length; i++) {
            m.insertar(i, distancias[i]);
        }
        assertEquals(7, m.tamano());
        assertEquals(0, m.distanciaMinima());
        int[] esperado = {6, 3, 1, 4, 2, 0, 5};
        for (int v : esperado) {
            assertEquals(v, m.extraerMinimo());
        }
        assertTrue(m.estaVacio());
    }

    @Test
    @DisplayName("Montículo: extraer de un montículo vacío lanza error")
    void monticuloVacio() {
        MonticuloMin m = new MonticuloMin(4);
        assertThrows(IllegalStateException.class, m::extraerMinimo);
        assertThrows(IllegalStateException.class, m::distanciaMinima);
    }
}
