package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import controlador.Controlador;

public class VentanaPrincipal extends JFrame {

	private JPanel contentPane;
	private JButton btnEnviar;
	private JButton btnConversacion;
	private JTextArea txtrEscribirMensaje;
	private JScrollPane spContactos;
	private JScrollPane spConversacion;	
	private JLabel lblNombre;
	private JButton btnContacto;
	private JScrollPane spMensajes;
	protected Point mouseClickPoint;
	
	public VentanaPrincipal(Controlador controlador) {
		Color cTexto = new Color(18, 1, 25); //violeta
		Color cFondo = new Color(232, 218, 239); // lila
	    Color cBorde = new Color(74, 35, 90);  // violeta oscuro
	    Color cBoton = new Color(210, 180, 222); // lila oscuro

	    //SECTOR VENTANA

	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setUndecorated(true);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setBackground(cFondo);
		
		contentPane.setBorder(BorderFactory.createLineBorder(cBorde, 4));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        
        //SECTOR CONTACTOS
		
		JLabel lblContactos = new JLabel("Contactos");
		lblContactos.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
		lblContactos.setHorizontalAlignment(SwingConstants.LEFT);
		lblContactos.setVerticalAlignment(SwingConstants.CENTER);
		lblContactos.setBounds(20, 45, 90, 30);
		contentPane.add(lblContactos);

		btnContacto = new JButton("+");
		btnContacto.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
		btnContacto.setBounds(140, 45, 30, 30);
		btnContacto.setBorder(BorderFactory.createLineBorder(cBorde, 2));
		btnContacto.setForeground(cTexto);
		btnContacto.setBackground(cBoton);
		contentPane.add(btnContacto);
		
		spContactos = new JScrollPane();
        spContactos.setBounds(20, 85, 150, 90);
		spContactos.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(spContactos);

		btnConversacion = new JButton("Iniciar conversación");
		btnConversacion.setBounds(20, 185, 150, 25);
		btnConversacion.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
		btnConversacion.setBorder(BorderFactory.createLineBorder(cBorde, 2));
		btnConversacion.setForeground(cTexto);
		btnConversacion.setBackground(cBoton);
		contentPane.add(btnConversacion);
	
		
		//SECTOR MENSAJES
		
		lblNombre = new JLabel("Nombre de usuario");
		lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
		lblNombre.setFont(new Font("Segoe UI Semibold", Font.BOLD | Font.ITALIC, 12));
		lblNombre.setBounds(190, 40, 350, 20);
		contentPane.add(lblNombre);
		
		spMensajes = new JScrollPane();
		spMensajes.setBounds(190, 110, 390, 370);
		contentPane.add(spMensajes);

		txtrEscribirMensaje = new JTextArea();
		txtrEscribirMensaje.setLineWrap(true);
		txtrEscribirMensaje.setToolTipText("Escribir mensaje...");
		txtrEscribirMensaje.setTabSize(3);
		txtrEscribirMensaje.setForeground(new Color(0, 0, 0));
		txtrEscribirMensaje.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 12));
		txtrEscribirMensaje.setBounds(190, 65, 340, 30);
		contentPane.add(txtrEscribirMensaje);
		
		btnEnviar = new JButton(">");
		btnEnviar.setBounds(550, 65, 30, 30);
		btnEnviar.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
		btnEnviar.setBorder(BorderFactory.createLineBorder(cBorde, 2));
		btnEnviar.setForeground(cTexto);
		btnEnviar.setBackground(cBoton);
		contentPane.add(btnEnviar);

		//SECTOR CONVERSACIONES
		
		JLabel lblConversaciones = new JLabel("Conversaciones");
		lblConversaciones.setHorizontalAlignment(SwingConstants.LEFT);
		lblConversaciones.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
		lblConversaciones.setBounds(20, 235, 150, 20);
		contentPane.add(lblConversaciones);

		spConversacion = new JScrollPane();
		spConversacion.setBounds(20, 270, 150, 210);
		spConversacion.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(spConversacion);

		
		//SECTOR BARRA 
		
        JPanel barraMovible = new JPanel();
        barraMovible.setBounds(0, 0, 600, 30);
        barraMovible.setOpaque(true);
        barraMovible.setBackground(cBoton); // Fondo de barra
        barraMovible.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, cBorde)); // Borde inferior
        barraMovible.setLayout(new BorderLayout());
        contentPane.add(barraMovible);

        // Hacer la barra movible
        agregarMovimientoVentana(barraMovible);

        // ✔️ Título centrado
        JLabel lblTitulo = new JLabel("Aplicación de mensajería instantánea", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(cTexto);
        barraMovible.add(lblTitulo, BorderLayout.CENTER);

        // ✔️ Botón Cerrar a la derecha
        JButton btnCerrar = new JButton("X");
        btnCerrar.setPreferredSize(new Dimension(25, 25)); // tamaño acorde a la barra
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrar.setBorder(BorderFactory.createLineBorder(cBorde, 2));
        btnCerrar.setForeground(cTexto);
        btnCerrar.setBackground(cBoton);
        btnCerrar.addActionListener(e -> {
            int opcion = javax.swing.JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que querés cerrar la aplicación?",
                "Confirmar salida",
                javax.swing.JOptionPane.YES_NO_OPTION
            );

            if (opcion == javax.swing.JOptionPane.YES_OPTION) {
            	//si se necesita, se podría ocultar la ventana y poner un tiempo de espera para hacer la persistencia
            	System.exit(0);
            }
        });

        barraMovible.add(btnCerrar, BorderLayout.EAST);
        
	}

	private void agregarMovimientoVentana(JComponent componente) {
        componente.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseClickPoint = e.getPoint();
            }
        });

        componente.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
                Point current = e.getLocationOnScreen();
				setLocation(current.x - mouseClickPoint.x, current.y - mouseClickPoint.y);
            }
        });
    }

	private JPanel crearPanelMensaje(Mensaje m, Usuario usuario) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    String fechaFormateada = m.getFechaYHora().format(formatter);

	    boolean esMio = m.getEmisor().equals(usuario.getUsuario().getNombre());

	    // Colores personalizados
	    Color cFondo = new Color(232, 218, 239); // lila claro
	    Color cBoton = new Color(210, 180, 222); // lila oscuro
	    Color colorFondoMensaje = esMio ? cFondo : cBoton;

	    JPanel panelMensaje = new JPanel();
	    panelMensaje.setLayout(new BoxLayout(panelMensaje, BoxLayout.Y_AXIS));
	    panelMensaje.setBorder(BorderFactory.createCompoundBorder(
	        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
	        BorderFactory.createEmptyBorder(5, 10, 5, 10)
	    ));
	    panelMensaje.setBackground(colorFondoMensaje);
	    panelMensaje.setAlignmentX(esMio ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

	    JLabel lblUsuario = new JLabel(m.getEmisor() + ":");
	    lblUsuario.setFont(new Font("Segoe UI Semibold", Font.ITALIC, 12));
	    lblUsuario.setForeground(new Color(50, 50, 50));

	    JTextArea txtrContenido = new JTextArea(m.getContenido());
	    txtrContenido.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 12));
	    txtrContenido.setLineWrap(true);
	    txtrContenido.setWrapStyleWord(true);
	    txtrContenido.setEditable(false);
	    txtrContenido.setBackground(colorFondoMensaje);
	    txtrContenido.setBorder(null);
	    txtrContenido.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

	    JLabel lblFecha = new JLabel(fechaFormateada);
	    lblFecha.setFont(new Font("Segoe UI", Font.ITALIC, 10));
	    lblFecha.setForeground(new Color(100, 100, 100));

	    panelMensaje.add(lblUsuario);
	    panelMensaje.add(txtrContenido);
	    panelMensaje.add(lblFecha);

	    JPanel wrapper = new JPanel(new FlowLayout(esMio ? FlowLayout.RIGHT : FlowLayout.LEFT));
	    wrapper.setOpaque(false);
	    wrapper.add(panelMensaje);

	    return wrapper;
	}

	public JPanel getContentPane() {
		return contentPane;
	}

	public JButton getBtnEnviar() {
		return btnEnviar;
	}

	public JButton getBtnConversacion() {
		return btnConversacion;
	}

	public JTextArea getTxtrEscribirMensaje() {
		return txtrEscribirMensaje;
	}

	public JScrollPane getSpContactos() {
		return spContactos;
	}

	public JScrollPane getSpConversacion() {
		return spConversacion;
	}

	public JLabel getLblNombre() {
		return lblNombre;
	}
	
	public JButton getBtnContacto() {
		return btnContacto;
	}

	public JScrollPane getSpMensajes() {
		return spMensajes;
	}
	
}