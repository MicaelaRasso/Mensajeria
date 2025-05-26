package conexion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import modelo.Sistema;

public class ConexionServidor extends Thread {
	// Variables de instancia
	private String servidorHost;
    private int servidorPort;
    private int MAX_RETRIES = 3;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Sistema sistema;

	public ConexionServidor() {
		// TODO Auto-generated constructor stub
	}
	
	public void connect() throws IOException {
        socket = new Socket(servidorHost, servidorPort);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("Conectado al Servidor en " + servidorHost + ":" + servidorPort);
    }
	
	public void run() {
		try {
			connect();
			
			
			
		}
		
	}
	
	public void enviar(Paquete paquete) {
		// TODO Auto-generated method stub
		
	}

}
