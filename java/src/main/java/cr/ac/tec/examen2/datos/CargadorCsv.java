package cr.ac.tec.examen2.datos;

import cr.ac.tec.examen2.estructuras.ArbolDirectorio;
import cr.ac.tec.examen2.estructuras.Grafo;
import cr.ac.tec.examen2.modelo.Parada;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * Lee los CSV de datos/version-a:
 * abordajes.csv (codigo,parada,zona,abordajes) y tramos.csv (linea,desde,hacia,minutos).
 * Cualquier línea mal formada produce IllegalArgumentException con el número de línea.
 */
public final class CargadorCsv {

    public static final String ENCABEZADO_ABORDAJES = "codigo,parada,zona,abordajes";
    public static final String ENCABEZADO_TRAMOS = "linea,desde,hacia,minutos";

    private CargadorCsv() {
    }

    public static ArbolDirectorio cargarDirectorio(String nombreCiudad, String rutaArchivo) throws IOException {
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo, StandardCharsets.UTF_8))) {
            return leerDirectorio(nombreCiudad, lector);
        }
    }

    public static ArbolDirectorio directorioDesdeTexto(String nombreCiudad, String contenido) {
        try (BufferedReader lector = new BufferedReader(new StringReader(contenido))) {
            return leerDirectorio(nombreCiudad, lector);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Grafo cargarGrafo(String rutaArchivo) throws IOException {
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo, StandardCharsets.UTF_8))) {
            return leerGrafo(lector);
        }
    }

    public static Grafo grafoDesdeTexto(String contenido) {
        try (BufferedReader lector = new BufferedReader(new StringReader(contenido))) {
            return leerGrafo(lector);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ArbolDirectorio leerDirectorio(String nombreCiudad, BufferedReader lector) throws IOException {
        validarEncabezado(lector.readLine(), ENCABEZADO_ABORDAJES);
        ArbolDirectorio directorio = new ArbolDirectorio(nombreCiudad);
        String linea;
        int numero = 1;
        while ((linea = lector.readLine()) != null) {
            numero++;
            if (linea.isBlank()) {
                continue;
            }
            String[] c = columnas(linea, numero);
            int abordajes = entero(c[3], numero);
            Parada parada;
            try {
                parada = new Parada(c[0], c[1], c[2], abordajes);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Línea " + numero + ": " + e.getMessage());
            }
            directorio.agregarZona(parada.getZona());
            try {
                directorio.agregarParada(parada.getZona(), parada);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Línea " + numero + ": " + e.getMessage());
            }
        }
        return directorio;
    }

    private static Grafo leerGrafo(BufferedReader lector) throws IOException {
        validarEncabezado(lector.readLine(), ENCABEZADO_TRAMOS);
        Grafo grafo = new Grafo();
        String linea;
        int numero = 1;
        while ((linea = lector.readLine()) != null) {
            numero++;
            if (linea.isBlank()) {
                continue;
            }
            String[] c = columnas(linea, numero);
            int minutos = entero(c[3], numero);
            try {
                if (c[0].trim().startsWith("Caminata")) {
                    grafo.agregarCaminata(c[1], c[2], minutos);
                } else {
                    grafo.agregarTramoBus(c[0], c[1], c[2], minutos);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Línea " + numero + ": " + e.getMessage());
            }
        }
        return grafo;
    }

    private static void validarEncabezado(String encabezado, String esperado) {
        if (encabezado == null) {
            throw new IllegalArgumentException("El archivo está vacío; se esperaba: " + esperado);
        }
        String limpio = encabezado.replace("﻿", "").trim();
        if (!limpio.equals(esperado)) {
            throw new IllegalArgumentException("Encabezado inválido: '" + limpio + "'; se esperaba: " + esperado);
        }
    }

    private static String[] columnas(String linea, int numero) {
        String[] c = linea.split(",", -1);
        if (c.length != 4) {
            throw new IllegalArgumentException("Línea " + numero + ": se esperaban 4 columnas y hay " + c.length);
        }
        return c;
    }

    private static int entero(String texto, int numero) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Línea " + numero + ": número inválido '" + texto + "'");
        }
    }
}
