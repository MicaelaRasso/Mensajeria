package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // 1) Leer todo el JSON en un String
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new FileReader(path.concat(".json")))) {
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line.trim());
            }
        }
        String json = sb.toString();

        // 2) Validar formato de array JSON
        if (!json.startsWith("[") || !json.endsWith("]")) {
            return conversaciones;
        }
        json = json.substring(1, json.length() - 1);  // quitar corchetes externos

        // 3) Extraer cada objeto de nivel 1 contando llaves
        List<String> items = new ArrayList<>();
        int depth = 0, start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            }
            if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    items.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }

        // 4) Prepara los patrones
        Pattern pContacto = Pattern.compile("\"contacto\"\\s*:\\s*\"([^\"]+)\"");
        Pattern pNoti     = Pattern.compile("\"notificacion\"\\s*:\\s*(true|false)");
        Pattern pMensajes = Pattern.compile("\"mensajes\"\\s*:\\s*\\[(.*)\\]");
        Pattern pMsg      = Pattern.compile("\\{\\s*\"emisor\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"contenido\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"fecha\"\\s*:\\s*\"([^\"]+)\"\\s*\\}");

        // 5) Parsea cada conversación
        for (String item : items) {
            Matcher m;

            // contacto
            m = pContacto.matcher(item);
            if (!m.find()) {
                System.err.println("⚠️ Falta o mal 'contacto' en: " + item);
                continue;
            }
            String nombre = m.group(1);

            // notificacion
            m = pNoti.matcher(item);
            if (!m.find()) {
                System.err.println("⚠️ Falta o mal 'notificacion' en: " + item);
                continue;
            }
            boolean noti = Boolean.parseBoolean(m.group(1));

            // array de mensajes (todo el bloque interior)
            m = pMensajes.matcher(item);
            if (!m.find()) {
                System.err.println("⚠️ Falta o mal 'mensajes' en: " + item);
                continue;
            }
            String mensajesBlock = m.group(1);

            // Construir Conversacion
            Contacto c = new Contacto(nombre);
            Conversacion conv = new Conversacion(c);
            conv.setNotificacion(noti);

            // extraer cada mensaje individual
            Matcher mm = pMsg.matcher(mensajesBlock);
            while (mm.find()) {
                String em = mm.group(1);
                String cont = mm.group(2);
                String fe = mm.group(3);
                conv.recibirMensaje(cont, LocalDateTime.parse(fe), new Contacto(em));
            }

            conversaciones.add(conv);
        }

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
