package conexion;

import java.time.LocalDateTime;

public abstract class Contenido {
	private LocalDateTime fechaYHora;
	
	public Contenido () {
		this.fechaYHora = LocalDateTime.now();
	}

	public LocalDateTime getFechaYHora() {
		return fechaYHora;
	}

	public void setFechaYHora(LocalDateTime fechaYHora) {
		this.fechaYHora = fechaYHora;
	}
	
	

}
