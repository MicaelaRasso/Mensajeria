package modelo;

import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import conexion.ConexionMonitor;
import conexion.ConexionServidor;
import conexion.MensajeDTO;
import conexion.Paquete;
import conexion.PuertoDTO;
import conexion.UsuarioDTO;
import controlador.Controlador;
import excepciones.ContactoNoExisteException;

/**
 * Orquesta el flujo del cliente: recibe paquetes de conexiones,
 * procesa la lógica de negocio y delega envíos a las conexiones.
 */
public class Sistema {
    private String monitorHost;
    private int monitorPort;
    private String serverHost;
    private int serverPort;

    private Usuario usuario;
    private Controlador controlador;

    // Módulos de conexión, autogestionan hilos y envíos
    private ConexionMonitor conexionMonitor;
    private ConexionServidor conexionServidor;

    // Estado de la aplicación
    private HashMap<String, Contacto> agenda = new HashMap<>();
    private ArrayList<Conversacion> conversaciones = new ArrayList<>();

    public Sistema(Usuario usuario, Controlador controlador) {
        this.usuario = usuario;
        this.controlador = controlador;
        this.monitorHost = ConfigLoader.host;
        this.monitorPort = ConfigLoader.port;

        // Inicializa conexión con monitor (envía petición internamente)
        conexionMonitor = new ConexionMonitor(this, this.monitorHost, this.monitorPort);
        conexionMonitor.start();
        
    }


	/**
     * Callback desde ConexionMonitor al recibir cualquier paquete.
     * Sólo procesa y delega acciones, no envía directamente.
     */
    public void recibePaqueteDelMonitor(Paquete paquete, Socket s) {
    	if(paquete.getOperacion().equals("obtenerSAR")) {
    		if(paquete.getContenido() != null) {
                PuertoDTO dto = (PuertoDTO) paquete.getContenido();
                this.serverPort = dto.getPuerto();
                this.serverHost = dto.getAddress();

                // Configura y arranca la conexión al servidor
                conexionServidor = new ConexionServidor(this, serverHost, serverPort);
                conexionServidor.start();
                PuertoDTO p = new PuertoDTO(s.getLocalPort(),s.getLocalAddress().getHostAddress());
                conexionServidor.registrarUsuario(new Paquete("registrarU", new UsuarioDTO(usuario.getNombre(), p)));
                //conexionMonitor.close();
    			
    		}else {
    			controlador.sinConexion("No hay servidores disponibles en este momento");
    		}
    	}
    }

    /**
     * Callback desde ConexionServidor al recibir paquetes.
     * @throws ContactoNoExisteException 
     */
    public void recibePaqueteDeServidor(Paquete paquete){
        switch (paquete.getOperacion()) {
            case "registrarUR":
            	System.out.println("Verificar registro: " + paquete.toString());
                controlador.verificarRegistro(((UsuarioDTO)paquete.getContenido()).getRespuesta());
                break;
            case "agregarCR":
            	String mensaje;
            	boolean resp;
            	UsuarioDTO uDTO = (UsuarioDTO) paquete.getContenido();
				if (uDTO.getRespuesta() == "no existe") {
					resp = false;
					mensaje = "Se ha agregado el contacto " + uDTO.getNombre();
				} else {
					UsuarioDTO usuarioDTO = (UsuarioDTO) paquete.getContenido();
					Contacto nuevoContacto = new Contacto(usuarioDTO.getNombre());
					agenda.put(nuevoContacto.getNombre(), nuevoContacto);
					resp = true;
					mensaje = "No existe el contacto " + uDTO.getNombre();
				}
				controlador.notificarRespuestaServidor(mensaje, resp);
				break;
            case "recibirM":
                recibirMensaje((MensajeDTO) paquete.getContenido());
                break;
        }
    }

    /**
     * Finaliza ambas conexiones.
     */
    public void detener() {
        if (conexionMonitor != null) conexionMonitor.stop();
        if (conexionServidor != null) conexionServidor.stop();
    }

    /*
     * Avisa al controlador para que no permita acceso al sistema
     * */
	public void sinConexion(String message) {
		controlador.sinConexion(message);
		
	}
    
    private void recibirMensaje(MensajeDTO mensaje) {
        UsuarioDTO emisorDTO = mensaje.getEmisor();
        String texto = mensaje.getMensaje();
        LocalDateTime fechahora = mensaje.getFechaYHora();

        Contacto cont = agenda.computeIfAbsent(emisorDTO.getNombre(), Contacto::new); //lo busca en la agenda, si no esta, lo crea
        Conversacion conv = cont.getConversacion();
        if (conv == null) {
            conv = new Conversacion(cont);
            cont.setConversacion(conv);
            conversaciones.add(conv);
        }
        conv.recibirMensaje(texto, fechahora, cont);
        controlador.notificarMensaje(cont);
    }

    /**
     * Solicita enviar un mensaje: delega a la conexión.
     */
    public void enviarMensaje(Contacto contacto, String texto) {
        if (conexionServidor != null) {
            conexionServidor.enviarMensaje(contacto.getNombre(), texto);
        }
    }

    /**
     * Agrega un contacto remoto: delega a la conexión.
     */
    public void agregarContacto(String nombreContacto) {
        if (conexionServidor != null) {
        	Paquete paquete = new Paquete("agregarC", new UsuarioDTO(nombreContacto));
            conexionServidor.agregarContacto(paquete);
        }
    }

    public void crearConversacion(Contacto contacto) {
        if (!conversaciones.contains(contacto.getConversacion())) {
            Conversacion conv = new Conversacion(contacto);
            contacto.setConversacion(conv);
            conversaciones.add(conv);
        }
    }

    public ArrayList<Mensaje> cargarMensajesDeConversacion(Contacto contacto) {
        Conversacion conv = contacto.getConversacion();
        if (conv != null) {
            return conv.getMensajes();
        }
        return new ArrayList<>();
    }
    	
    public Contacto getContacto(String nombre) {
		return agenda.get(nombre);
	}
    
    public Usuario getUsuario() {
		return usuario;
	}
    
    public HashMap<String, Contacto> getAgenda() {
		return agenda;
	}

	public ArrayList<Conversacion> getConversaciones() {
		return conversaciones;
	}

	
}
