package persistencia;

import java.io.IOException;
import java.util.ArrayList;

import modelo.ConfigLoader;
import modelo.Conversacion;

public class Persistencia {
	
	DAOFactory factory;
	ConversacionDAO dao;
	private String path = ConfigLoader.path;
	
	public Persistencia() {
		factory = new TextDAOFactory();
		dao = factory.createConversacionDAO();
	}
	
	public void guardarConversacion(ArrayList<Conversacion> conv,String format) {
		try {			
			switch(format.toUpperCase()){
			case "XML":
				factory = new XMLDAOFactory();
				dao = factory.createConversacionDAO();
				dao.save(conv, path.concat(".xml"));
			break;
			case "TEXTO":
				factory = new TextDAOFactory();
				dao = factory.createConversacionDAO();
				dao.save(conv, path.concat(".txt"));
			break;
			case "JSON":
				factory = new JSONDAOFactory();
				dao = factory.createConversacionDAO();
				dao.save(conv, path.concat(".json"));
			break;
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Conversacion> CargarConversacion(String format) {
		ArrayList<Conversacion> conv = null;
		try {			
			switch(format.toUpperCase()){
			case "XML":
				factory = new XMLDAOFactory();
				dao = factory.createConversacionDAO();
				dao.load(path.concat(".xml"));
			break;
			case "TEXTO":
				factory = new TextDAOFactory();
				dao = factory.createConversacionDAO();
				dao.load(path.concat(".txt"));
			break;
			case "JSON":
				factory = new JSONDAOFactory();
				dao = factory.createConversacionDAO();
				conv = dao.load(path.concat(".json"));
			break;
			}
			return conv;
		}catch(IOException e) {
			e.printStackTrace();
		}
		return conv;
	}
	
}
