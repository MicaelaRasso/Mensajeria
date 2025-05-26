package modelo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;

import conexion.ConexionMonitor;
import conexion.ConexionServidor;
import conexion.MensajeDTO;
import conexion.Paquete;
import conexion.UsuarioDTO;
import controlador.Controlador;

public class Sistema {
    private Usuario usuario;
    private ConexionMonitor conexionM;
    private ConexionServidor conexionS;
    private HashMap<String, Contacto> agenda = new HashMap<>();
    private ArrayList<Conversacion> conversaciones = new ArrayList<>();
    private Controlador controlador;

    public Sistema(Usuario usuario, Controlador controlador) {
    	this.usuario = usuario;
    	this.controlador = controlador;
        this.conexionM = new ConexionMonitor(this);
        this.conexionM.start();
    }

    public void registrarUsuario() throws IOException {
        Paquete paquete = new Paquete("RegistarU", new UsuarioDTO(this.usuario,""));
        
        conexionS.enviar(paquete);
        //manejar respuesta del servidor al registrar usuario
    }

    public void enviarMensaje(String contenido, Contacto contacto) throws IOException {
    	Paquete paquete = new Paquete("EnviarM", new MensajeDTO(this.usuario, contenido, contacto));


        
    }
    /*
     pre: solo llegan paquetes de con mensajes (operacion recibirM) 
    */
    public synchronized void recibirMensaje(Paquete paquete) {
    	
    	MensajeDTO contenido = (MensajeDTO) paquete.getContenido();
    	String nombre = contenido.getEmisor().getNombre();
    	String mensaje = contenido.getMensaje();
        LocalDateTime fechaYHoraStr = contenido.getFechaYHora();

        System.out.println("Mensaje recibido");

        Contacto cont;
        Conversacion conv;
        if (agenda.containsKey(nombre)) {
            cont = agenda.get(nombre);
            conv = cont.getConversacion();
        } else {
            cont = new Contacto(nombre);
            agenda.put(nombre, cont);
            conv = new Conversacion(cont);
            cont.setConversacion(conv);
            conversaciones.add(conv);
            System.out.println("Nueva conversación creada para: " + nombre);
        }
        conv.recibirMensaje(mensaje, fechaYHoraStr, cont);
        controlador.actualizarVentanaPrincipal();
    }

    public void consultaPorContacto(String nombreContacto) throws IOException {
    	Paquete paquete = new Paquete("AgregarC", new UsuarioDTO(this.usuario,""));
    	
	        proxyClient.send(request);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

    public HashMap<String, Contacto> getAgenda() {
        return agenda;
    }
    
    public Contacto getContacto(String nombre) {
		return agenda.get(nombre);
	}

    public ArrayList<Conversacion> getConversaciones() {
        return conversaciones;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
