package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import modelo.Contacto;
import modelo.Conversacion;
import modelo.Mensaje;
import modelo.Sistema;
import modelo.Usuario;
import vista.VentanaAgregarContacto;
import vista.VentanaInicio;
import vista.VentanaPrincipal;

public class Controlador implements ActionListener{
	private VentanaInicio vInicio;
	private VentanaPrincipal vPrincipal;
	private VentanaAgregarContacto vContacto;
	private Sistema sistema;
	private Contacto contactoActual = null;
	
	
	public Controlador(VentanaInicio vInicio, VentanaPrincipal vPrincipal, VentanaAgregarContacto vContacto) {
		super();
		this.vInicio = vInicio;
		this.vPrincipal = vPrincipal;
		this.vPrincipal.getBtnEnviar().setEnabled(false);  // estado inicial

		this.vPrincipal.getTxtrEscribirMensaje().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
		    private void actualizarEstadoBoton() {
		        String texto = vPrincipal.getTxtrEscribirMensaje().getText().trim();
		        vPrincipal.getBtnEnviar().setEnabled(!texto.isEmpty());
		    }

		    @Override
		    public void insertUpdate(javax.swing.event.DocumentEvent e) {
		        actualizarEstadoBoton();
		    }

		    @Override
		    public void removeUpdate(javax.swing.event.DocumentEvent e) {
		        actualizarEstadoBoton();
		    }

		    @Override
		    public void changedUpdate(javax.swing.event.DocumentEvent e) {
		        actualizarEstadoBoton();
		    }
		});

		this.vContacto = vContacto;
		this.vInicio.getBtnIngresar().addActionListener(this);
		this.vPrincipal.getBtnConversacion().addActionListener(this);
		this.vPrincipal.getBtnEnviar().addActionListener(this);
		this.vPrincipal.getBtnContacto().addActionListener(this);
		this.vContacto.getBtnAgregar().addActionListener(this);
		this.vContacto.getBtnVolver().addActionListener(this);
	}

	public static void main(String[] args) {
		
		VentanaInicio inicio = new VentanaInicio();
		VentanaPrincipal principal = new VentanaPrincipal(null);
		VentanaAgregarContacto contacto = new VentanaAgregarContacto();

		Controlador contr = new Controlador(inicio, principal, contacto);

		inicio.setVisible(true);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		//VENTANA INICIO
		if (vInicio.getBtnIngresar().equals(e.getSource())) {
			registroInicial();
		}else {

			//VENTANA AGREGAR CONTACTO - Crea contacto e inicia la conversación (en sistema)
			if(vContacto.getBtnAgregar().equals(e.getSource())) {
				agregarContacto();
				cargarListaDeContactos();
				vContacto.getTfNombre().setText("");
			}else {
				//VENTANA PRINCIPAL
				if(vPrincipal.getBtnConversacion().equals(e.getSource())) {
					DefaultListModel<String> modelo = new DefaultListModel<>();
					System.out.println("boton conversaciones");   
			        if (sistema!=null && !sistema.getAgenda().isEmpty()) {
				        Iterator<Contacto> it = sistema.getAgenda().values().iterator();
				        while(it.hasNext()) {
							Contacto c =  (Contacto) it.next();
							modelo.addElement(c.getNombre());
						}
				        
		                //System.out.println("cargando conversaciones");
		                crearConversacion(contactoActual);
		                cargarListaDeConversaciones();
			        }
					

			        vPrincipal.revalidate();
			        vPrincipal.repaint();
					
				}else {
					
					//VENTANA PRINCIPAL - Presiona para enviar un mensaje
					if (vPrincipal.getBtnEnviar().equals(e.getSource())) {
						if (contactoActual == null) {
							mensajeError("ERROR 003","Debés seleccionar una conversación antes de enviar un mensaje.");
							return;
						}

						String m = vPrincipal.getTxtrEscribirMensaje().getText().trim();
						vPrincipal.getTxtrEscribirMensaje().setText("");
						System.out.println(contactoActual);
						enviarMensaje(m, contactoActual);

						SwingUtilities.invokeLater(() -> {
							cargarListaDeMensajes();
						});	
					}else {
						//VENTANA PRINCIPAL - Abre la ventana de agregar contacto
						if (vPrincipal.getBtnContacto().equals(e.getSource())) {
							vPrincipal.setVisible(false);
							vContacto.setVisible(true);	
						}else {
							//VENTANA AGREGAR CONTACTO - Volver a la ventana principal
							if(vContacto.getBtnVolver().equals(e.getSource())) {
								vPrincipal.setVisible(true);
								vContacto.setVisible(false);	
							}
						}
					}
				}
			}
		}		
	}

	private void registroInicial() {
		String nombre = vInicio.getTfNombre().getText();
		if (!(nombre.equals(""))) {
			Usuario usuario = new Usuario(nombre,"127.0.0.1"); 
			this.sistema = new Sistema(usuario,this);
		}else{
			mensajeError("ERROR 001","Debe completar todos los campos");
		}
	}
	
	public void verificarRegistro(String respuesta) {
		if(respuesta.equals("registrado")) {
			vInicio.setVisible(false);
			vPrincipal.setVisible(true);
			//cargarListaDeContactos();
			//cargarListaDeConversaciones();
		}else {
			mensajeError("ERROR 011","El nombre de usuario ya se encuentra en uso, por favor ingrese otro.");
		}
	}
	
	private void agregarContacto() {
		String nombre = vContacto.getTfNombre().getText();
		if (!(nombre.equals("") || nombre.equals(sistema.getUsuario().getNombre()))) {
			if(sistema.getAgenda().containsKey(nombre)){
				mensajeError("ERROR 006","Se ha intentado agregar a un contacto ya agendado");
			}else {
				sistema.agregarContacto(nombre);
			}
		}else {
			if(nombre.equals("")) {
				mensajeError("ERROR 001", "Debe ingresar un nombre de contacto");
			}else {
				mensajeError("ERROR 001", "No puede agregarse usted como contacto");
			}	
		}
	}
	public void crearConversacion(Contacto contacto) {
		sistema.crearConversacion(contacto);
	}

	public void enviarMensaje(String m, Contacto contactoActual){
		sistema.enviarMensaje(contactoActual, m);
	}

	public void cargarListaDeContactos() {
        DefaultListModel<String> modelo = new DefaultListModel<>();
        if (sistema!=null && !sistema.getAgenda().isEmpty()) {
	        Iterator<Contacto> it = sistema.getAgenda().values().iterator();
	        while(it.hasNext()) {
				Contacto c =  (Contacto) it.next();
				modelo.addElement(c.getNombre());
			}
	        JList<String> listaContactos = new JList<>(modelo);
	        listaContactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        vPrincipal.getSpContactos().setViewportView(listaContactos);
	        
	        listaContactos.addListSelectionListener(f -> {
	            if (!f.getValueIsAdjusting()) {
	                String seleccionado = listaContactos.getSelectedValue();
	                contactoActual = sistema.getContacto(seleccionado);
	            }
	        });
        }	
	}
	
	public void notificarMensaje(Contacto cont) {
		cargarListaDeConversaciones();  // Recargamos la lista de conversaciones con el asterisco
		if (!cont.equals(contactoActual)) {
			JList<String> listaConversaciones = (JList<String>) vPrincipal.getSpConversacion().getViewport().getView();
			DefaultListModel<String> modelo = (DefaultListModel<String>) listaConversaciones.getModel();
			for (int i = 0; i < modelo.size(); i++) {
				String nombre = modelo.getElementAt(i);
				if (nombre.equals(cont.getNombre()) || nombre.equals(cont.getNombre() + " *")) {
					if (!nombre.endsWith("*")) {
						modelo.set(i, cont.getNombre() + " *");  // Agrega notificación
					}
					break;
				}
			}
		}
	}
	
	public void nuevoMensaje() {
	    SwingUtilities.invokeLater(() -> {
	        cargarListaDeMensajes();
	    });
	}
	
	private void cargarListaDeMensajes() {
	    if (contactoActual == null) {
//	        System.out.println("[DEBUG] cargarMensajes(): contactoActual == null, no hago nada");
	        return;
	    }

//	    System.out.println("[DEBUG] cargarMensajes(): inicio para contacto " + contactoActual.getNombre());

	    vPrincipal.getLblNombre().setText(contactoActual.getNombre());

	    // Panel que contendrá los mensajes
	    JPanel messagePanel = new JPanel();
	    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

	    // Obtengo la lista original y la invierto
	    ArrayList<Mensaje> invertida = new ArrayList<>(sistema.cargarMensajesDeConversacion(contactoActual));
	    //	    System.out.println("[DEBUG] cargarMensajes(): mensajes totales sin invertir = " + invertida.size());

	    Collections.reverse(invertida);
//	    System.out.println("[DEBUG] cargarMensajes(): mensajes totales invertidos = " + invertida.size());

	    for (Mensaje m : invertida) {
//	        System.out.println("[DEBUG] cargarMensajes(): agregando mensaje al panel = " + m.getContenido());
	        JPanel panelMensaje = vPrincipal.crearPanelMensaje(m, sistema.getUsuario());
	        messagePanel.add(panelMensaje);
	    }

//	    System.out.println("[DEBUG] cargarMensajes(): componentes en messagePanel = " + messagePanel.getComponentCount());

	    // Finalmente lo muestro
	    vPrincipal.getSpMensajes().setViewportView(messagePanel);
	//    System.out.println("[DEBUG] cargarMensajes(): viewport seteado correctamente");
	}


	public void cargarListaDeConversaciones() {
        DefaultListModel<String> modelo = new DefaultListModel<>();
       
        if (sistema!=null && !sistema.getConversaciones().isEmpty()) {
        	System.out.println("cargando conversaciones");
	        Iterator<Conversacion> it = sistema.getConversaciones().iterator();
	        
	        while (it.hasNext()) {
	        	Conversacion c = it.next();
	        	modelo.addElement(c.getContacto().getNombre());
	        }
	        
	        JList<String> listaConversaciones = new JList<>(modelo);
	        listaConversaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	
	        listaConversaciones.addListSelectionListener(e -> {
	        	if (!e.getValueIsAdjusting()) {
	        		String seleccionado = listaConversaciones.getSelectedValue();

	        		if (seleccionado.contains(" *")) {
	        			seleccionado = seleccionado.replace(" *", "");  // Quitamos asterisco
	        		}
	        		if (sistema.getAgenda().containsKey(seleccionado)) {
	        			contactoActual = sistema.getContacto(seleccionado);
	        			cargarListaDeMensajes();

	        			// Habilita o deshabilita el botón enviar según el contenido del campo de texto
	        			String texto = vPrincipal.getTxtrEscribirMensaje().getText().trim();
	        			vPrincipal.getBtnEnviar().setEnabled(!texto.isEmpty());

	        			// Limpia el asterisco en la lista de conversaciones
	        			DefaultListModel<String> modeloLista = (DefaultListModel<String>) listaConversaciones.getModel();
	        			for (int i = 0; i < modeloLista.size(); i++) {
	        				String nombre = modeloLista.get(i);
	        				if (nombre.equals(contactoActual.getNombre() + " *")) {
	        					modeloLista.set(i, contactoActual.getNombre());
	        					break;
	        				}
	        			}
	        		}else {
	        			mensajeError("ERROR 008","Error al seleccionar la conversación");
	        		}
	        	}
	        });
	
	        vPrincipal.getSpConversacion().setViewportView(listaConversaciones);
        }	
	
	}

	public void actualizarVentanaPrincipal() {
        nuevoMensaje();
        cargarListaDeContactos();
        cargarListaDeConversaciones();
	}
