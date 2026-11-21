package cr.ac.tec.examen2.modelo;

/**
 * Parada de bus: código único, nombre, zona a la que pertenece y abordajes diarios.
 */
public class Parada {

    private final String codigo;
    private final String nombre;
    private final String zona;
    private final int abordajes;

    public Parada(String codigo, String nombre, String zona, int abordajes) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la parada no puede estar vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la parada no puede estar vacío");
        }
        if (zona == null || zona.isBlank()) {
            throw new IllegalArgumentException("La zona de la parada no puede estar vacía");
        }
        if (abordajes < 0) {
            throw new IllegalArgumentException("Los abordajes no pueden ser negativos: " + abordajes);
        }
        this.codigo = codigo.trim();
        this.nombre = nombre.trim();
        this.zona = zona.trim();
        this.abordajes = abordajes;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getZona() {
        return zona;
    }

    public int getAbordajes() {
        return abordajes;
    }

    @Override
    public String toString() {
        return codigo + " " + nombre + " (" + zona + ") " + abordajes;
    }
}
