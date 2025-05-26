package modelo;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
	    public static String host;
	    public static int port;

	    static {
	        try {
	            Properties props = new Properties();
	            props.load(new FileInputStream("config.properties"));
	            host = props.getProperty("server.host");
	            port = Integer.parseInt(props.getProperty("server.port"));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}
