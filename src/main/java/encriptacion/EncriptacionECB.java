package encriptacion;

import java.util.Base64;

import javax.crypto.Cipher;

import javax.crypto.spec.SecretKeySpec;

import modelo.ConfigLoader;

public class EncriptacionECB extends EncriptacionStrategy {
	
    private static final String ALGO = "AES/ECB/PKCS5Padding";
    
	public EncriptacionECB() {
		super();
	}

	@Override
	public String encriptar(String input) {
		try {			
			SecretKeySpec secretKey = new SecretKeySpec(ConfigLoader.key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encrypted = cipher.doFinal(input.getBytes("UTF-8"));
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error de encriptacion del emisor";
	}

	@Override
	public String desencriptar(String input){
		try {			
			SecretKeySpec secretKey = new SecretKeySpec(ConfigLoader.key.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decoded = Base64.getDecoder().decode(input);
			byte[] decrypted = cipher.doFinal(decoded);
			return new String(decrypted, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error de desencriptacion";
	}

}