/*	
	public void contactoAgregado(Contacto c) {
	    SwingUtilities.invokeLater(() -> {
	    	actualizarVentanaPrincipal();
//	        cargarListaDeContactos();
//	        cargarListaDeConversaciones();  // también para que aparezca la nueva conversación
	        mensajeAviso("Contacto agregado", "Se ha agregado el contacto " + c.getNombre());
	    });
	}
	public void contactoInexistente() {
		
	}
	*/
	public void contactoSinConexion(String s, String e) {
		mensajeError(e,s);
	}

	public Sistema getSistema() {
		return sistema;
	}

	public Contacto getContactoActual() {
		return contactoActual;
	}
	
	public void notificarRespuestaServidor(String mensaje, boolean respuesta) {
		 SwingUtilities.invokeLater(() -> {
             if(respuesta == true) {
 				System.out.println(sistema.getAgenda());
 				vContacto.setVisible(false);
 		        vPrincipal.revalidate();
 		        vPrincipal.repaint();
 				vPrincipal.setVisible(true);
 				cargarListaDeContactos();
 				mensajeAviso("Contacto agregado",mensaje);
 				
             }else {
  				mensajeAviso("Contacto inválido",mensaje);
             }
         });
	}
	
	void mensajeError(String nomb, String descr) {
		JOptionPane.showMessageDialog(
		    null,
		    descr,
		    nomb,
		    JOptionPane.WARNING_MESSAGE
		);
	}
	
	void mensajeAviso(String nomb, String descr) {
   	 JOptionPane.showMessageDialog(
			    null,
			    descr,
			    nomb,
			    JOptionPane.WARNING_MESSAGE
			);

	}

	public void sinConexion(String e) {
		mensajeError("ERROR 010", e);
	}



}
