package conexion;

import java.io.*;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import controlador.Controlador;
import modelo.ConfigLoader;
import modelo.Sistema;

/**
 * Encapsula la lógica de conexión con el proxy/monitor en puerto 60000,
 * envío de requests y recepción de respuestas con ACK + payload JSON,
 * y reintentos (fail-over).
 */
public class ConexionMonitor extends Thread {
    private static final String MONITOR_HOST = ConfigLoader.host;
    private static final int MONITOR_PORT = ConfigLoader.port;
    private static final int MAX_RETRIES = 3;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Sistema sistema;

    public ConexionMonitor(Sistema s) {
        this.sistema = s;
    }

    public void connect() throws IOException {
        socket = new Socket(MONITOR_HOST, MONITOR_PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Conectado al Monitor en " + MONITOR_HOST + ":" + MONITOR_PORT);
    }

    public void run() {
        try {
            connect();

            Paquete paquete;
            while ((paquete = in.readLine()) != null) {
                System.out.println("[RECV HEADER] " + header);
                String payload = in.readLine(); // JSON o string plano
                System.out.println("[RECV PAYLOAD] " + payload);

                String op = parseField(header, "OPERACION");
                System.out.println(op);

                if ("GET_MESSAGE".equalsIgnoreCase(op)) {
                    // Mensaje espontáneo: notificar al sistema
                    Paquete req = JsonConverter.fromJson(payload);
                    sistema.recibirMensaje(req);
                } else if ("CLIENT_REQ".equalsIgnoreCase(op)) {
                    // Respuesta a un send(): colocar en la cola
                    Paquete resp;
                    if (payload.equals("registrado") || payload.equals("en uso") || payload.equals("enviado")) {
                        resp = new Paquete();
                        resp.setContenido(payload);
                    } else {
                        resp = JsonConverter.fromJson(payload);
                    }

                    // Intentar poner en la cola sin bloquear indefinidamente
                    if (!responseQueue.offer(resp, 5, TimeUnit.SECONDS)) {
                        System.err.println("No se pudo entregar la respuesta al hilo que hizo send()");
                    }
                } else {
                    System.err.println("Operación desconocida: " + op);
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public int obtenerPuerto() {
		//pedir puerto del servidor al monitor
    	return 0; // Implementar lógica para obtener el puerto del servidor
	}
    
	public void send(Paquete req) throws InterruptedException, IOException {
	    	IOException lastEx = null;
	    	for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
	            try {
	                if (socket == null || socket.isClosed()) {
	                    connect();
	                }
	                
	                //agregar logica de envío
	                return;
	            } catch (IOException e) {
	                lastEx = e;
	                System.out.println("entra al catch");
	                close();
	                Thread.sleep(1000); // Espera antes de reintentar
	                System.out.println("Reintentando conexión (" + attempt + "/" + MAX_RETRIES + ")");
	            }
	        }
	    	throw lastEx;
	    }

    
    public void close() {
        try {
            if (socket != null) socket.close();
            System.out.println("Conexión cerrada con el Proxy.");
        } catch (IOException ignored) {}
    }

	public BufferedReader getIn() {
		return in;
	}

	public PrintWriter getOut() {
		return out;
	}
}
