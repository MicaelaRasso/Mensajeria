package vista;

import java.awt.BorderLayout;
import java.awt.Color;
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

public class VentanaAgregarContacto extends JFrame {

	private JPanel contentPane;
	private JTextField tfNombre;
	private JButton btnAgregar;
	private JButton btnVolver;
	protected Point mouseClickPoint;

	public VentanaAgregarContacto() {
		
		Color cTexto = new Color(18, 1, 25); //violeta
		Color cFondo = new Color(232, 218, 239); // lila
	    Color cBorde = new Color(74, 35, 90);  // violeta oscuro
	    Color cBoton = new Color(210, 180, 222); // lila oscuro


	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 200);
		setLocationRelativeTo(null);
		setResizable(false);
		setUndecorated(true);
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(cFondo);

		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.setBackground(cFondo);
		
		contentPane.setBorder(BorderFactory.createLineBorder(cBorde, 4));
        contentPane.setLayout(null);
        setContentPane(contentPane);

		
		
		JLabel lblNombre = new JLabel("Nombre de usuario");
		lblNombre.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
		lblNombre.setHorizontalAlignment(SwingConstants.CENTER);
		lblNombre.setBounds(40, 45, 200, 20);
		contentPane.add(lblNombre);
		
		tfNombre = new JTextField();
		tfNombre.setColumns(10);
		tfNombre.setBounds(40, 70, 200, 30);
		contentPane.add(tfNombre);
		
		btnAgregar = new JButton("Agregar");
        btnAgregar.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
		btnAgregar.setBounds(150, 120, 90, 20);
        btnAgregar.setBorder(BorderFactory.createLineBorder(cBorde, 2));
        btnAgregar.setForeground(cTexto);
        btnAgregar.setBackground(cBoton);
		contentPane.add(btnAgregar);

		btnVolver = new JButton("Volver");
        btnVolver.setFont(new Font("Segoe UI Semibold", Font.BOLD, 12));
		btnVolver.setBounds(40, 120, 90, 20);
        btnVolver.setBorder(BorderFactory.createLineBorder(cBorde, 2));
        btnVolver.setForeground(cTexto);
        btnVolver.setBackground(cBoton);
		contentPane.add(btnVolver);
		
	
		JPanel barraMovible = new JPanel();
        barraMovible.setBounds(0, 0, 280, 30);
        barraMovible.setOpaque(true);
        barraMovible.setBackground(cBoton); // Fondo de barra
        barraMovible.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, cBorde)); // Borde inferior
        barraMovible.setLayout(new BorderLayout());
        contentPane.add(barraMovible);

        // Hacer la barra movible
        agregarMovimientoVentana(barraMovible);

        // ✔️ Título centrado
        JLabel lblTitulo = new JLabel("Agregar contacto", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(cTexto);
        barraMovible.add(lblTitulo, BorderLayout.CENTER);

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
 
	public JPanel getContentPane() {
		return contentPane;
	}

	public JTextField getTfNombre() {
		return tfNombre;
	}

	public JButton getBtnAgregar() {
		return btnAgregar;
	}
	
	public JButton getBtnVolver() {
		return btnVolver;
	}
}