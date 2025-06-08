package encriptacion;
import java.util.Base64;

import modelo.ConfigLoader;

public class EncriptacionXOR extends EncriptacionStrategy{

	public EncriptacionXOR() {
		super();
	}
	
	@Override
	public String encriptar(String input) {
        byte[] inputBytes = input.getBytes();
        byte[] keyBytes = ConfigLoader.key.getBytes();
        byte[] result = new byte[inputBytes.length];

        for (int i = 0; i < inputBytes.length; i++) {
            result[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return Base64.getEncoder().encodeToString(result);
	}

	@Override
	public String desencriptar(String input) {
        byte[] inputBytes = Base64.getDecoder().decode(input);
        byte[] keyBytes = ConfigLoader.key.getBytes();
        byte[] result = new byte[inputBytes.length];

        for (int i = 0; i < inputBytes.length; i++) {
            result[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return new String(result);
	}

}
