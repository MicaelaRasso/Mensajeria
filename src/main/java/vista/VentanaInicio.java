package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class VentanaInicio extends JFrame{

	private JPanel contentPane;
	private JTextField tfNombre;
	private JTextField tfPuerto;
	private JButton btnIngresar;
	
	private Point mouseClickPoint;

	public VentanaInicio() {
		
		Color cTexto = new Color(18, 1, 25); //violeta
		Color cFondo = new Color(232, 218, 239); // lila
	    Color cBorde = new Color(74, 35, 90);  // violeta oscuro
	    Color cBoton = new Color(210, 180, 222); // lila oscuro


		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 200);
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
		
        
        
		tfNombre = new JTextField();
		tfNombre.setBounds(50, 90, 200, 30);
		contentPane.add(tfNombre);
		tfNombre.setColumns(10);
		
		JLabel lblNombre = new JLabel("Nombre de usuario");
		lblNombre.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
		lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
		lblNombre.setVerticalAlignment(SwingConstants.CENTER);
		lblNombre.setBounds(50, 65, 200, 20);
		contentPane.add(lblNombre);
		
		/*JLabel lblPuerto = new JLabel("Puerto");
		lblPuerto.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
		lblPuerto.setHorizontalAlignment(SwingConstants.CENTER);
		lblPuerto.setBounds(50, 135, 200, 20);
		contentPane.add(lblPuerto);
		
		tfPuerto = new JTextField();
		tfPuerto.setColumns(10);
		tfPuerto.setBounds(50, 160, 200, 30);
		contentPane.add(tfPuerto);*/
		
		btnIngresar = new JButton("Ingresar");
		btnIngresar.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
		btnIngresar.setBounds(160, 150, 90, 23);
		btnIngresar.setBorder(BorderFactory.createLineBorder(cBorde, 2));
		btnIngresar.setForeground(cTexto);
		btnIngresar.setBackground(cBoton);
		contentPane.add(btnIngresar);
        
        
        JPanel barraMovible = new JPanel();
        barraMovible.setBounds(0, 0, 300, 30);
        barraMovible.setOpaque(true);
        barraMovible.setBackground(cBoton); // Fondo de barra
        barraMovible.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, cBorde)); // Borde inferior
        barraMovible.setLayout(new BorderLayout());
        contentPane.add(barraMovible);

        // Hacer la barra movible
        agregarMovimientoVentana(barraMovible);

        // ✔️ Título centrado
        JLabel lblTitulo = new JLabel("Ingreso de usuario", SwingConstants.CENTER);
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
        btnCerrar.addActionListener(e -> System.exit(0));
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
    
	public JTextField getTfNombre() {
		return tfNombre;
	}

	public JTextField getTfPuerto() {
		return tfPuerto;
	}

	public JButton getBtnIngresar() {
		return btnIngresar;
	}

	public JPanel getContentPane() {
		return contentPane;
	}

}