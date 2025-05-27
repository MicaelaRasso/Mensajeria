package conexion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import modelo.Sistema;

/**
 * Maneja la conexión al Monitor. Se auto-inicia y envía petición de servidor activo.
 */
public class ConexionMonitor implements Runnable {
    private final Sistema sistema;
    private final String host;
    private final int port;
    private Thread thread;
    private volatile boolean running = true;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ConexionMonitor(Sistema sistema, String host, int port) {
        this.sistema = sistema;
        this.host = host;
        this.port = port;
        this.thread = new Thread(this, "MonitorConnectionThread");
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

    @Override
    public void run(){
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Solicitar servidor activo
            out.writeObject(new Paquete("ObtenerSA", null));

            while (running && !thread.isInterrupted()) {
                Paquete paquete = (Paquete) in.readObject();
                sistema.recibePaqueteDelMonitor(paquete);
            }
        } catch (Exception e) {
        	sistema.sinConexion("No hay conexion al monitor");
        } finally {
            close();
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