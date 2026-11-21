package cr.ac.tec.examen2.estructuras;

import cr.ac.tec.examen2.modelo.Parada;

/**
 * F1. Directorio de paradas como árbol n-ario de 4 niveles:
 * RutaBus (raíz) → Ciudad → Zona → Parada.
 */
public class ArbolDirectorio {

    public static final String NOMBRE_RAIZ = "RutaBus";

    private final NodoArbol raiz;
    private final NodoArbol ciudad;

    public ArbolDirectorio(String nombreCiudad) {
        if (nombreCiudad == null || nombreCiudad.isBlank()) {
            throw new IllegalArgumentException("El nombre de la ciudad no puede estar vacío");
        }
        raiz = new NodoArbol(NodoArbol.Nivel.RUTABUS, NOMBRE_RAIZ);
        ciudad = new NodoArbol(NodoArbol.Nivel.CIUDAD, nombreCiudad.trim());
        raiz.agregarHijo(ciudad);
    }

    public NodoArbol getRaiz() {
        return raiz;
    }

    public NodoArbol getCiudad() {
        return ciudad;
    }

    /** Registra una zona en la ciudad. Si ya existe, no hace nada. */
    public void agregarZona(String nombreZona) {
        if (nombreZona == null || nombreZona.isBlank()) {
            throw new IllegalArgumentException("El nombre de la zona no puede estar vacío");
        }
        if (ciudad.buscarHijo(nombreZona.trim()) == null) {
            ciudad.agregarHijo(new NodoArbol(NodoArbol.Nivel.ZONA, nombreZona.trim()));
        }
    }

    public boolean existeZona(String nombreZona) {
        return nombreZona != null && ciudad.buscarHijo(nombreZona.trim()) != null;
    }

    /**
     * RF 2. Agrega una parada a una zona existente.
     *
     * @throws IllegalArgumentException si la zona no existe, si la parada es nula,
     *         si su zona no coincide o si el código ya está registrado.
     */
    public void agregarParada(String nombreZona, Parada parada) {
        if (parada == null) {
            throw new IllegalArgumentException("La parada no puede ser nula");
        }
        NodoArbol zona = buscarZona(nombreZona);
        if (!parada.getZona().equals(zona.getNombre())) {
            throw new IllegalArgumentException("La parada " + parada.getCodigo()
                    + " pertenece a la zona " + parada.getZona() + ", no a " + zona.getNombre());
        }
        if (buscarParada(parada.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe una parada con código " + parada.getCodigo());
        }
        zona.agregarHijo(new NodoArbol(NodoArbol.Nivel.PARADA, parada.getNombre(), parada));
    }

    /** RF 3. Lista las paradas registradas en una zona, en orden de registro. */
    public Parada[] listarParadas(String nombreZona) {
        NodoArbol zona = buscarZona(nombreZona);
        Parada[] paradas = new Parada[zona.cantidadHijos()];
        for (int i = 0; i < paradas.length; i++) {
            paradas[i] = zona.getHijo(i).getParada();
        }
        return paradas;
    }

    /** RF 4. Total de abordajes diarios de una zona. */
    public long totalAbordajesZona(String nombreZona) {
        return buscarZona(nombreZona).totalAbordajes();
    }

    /** RF 4. Total de abordajes diarios de toda la ciudad. */
    public long totalAbordajesCiudad() {
        return ciudad.totalAbordajes();
    }

    /** Nombres de las zonas en orden de registro. */
    public String[] nombresZonas() {
        String[] nombres = new String[ciudad.cantidadHijos()];
        for (int i = 0; i < nombres.length; i++) {
            nombres[i] = ciudad.getHijo(i).getNombre();
        }
        return nombres;
    }

    /** Todas las paradas de la ciudad (recorrido de las zonas en orden). */
    public Parada[] todasLasParadas() {
        Lista<Parada> lista = new Lista<>();
        for (int i = 0; i < ciudad.cantidadHijos(); i++) {
            NodoArbol zona = ciudad.getHijo(i);
            for (int j = 0; j < zona.cantidadHijos(); j++) {
                lista.agregar(zona.getHijo(j).getParada());
            }
        }
        return lista.aArreglo(new Parada[0]);
    }

    /** Busca una parada por código en todo el directorio; null si no existe. */
    public Parada buscarParada(String codigo) {
        if (codigo == null) {
            return null;
        }
        for (int i = 0; i < ciudad.cantidadHijos(); i++) {
            NodoArbol zona = ciudad.getHijo(i);
            for (int j = 0; j < zona.cantidadHijos(); j++) {
                Parada p = zona.getHijo(j).getParada();
                if (p.getCodigo().equals(codigo.trim())) {
                    return p;
                }
            }
        }
        return null;
    }

    private NodoArbol buscarZona(String nombreZona) {
        if (nombreZona == null || nombreZona.isBlank()) {
            throw new IllegalArgumentException("El nombre de la zona no puede estar vacío");
        }
        NodoArbol zona = ciudad.buscarHijo(nombreZona.trim());
        if (zona == null) {
            throw new IllegalArgumentException("No existe la zona: " + nombreZona);
        }
        return zona;
    }
}
