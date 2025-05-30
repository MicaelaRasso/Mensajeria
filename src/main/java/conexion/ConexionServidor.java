package conexion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import modelo.Sistema;
import conexion.Paquete;
import conexion.MensajeDTO;
import conexion.UsuarioDTO;

/**
 * Maneja la conexión al Servidor. Se auto-inicia tras recibir datos del monitor.
 */
public class ConexionServidor implements Runnable {
    private final Sistema sistema;
    private final String host;
    private final int port;
    private Thread thread;
    private volatile boolean running = true;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ConexionServidor(Sistema sistema, String host, int port) {
        this.sistema = sistema;
        this.host = host;
        this.port = port;
        this.thread = new Thread(this, "ServerConnectionThread");
    }

    /**
     * Inicia el hilo de conexión.
     */
    public void start() {
        thread.start();
    }

    /**
     * Detiene la conexión y el hilo.
     */
    public void stop() {
        running = false;
        thread.interrupt();
        close();
    }

    /**
     * Envía el paquete de registro de usuario.
     */
    public void registrarUsuario(Paquete paqueteRegistro) {
    	System.out.println(paqueteRegistro.toString());
        send(paqueteRegistro);
        
    }

    /**
     * Envía un mensaje al servidor.
     */
    public void enviarMensaje(String receptor, String texto) {
    	//emisor, mensaje, receptor
    	
    	UsuarioDTO emisorDTO = new UsuarioDTO(sistema.getUsuario().getNombre());
    	UsuarioDTO receptorDTO = new UsuarioDTO(sistema.getContacto(receptor).getNombre());
        send(new Paquete("enviarM", new MensajeDTO(emisorDTO, texto, receptorDTO)));
    }

    /**
     * Envía solicitud de agregar contacto.
     */
    public void agregarContacto(Paquete paqueteContacto) {
        send(paqueteContacto);
    }

    private void send(Paquete paquete) {
        try {
            if (out != null) out.writeObject(paquete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            while (running && !thread.isInterrupted()) {
                Paquete paquete = (Paquete) in.readObject();
                sistema.recibePaqueteDeServidor(paquete);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignore) {}
    }
}