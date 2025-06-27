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
import encriptacion.Encriptacion;
import excepciones.ContactoNoExisteException;
import persistencia.Persistencia;

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
   // private boolean primerRegistro = true;

    // Estado de la aplicación
    private HashMap<String, Contacto> agenda = new HashMap<>();
    private ArrayList<Conversacion> conversaciones = new ArrayList<>();
    public Encriptacion encriptacion = new Encriptacion();
    public Persistencia persistencia = new Persistencia();

    public Sistema(Usuario usuario, Controlador controlador) {
    	ArrayList<Conversacion> convs = this.persistencia.CargarConversacion(usuario.getNombre());
    	ArrayList<Contacto> contactos = this.persistencia.CargarContactos(usuario.getNombre());
        this.usuario = usuario;
        this.controlador = controlador;
        this.monitorHost = ConfigLoader.host;
        this.monitorPort = ConfigLoader.port;
        this.encriptacion.establecerEstrategia(ConfigLoader.algo);
        if(contactos != null) {
        	cargarContactos(contactos);
        }
        if(convs != null) {
        	cargarConversaciones(convs);
        }
        
        
        // Inicializa conexión con monitor (envía petición internamente)
        conexionMonitor = new ConexionMonitor(this, this.monitorHost, this.monitorPort);
        conexionMonitor.start();
    }
        
	/**
     * Callback desde ConexionMonitor al recibir cualquier paquete.
     * Sólo procesa y delega acciones, no envía directamente.
     */
    public synchronized void recibePaqueteDelMonitor(Paquete paquete, Socket s) {
        switch (paquete.getOperacion()) {
        case "obtenerSAR":
            if (paquete.getContenido() != null) {
                PuertoDTO dto = (PuertoDTO) paquete.getContenido();
                this.serverPort = dto.getPuerto();
                this.serverHost = dto.getAddress();
                System.out.println(serverHost + ":" + serverPort);
                reiniciarConexionServidor();
                try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Espera para asegurar que la conexión se establezca
                registrarUsuarioEnServidor();  
            } else {
                controlador.sinConexion("No hay servidores disponibles en este momento");
            }
        }
    }
    
    private void reiniciarConexionServidor() {
        if (conexionServidor != null) {
            conexionServidor.stop();
        }
        conexionServidor = new ConexionServidor(this, serverHost, serverPort);
        conexionServidor.start();
        //registrarUsuarioEnServidor(); //PROBAR SI FUNCIONA PARA LA RECONEXION, NO LO CHEQUEE
    }
    
    /*private void actualizarUsuarioEnServidor() {
        Paquete paqueteRegistro = new Paquete("actualizarSocket", new UsuarioDTO(usuario.getNombre()));
        conexionServidor.registrarUsuario(paqueteRegistro);
    }*/

    private void registrarUsuarioEnServidor() {
        Paquete paqueteRegistro = new Paquete("registrarU", new UsuarioDTO(usuario.getNombre()));
        conexionServidor.registrarUsuario(paqueteRegistro);
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
				if (uDTO.getRespuesta().equalsIgnoreCase("no existe")) {
					resp = false;
					mensaje = "No existe el contacto " + uDTO.getNombre();
					
				} else {
					UsuarioDTO usuarioDTO = (UsuarioDTO) paquete.getContenido();
					Contacto nuevoContacto = new Contacto(usuarioDTO.getNombre());
					agenda.put(nuevoContacto.getNombre(), nuevoContacto);
					ArrayList<Contacto> lista = new ArrayList<>(agenda.values());
					persistencia.guardarContactos(lista, usuario.getNombre());
					resp = true;
					mensaje = "Se ha agregado el contacto " + uDTO.getNombre();
				}
				controlador.notificarRespuestaServidor(mensaje, resp);
				break;
            case "recibirM":
                recibirMensaje((MensajeDTO) paquete.getContenido());
                break;
        }
    }
    
    public void reconectarConServidor() {
        this.conexionServidor.stop();
        this.conexionServidor = null;
        this.conexionMonitor = new ConexionMonitor(this, this.monitorHost, this.monitorPort);
        this.conexionMonitor.start();
    }
    
    /**
     * Finaliza ambas conexiones.
     */
    public void detener() {
        if (conexionMonitor != null) conexionMonitor.stop();
        if (conexionServidor != null) conexionServidor.stop();
    }

    public void reintentarRegistro(String nuevoNombre) {
        this.usuario.setNombre(nuevoNombre);
        Paquete paqueteRegistro = new Paquete("registrarU", new UsuarioDTO(nuevoNombre));
        conexionServidor.registrarUsuario(paqueteRegistro);
    }

    /*
     * Avisa al controlador para que no permita acceso al sistema
     * */
	public void sinConexion(String message) {
		controlador.sinConexion(message);
		
	}
    
    private void recibirMensaje(MensajeDTO mensaje) {
        UsuarioDTO emisorDTO = mensaje.getEmisor();
        String texto = encriptacion.desencriptar(mensaje.getMensaje());
        LocalDateTime fechahora = mensaje.getFechaYHora();

        Contacto cont = agenda.get(emisorDTO.getNombre());
        if (cont == null) {
			cont = new Contacto(emisorDTO.getNombre());
			agenda.put(cont.getNombre(), cont);
			ArrayList<Contacto> lista = new ArrayList<>(agenda.values());
			persistencia.guardarContactos(lista, usuario.getNombre());
		}
        Conversacion conv = cont.getConversacion();
        if (conv == null) {
            conv = new Conversacion(cont);
            cont.setConversacion(conv);
            conversaciones.add(conv);
        }
        conv.recibirMensaje(texto, fechahora, cont);
        persistencia.guardarConversacion(conversaciones, usuario.getNombre());
        controlador.notificarMensaje(cont);
        
    }

    /**
     * Solicita enviar un mensaje: delega a la conexión.
     */
    public void enviarMensaje(Contacto contacto, String texto) {
        if (conexionServidor != null) {
            conexionServidor.enviarMensaje(contacto.getNombre(), texto);
            Conversacion conv = contacto.getConversacion();
            conv.agregarMensaje(texto, LocalDateTime.now(), usuario);
            persistencia.guardarConversacion(conversaciones, usuario.getNombre());
        }
        else {
			controlador.sinConexion("No se puede enviar el mensaje, no hay conexión al servidor.");
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


	public void desconectarUsuario() {
		Paquete paquete = new Paquete("desconectarU", new UsuarioDTO(usuario.getNombre()));
		if(conexionServidor != null) {			
			conexionServidor.desconectarUsuario(paquete);
		}
	}
	
	public ConexionMonitor getConexionMonitor() {
		return conexionMonitor;
	}

	private void cargarConversaciones(ArrayList<Conversacion> convs) {
	    for (Conversacion conv : convs) {
	        Contacto contacto = conv.getContacto();
	        agenda.put(contacto.getNombre(), contacto);  // Guardás el contacto en la agenda
	        contacto.setConversacion(new Conversacion(contacto)); // Asegurás que tenga una conversación
	        conversaciones.add(contacto.getConversacion()); // Agregás la conversación a la lista de conversaciones
	        System.out.println("Cargando conversación con: " + contacto.getNombre());
	        // Iterar sobre los mensajes de la conversación
	        for (Mensaje mensaje : conv.getMensajes()) {
	            if (mensaje.getEmisor().equals(usuario.getNombre())) {
	                contacto.getConversacion().agregarMensaje(mensaje.getContenido(), mensaje.getFechaYHora(), usuario);
	            } else {
	                contacto.getConversacion().recibirMensaje(mensaje.getContenido(), mensaje.getFechaYHora(), contacto);
	            }
	            System.out.println("Mensaje de " + mensaje.getEmisor() + " a " + contacto.getNombre() + ": " + mensaje.getContenido());
	        }
	    }
	    System.out.println("Conversaciones cargadas desde persistencia: " + conversaciones.size());
	    controlador.actualizarVentanaPrincipal();
	}
	
	private void cargarContactos(ArrayList<Contacto> contactos) {
		for (Contacto c : contactos) {
    	    agenda.put(c.getNombre(), c);
    	}
		controlador.actualizarVentanaPrincipal();
	}
	
}
