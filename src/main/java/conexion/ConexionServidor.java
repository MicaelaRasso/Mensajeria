package conexion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import modelo.Sistema;
import conexion.Paquete;
import conexion.MensajeDTO;
import conexion.UsuarioDTO;
import encriptacion.Encriptacion;

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
    private Encriptacion encriptacion;
    private Thread pingThread;
    private volatile boolean echo = false;
    
    public ConexionServidor(Sistema sistema, String host, int port) {
        this.sistema = sistema;
        this.host = host;
        this.port = port;
        this.encriptacion = sistema.encriptacion;
        this.thread = new Thread(this, "ServerConnectionThread");
    }

    /**
     * Inicia el hilo de conexión.
     */
    public void start() {
        thread.start();
        startPingLoop();
    }

    /**
     * Detiene la conexión y el hilo.
     */
    public void stop() {
        if (!running) return;
        	running = false;
        thread.interrupt();
        if (pingThread != null) 
        	pingThread.interrupt();
        close();
    }

    
    /**
     * Envía el paquete de registro de usuario.
     */
    public void registrarUsuario(Paquete paqueteRegistro) {
    	System.out.println(paqueteRegistro.toString());
        send(paqueteRegistro);
        dormir(500);
    }

	/**
     * Envía un mensaje al servidor.
     */
    public void enviarMensaje(String receptor, String texto) {
    	//emisor, mensaje, receptor
    	
    	UsuarioDTO emisorDTO = new UsuarioDTO(sistema.getUsuario().getNombre());
    	UsuarioDTO receptorDTO = new UsuarioDTO(receptor);
        send(new Paquete("enviarM",encriptacion.getEstrategia(), new MensajeDTO(emisorDTO, encriptacion.encriptar(texto), receptorDTO)));
    }

    /**
     * Envía solicitud de agregar contacto.
     */
    public void agregarContacto(Paquete paqueteContacto) {
        send(paqueteContacto);
    }

    public void desconectarUsuario(Paquete paquete) {
		send(paquete);
	}
    
    private void send(Paquete paquete) {
        try {
            if (out != null) {
            	out.writeObject(paquete);
            	out.flush();
            }
        } catch (IOException e) {
        	System.err.println("Se cayó el canal de entrada");
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
                if(paquete.getOperacion().equals("echo"))
                	echo = true;
                else
                	sistema.recibePaqueteDeServidor(paquete);
            }
        } catch (Exception e) {
        	System.err.println("Se cayó el canal de entrada del servidor");
        }
    }

    private void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignore) {}
    }

    private void startPingLoop() {
        pingThread = new Thread(() -> {
            while (running) {
                try {

                    send(new Paquete("ping",null, null));
                    boolean pongRecibido = esperarPong(3000); 

                    if (!pongRecibido) {
                        System.err.println("No se recibió echo. El servidor está caído.");
                        sistema.reconectarConServidor();
                        stop();
                        break;
                    }
                    Thread.sleep(5000); // Esperar antes del próximo ping
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        }, "PingThread");
        pingThread.start();
    }


    private boolean esperarPong(int timeoutMs) {
        echo = false;  // Resetear antes de esperar
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (echo) return true;
            try { Thread.sleep(300); } catch (InterruptedException e) { break; }
        }
        return false;
    }

    private void dormir(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {}
	}	
}