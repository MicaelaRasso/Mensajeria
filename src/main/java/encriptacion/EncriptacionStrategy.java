package encriptacion;

public abstract class EncriptacionStrategy {
	
	public abstract String encriptar(String input);
	
	public abstract String desencriptar(String input);
}