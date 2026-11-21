package cr.ac.tec.examen2.algoritmos;

import cr.ac.tec.examen2.modelo.Parada;

/**
 * F2. Merge Sort propio para el reporte de pasajeros.
 * Orden: de más a menos abordajes; en caso de empate, por código de la A a la Z.
 * Divide siempre a la mitad, por lo que su tiempo no depende del orden de la entrada.
 */
public final class MergeSort {

    private long comparaciones;

    /**
     * Devuelve un arreglo nuevo ordenado; no modifica el arreglo recibido.
     *
     * @throws IllegalArgumentException si el arreglo o alguno de sus elementos es nulo.
     */
    public Parada[] ordenar(Parada[] paradas) {
        if (paradas == null) {
            throw new IllegalArgumentException("El arreglo de paradas no puede ser nulo");
        }
        comparaciones = 0;
        Parada[] datos = new Parada[paradas.length];
        for (int i = 0; i < paradas.length; i++) {
            if (paradas[i] == null) {
                throw new IllegalArgumentException("La parada en la posición " + i + " es nula");
            }
            datos[i] = paradas[i];
        }
        Parada[] auxiliar = new Parada[datos.length];
        ordenar(datos, auxiliar, 0, datos.length - 1);
        return datos;
    }

    /** Comparaciones realizadas en la última llamada a {@link #ordenar(Parada[])}. */
    public long getComparaciones() {
        return comparaciones;
    }

    /** Criterio del reporte: negativo si a va antes que b. */
    public static int comparar(Parada a, Parada b) {
        if (a.getAbordajes() != b.getAbordajes()) {
            return a.getAbordajes() > b.getAbordajes() ? -1 : 1;
        }
        return a.getCodigo().compareTo(b.getCodigo());
    }

    private void ordenar(Parada[] datos, Parada[] aux, int inicio, int fin) {
        if (inicio >= fin) {
            return;
        }
        int medio = inicio + (fin - inicio) / 2;
        ordenar(datos, aux, inicio, medio);
        ordenar(datos, aux, medio + 1, fin);
        mezclar(datos, aux, inicio, medio, fin);
    }

    private void mezclar(Parada[] datos, Parada[] aux, int inicio, int medio, int fin) {
        for (int k = inicio; k <= fin; k++) {
            aux[k] = datos[k];
        }
        int i = inicio;
        int j = medio + 1;
        int k = inicio;
        while (i <= medio && j <= fin) {
            comparaciones++;
            if (comparar(aux[i], aux[j]) <= 0) {
                datos[k++] = aux[i++];
            } else {
                datos[k++] = aux[j++];
            }
        }
        while (i <= medio) {
            datos[k++] = aux[i++];
        }
        while (j <= fin) {
            datos[k++] = aux[j++];
        }
    }
}
