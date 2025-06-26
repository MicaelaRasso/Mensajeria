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
	    public static String algo;
	    public static String cpath;

	    static {
	        try {
	            Properties props = new Properties();
	            props.load(new FileInputStream("config.properties"));
	            
	            host = props.getProperty("monitor.host");
	            port = Integer.parseInt(props.getProperty("monitor.port"));
	            path = props.getProperty("path");
	            cpath = props.getProperty("cpath");
	            persistencia = props.getProperty("persistencia");
	            algo = props.getProperty("algo");
	            key = props.getProperty("key");
	            iv = props.getProperty("iv");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}
