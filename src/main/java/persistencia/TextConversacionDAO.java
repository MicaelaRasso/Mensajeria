package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import modelo.Contacto;
import modelo.Conversacion;
import modelo.Mensaje;

public class TextConversacionDAO implements ConversacionDAO {
    @Override
    public void save(ArrayList<Conversacion> conversaciones, String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.concat(".txt")))) {
            writer.write(Integer.toString(conversaciones.size()));
            writer.newLine();
            for (Conversacion conv : conversaciones) {
                writer.write(conv.getContacto().getNombre()); writer.newLine();
                writer.write(Boolean.toString(conv.tieneNotificacion())); writer.newLine();
                writer.write(Integer.toString(conv.getMensajes().size())); writer.newLine();
                for (Mensaje m : conv.getMensajes()) {
                    writer.write(m.getEmisor() + "|" + m.getContenido() + "|" + m.getFechaYHora().toString());
                    writer.newLine();
                }
            }
        }
    }

    @Override
    public ArrayList<Conversacion> load(String path) throws IOException {
        ArrayList<Conversacion> conversaciones = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.concat(".txt")))) {
            int convCount = Integer.parseInt(reader.readLine());
            for (int i = 0; i < convCount; i++) {
                String nombre = reader.readLine();
                boolean noti = Boolean.parseBoolean(reader.readLine());
                int msgCount = Integer.parseInt(reader.readLine());
                Contacto c = new Contacto(nombre);
                Conversacion conv = new Conversacion(c);
                conv.setNotificacion(noti);
                for (int j = 0; j < msgCount; j++) {
                    String[] parts = reader.readLine().split("\\|");
                    conv.recibirMensaje(parts[1], LocalDateTime.parse(parts[2]), new Contacto(parts[0]));
                }
                conversaciones.add(conv);
            }
        }
        return conversaciones;
    }
    

    @Override
    public void saveContactos(ArrayList<Contacto> contactos, String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.concat(".txt")))) {
            writer.write(Integer.toString(contactos.size()));
            writer.newLine();
            for (Contacto c : contactos) {
                writer.write(c.getNombre());
                writer.newLine();
            }
        }
    }

    @Override
    public ArrayList<Contacto> loadContactos(String path) throws IOException {
        ArrayList<Contacto> contactos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path.concat(".txt")))) {
            int count = Integer.parseInt(reader.readLine());
            for (int i = 0; i < count; i++) {
                contactos.add(new Contacto(reader.readLine()));
            }
        }
        return contactos;
    }
}
