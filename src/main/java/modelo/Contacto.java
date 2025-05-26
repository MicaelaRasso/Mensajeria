package modelo;

public class Contacto {
    private String nombre;
    private Conversacion conversacion;

    public Contacto(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters
    public Conversacion getConversacion() {
        return conversacion;
    }

    public void setConversacion(Conversacion conversacion) {
        this.conversacion = conversacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
