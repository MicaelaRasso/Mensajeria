package conexion;

import modelo.Usuario;

public class UsuarioDTO extends ContenidoC {
	private String address;

	public UsuarioDTO(Usuario emisor, String address) {
		super(emisor);
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
