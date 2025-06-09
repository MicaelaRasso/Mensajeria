package modelo;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
	    public static String host;
	    public static int port;
	    public static String key;
	    public static String persistencia;
	    public static String iv;
	    public static String path;

	    static {
	        try {
	            Properties props = new Properties();
	            props.load(new FileInputStream("config.properties"));
	            
	            host = props.getProperty("monitor.host");
	            port = Integer.parseInt(props.getProperty("monitor.port"));
	            path = props.getProperty("path");
	            persistencia = props.getProperty("persistencia");
	            key = props.getProperty("key");
	            iv = props.getProperty("iv");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}
