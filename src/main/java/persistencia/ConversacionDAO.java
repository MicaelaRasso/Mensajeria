package persistencia;

import java.io.IOException;
import java.util.ArrayList;

import modelo.Contacto;
import modelo.Conversacion;

public interface ConversacionDAO {
    void save(ArrayList<Conversacion> conversaciones, String path) throws IOException;
    ArrayList<Conversacion> load(String path) throws IOException;
    void saveContactos(ArrayList<Contacto> contactos, String path) throws IOException;
    ArrayList<Contacto> loadContactos(String path) throws IOException;
}
