package conexion;

import java.io.Serializable;

public class Paquete implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String operacion;
    private Contenido contenido;
    private String encriptacion;

    public Paquete() {}
    
    public Paquete(String operacion, String encriptacion, Contenido contenido) {
		this.operacion = operacion;
		this.contenido = contenido;
		this.setEncriptacion(encriptacion);
	}

	// Getters y Setters
    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public Contenido getContenido() {
        return contenido;
    }

    public void setContenido(Contenido contenido) {
        this.contenido = contenido;
    }

	@Override
	public String toString() {
		return "[Paquete] op: " + operacion + " contenido: " + contenido;
	}

	public String getEncriptacion() {
		return encriptacion;
	}

	public void setEncriptacion(String encriptacion) {
		this.encriptacion = encriptacion;
	}
    
    
}
