package persistencia;

import java.io.IOException;
import java.util.ArrayList;

import modelo.ConfigLoader;
import modelo.Contacto;
import modelo.Conversacion;

public class Persistencia {
	
	DAOFactory factory;
	ConversacionDAO dao;
	
	
	public Persistencia() {
		factory = new TextDAOFactory();
		dao = factory.createConversacionDAO();
		switch(ConfigLoader.persistencia.toUpperCase()){
		case "XML":
			factory = new XMLDAOFactory();
			dao = factory.createConversacionDAO();
		break;
		case "TEXTO":
			factory = new TextDAOFactory();
			dao = factory.createConversacionDAO();
		break;
		case "JSON":
			factory = new JSONDAOFactory();
			dao = factory.createConversacionDAO();
		break;
		}
	}
	
	public void guardarConversacion(ArrayList<Conversacion> conv, String usuario) {
		try {
			dao.save(conv, ConfigLoader.path.concat(usuario));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Conversacion> CargarConversacion(String usuario) {
		ArrayList<Conversacion> conv = null;
		try {			
			conv = dao.load(ConfigLoader.path.concat(usuario));
			return conv;
		}catch(IOException e) {
			e.printStackTrace();
		}
		return conv;
	}

	public void guardarContactos(ArrayList<Contacto> contactos, String usuario) {
		try {
			dao.saveContactos(contactos, ConfigLoader.cpath.concat(usuario));
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Contacto> CargarContactos(String usuario) {
		ArrayList<Contacto> con = null;
		try {			
			con = dao.loadContactos(ConfigLoader.cpath.concat(usuario));
			return con;
		}catch(IOException e) {
			e.printStackTrace();
		}
		return con;
	}
	
}
