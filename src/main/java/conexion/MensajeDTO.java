package conexion;

import modelo.Usuario;

public class MensajeDTO extends ContenidoC {
	private String mensaje;
	private Usuario receptor;
	

	public MensajeDTO(Usuario emisor, String mensaje, Usuario receptor) {
		super(emisor);
		this.setMensaje(mensaje);
		this.setReceptor(receptor);
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Usuario getReceptor() {
		return receptor;
	}

	public void setReceptor(Usuario receptor) {
		this.receptor = receptor;
	}

}
