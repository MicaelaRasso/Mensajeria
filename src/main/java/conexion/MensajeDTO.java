package conexion;

public class MensajeDTO extends ContenidoC {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mensaje;
	private UsuarioDTO receptor;
	private String encriptacion;
	

	public MensajeDTO(UsuarioDTO emisor, String mensaje, UsuarioDTO receptor, String encriptacion) {
		super(emisor);
		this.mensaje = mensaje;
		this.receptor = receptor;
		this.encriptacion = encriptacion;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public UsuarioDTO getReceptor() {
		return receptor;
	}

	public void setReceptor(UsuarioDTO receptor) {
		this.receptor = receptor;
	}

	@Override
	public String toString() {
		return " mensaje:" + mensaje + " receptor: " + receptor;
	}

	public String getEncriptacion() {
		return encriptacion;
	}

	public void setEncriptacion(String encriptacion) {
		this.encriptacion = encriptacion;
	}

}
