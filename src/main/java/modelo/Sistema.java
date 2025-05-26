package modelo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observer;

import conexion.ConexionMonitor;
import conexion.ConexionServidor;
import conexion.Contenido;
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
        Paquete paquete = new Paquete("REGISTRARU", new UsuarioDTO(this.usuario,""));
        
        conexionS.enviar(paquete);
        //manejar respuesta del servidor al registrar usuario
    }

    public void enviarMensaje(String contenido, Contacto contacto) throws IOException {
        Paquete request = crearRequest();
        request.setOperacion("mensaje");
        request.setNombreReceptor(contacto.getNombre());
        request.setContenido(contenido);

        
    }

    public synchronized void recibirMensaje(Paquete request) {
        String nombre = request.getEmisor().getNombre();
        String contenido = request.getContenido();
        LocalDateTime fechaYHoraStr = request.getFechaYHora();
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
        conv.recibirMensaje(contenido, fechaYHoraStr, cont);
        controlador.nuevoMensaje();
        controlador.cargarContactos();
        controlador.cargarConversaciones();
    }

    public void consultaPorContacto(String nombreContacto) throws IOException {
        Paquete request = crearRequest();
        request.setOperacion("consulta");
        request.setEmisor(this.usuario);
        request.setContenido(nombreContacto);
		try {
			Observer<Paquete> respuestaMensaje = new Observer<>() {
	            @Override
	            public void update(Paquete response) {
	            	if (!response.getContenido().equals("")) {
	    	            if (!agenda.containsKey(response.getContenido())) {
	    	                Contacto c = new Contacto(response.getContenido());
	    	                agenda.put(c.getNombre(), c);
	    	                controlador.NotificarRespuestaServidor("El contacto ha sido agregado exitosamente", true);
	    	            }
	    	        }else {
	    	        	System.out.println("el contacto no existe");
	    	        	controlador.NotificarRespuestaServidor("El contacto no existe", false);
	    	        }
	            	responseObservable.removeObserver(this); // auto-remover
	            }
	        };
	        responseObservable.addObserver(respuestaMensaje);
	        proxyClient.send(request);
	        /*if (!respuesta.getContenido().equals("")) {
	            if (!agenda.containsKey(respuesta.getContenido())) {
	                Contacto c = new Contacto(respuesta.getContenido());
	                agenda.put(c.getNombre(), c);
	                this.controlador.NotificarRespuestaServidor("El contacto ha sido agregado exitosamente", true);
	            }
	        }else {
	        	this.controlador.NotificarRespuestaServidor("El contacto no existe", false);
	        }*/
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

    public Paquete crearRequest() {
        Paquete request = new Paquete();
        request.setEmisor(this.usuario);
        //request.setReceptor(new Usuario());
        request.setFechaYHora(LocalDateTime.now());
        return request;
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
