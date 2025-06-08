package encriptacion;

public class Encriptacion {
	
	private EncriptacionStrategy estrategia;
	private String estrategiaSeleccionada;
	
	public Encriptacion() {
		estrategia = new EncriptacionXOR();
	}
	
	public void establecerEstrategia(String metodo) {
		switch(metodo){
		case "xor": establecerXOR();
		break;
		case "ecb": establecerECB();
		break;
		case "cbc": establecerCBC();
		break;
		default: sinEncriptacion();
		
		}
	}
	
	//Nivel bajo de seguridad
	public void establecerXOR() {
		estrategiaSeleccionada = "xor";
		estrategia = new EncriptacionXOR();
	}
	
	//Nivel medio de seguridad
	public void establecerECB() {
		estrategiaSeleccionada = "ecb";
		estrategia = new EncriptacionECB();
	}
	
	//Nivel alto de seguridad
	public void establecerCBC() {
		estrategiaSeleccionada = "cbc";
		estrategia = new EncriptacionCBC();
	}
	
	public void sinEncriptacion() {
		estrategia = new SinEncriptacion();
	}
	
	public String encriptar(String texto) {
		return estrategia.encriptar(texto);
	}
	
	public String desencriptar(String texto) {
		return estrategia.desencriptar(texto);
	}
	
	public String getEstrategia() {
		return estrategiaSeleccionada;
	}
	
}
