package conexion;

import modelo.Usuario;

public class UsuarioDTO extends ContenidoC {
	private String address;
	private String respuesta;

	public UsuarioDTO(Usuario emisor, String address, String respuesta) {
		super(emisor);
		this.address = address;
		this.respuesta = respuesta;
	}
	
	public UsuarioDTO(Usuario emisor) {
		super(emisor);
		this.address = "";
		this.respuesta = "";
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}
}
