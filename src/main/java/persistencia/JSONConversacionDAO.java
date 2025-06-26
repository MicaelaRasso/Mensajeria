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

public class JSONConversacionDAO implements ConversacionDAO {
    @Override
    public void save(ArrayList<Conversacion> conversaciones, String path) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path.concat(".json")))) {
            w.write("[\n");
            for (int i = 0; i < conversaciones.size(); i++) {
                Conversacion conv = conversaciones.get(i);
                w.write("  {\n");
                w.write("    \"contacto\": \"" + conv.getContacto().getNombre() + "\",\n");
                w.write("    \"notificacion\": " + conv.tieneNotificacion() + ",\n");
                w.write("    \"mensajes\": [\n");
                for (int j = 0; j < conv.getMensajes().size(); j++) {
                    Mensaje m = conv.getMensajes().get(j);
                    w.write("      {\"emisor\":\"" + m.getEmisor() + "\", \"contenido\":\"" + m.getContenido() + "\", \"fecha\":\"" + m.getFechaYHora().toString() + "\"}");
                    if (j < conv.getMensajes().size() - 1) w.write(",");
                    w.write("\n");
                }
                w.write("    ]\n  }");
                if (i < conversaciones.size() - 1) w.write(",");
                w.write("\n");
            }
            w.write("]");
        }
    }

    @Override
    public ArrayList<Conversacion> load(String path) throws IOException {
        ArrayList<Conversacion> conversaciones = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new FileReader(path.concat(".json")));
        String line;
        while ((line = r.readLine()) != null) sb.append(line.trim());
        String json = sb.toString();
        if (!json.startsWith("[") || !json.endsWith("]")) {
        	r.close();
        	return conversaciones;
        }
        json = json.substring(1, json.length() - 1);
        String[] convs = json.split("\\},\\{");
        for (String item : convs) {
            item = item.trim();
            if (!item.startsWith("{")) item = "{" + item;
            if (!item.endsWith("}")) item = item + "}";
            String nombre = item.split("\\\"contacto\\\":\\\"")[1].split("\\\"")[0];
            boolean noti = Boolean.parseBoolean(item.split("\\\"notificacion\\\":")[1].split(",")[0]);
            String array = item.split("\\\"mensajes\\\":\\[")[1].split("]")[0];
            Contacto c = new Contacto(nombre);
            Conversacion conv = new Conversacion(c);
            conv.setNotificacion(noti);
            if (!array.trim().isEmpty()) {
                String[] messages = array.split("\\},");
                for (String msg : messages) {
                    if (!msg.endsWith("}")) msg += "}";
                    String em = msg.split("\\\"emisor\\\":\\\"")[1].split("\\\"")[0];
                    String cont = msg.split("\\\"contenido\\\":\\\"")[1].split("\\\"")[0];
                    String fe = msg.split("\\\"fecha\\\":\\\"")[1].split("\\\"")[0];
                    conv.recibirMensaje(cont, LocalDateTime.parse(fe), new Contacto(em));
                }
            }
            conversaciones.add(conv);
        }
        r.close();
        return conversaciones;
    }

    @Override
    public void saveContactos(ArrayList<Contacto> contactos, String path) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path.concat(".json")))) {
            w.write("[\n");
            for (int i = 0; i < contactos.size(); i++) {
                w.write("  {\"nombre\":\"" + contactos.get(i).getNombre() + "\"}");
                if (i < contactos.size() - 1) w.write(",");
                w.write("\n");
            }
            w.write("]");
        }
    }

    @Override
    public ArrayList<Contacto> loadContactos(String path) throws IOException {
        ArrayList<Contacto> contactos = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new FileReader(path.concat(".json")));
        String line;
        while ((line = r.readLine()) != null) sb.append(line.trim());
        String json = sb.toString();
        if (!json.startsWith("[") || !json.endsWith("]")) return contactos;
        json = json.substring(1, json.length() - 1);
        if (json.trim().isEmpty()) return contactos;
        String[] items = json.split("\\},\\{");
        for (String item : items) {
            item = item.trim();
            if (!item.startsWith("{")) item = "{" + item;
            if (!item.endsWith("}")) item += "}";
            String nombre = item.split("\"nombre\":\"")[1].split("\"")[0];
            contactos.add(new Contacto(nombre));
        }
        return contactos;
    }
    
}
