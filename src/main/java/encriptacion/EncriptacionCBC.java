package encriptacion;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import modelo.ConfigLoader;

public class EncriptacionCBC extends EncriptacionStrategy {
	
    private static final String ALGO = "AES/CBC/PKCS5Padding";
    
    private SecretKey key;
	
	public EncriptacionCBC() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String encriptar(String input) {
		try {			
			IvParameterSpec iv = getIVFromString(ConfigLoader.iv);
			key = getSecretKeyFromString(ConfigLoader.key);
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
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
			
			IvParameterSpec iv = getIVFromString(ConfigLoader.iv);
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			byte[] decoded = Base64.getDecoder().decode(input);
			byte[] decrypted = cipher.doFinal(decoded);
			return new String(decrypted, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Error de desencriptacion";
	}
	
	public static SecretKey getSecretKeyFromString(String keyString) throws Exception {
	    MessageDigest sha = MessageDigest.getInstance("SHA-256");
	    byte[] keyBytes = sha.digest(keyString.getBytes("UTF-8"));
	    return new SecretKeySpec(keyBytes, 0, 16, "AES"); // AES-128 (usa 16 bytes)
	}
	
	public static IvParameterSpec getIVFromString(String ivSeed) throws Exception {
	    MessageDigest sha = MessageDigest.getInstance("SHA-256");
	    byte[] ivBytes = sha.digest(ivSeed.getBytes("UTF-8"));
	    return new IvParameterSpec(ivBytes, 0, 16);
	}
}
