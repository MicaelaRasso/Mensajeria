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

public class XMLConversacionDAO implements ConversacionDAO {
    @Override
    public void save(ArrayList<Conversacion> conversaciones, String path) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path.concat(".xml")))) {
            w.write("<conversaciones>\n");
            for (Conversacion conv : conversaciones) {
                w.write("  <conversacion>\n");
                w.write("    <contacto>" + conv.getContacto().getNombre() + "</contacto>\n");
                w.write("    <notificacion>" + conv.tieneNotificacion() + "</notificacion>\n");
                w.write("    <mensajes>\n");
                for (Mensaje m : conv.getMensajes()) {
                    w.write("      <mensaje>\n");
                    w.write("        <emisor>" + m.getEmisor() + "</emisor>\n");
                    w.write("        <contenido>" + m.getContenido() + "</contenido>\n");
                    w.write("        <fecha>" + m.getFechaYHora().toString() + "</fecha>\n");
                    w.write("      </mensaje>\n");
                }
                w.write("    </mensajes>\n");
                w.write("  </conversacion>\n");
            }
            w.write("</conversaciones>");
            w.close();
        }
    }

    @Override
    public ArrayList<Conversacion> load(String path) throws IOException {
        ArrayList<Conversacion> conversaciones = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(path.concat(".xml")));
        String line;
        String nombre = "";
        boolean noti = false;
        ArrayList<String> msgs = new ArrayList<>();
        ArrayList<String> fechas = new ArrayList<>();
        ArrayList<String> emisors = new ArrayList<>();
        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (line.equals("<conversacion>")) {
                nombre = "";
                noti = false;
                msgs.clear(); fechas.clear(); emisors.clear();
            } else if (line.startsWith("<contacto>")) {
                nombre = line.replace("<contacto>", "").replace("</contacto>", "");
            } else if (line.startsWith("<notificacion>")) {
                noti = Boolean.parseBoolean(line.replace("<notificacion>", "").replace("</notificacion>", ""));
            } else if (line.startsWith("<mensaje>")) {
                String em = r.readLine().trim(); em = em.replace("<emisor>", "").replace("</emisor>", ""); emisors.add(em);
                String cont = r.readLine().trim(); cont = cont.replace("<contenido>", "").replace("</contenido>", ""); msgs.add(cont);
                String fe = r.readLine().trim(); fe = fe.replace("<fecha>", "").replace("</fecha>", ""); fechas.add(fe);
            } else if (line.equals("</conversacion>")) {
                Contacto c = new Contacto(nombre);
                Conversacion conv = new Conversacion(c);
                conv.setNotificacion(noti);
                for (int i = 0; i < emisors.size(); i++) {
                    conv.recibirMensaje(msgs.get(i), LocalDateTime.parse(fechas.get(i)), new Contacto(emisors.get(i)));
                }
                conversaciones.add(conv);
            }
        }
        r.close();
        return conversaciones;
    }
    
    @Override
    public void saveContactos(ArrayList<Contacto> contactos, String path) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path.concat(".xml")))) {
            w.write("<contactos>\n");
            for (Contacto c : contactos) {
                w.write("  <contacto>" + c.getNombre() + "</contacto>\n");
            }
            w.write("</contactos>");
            w.close();
        }
    }

    @Override
    public ArrayList<Contacto> loadContactos(String path) throws IOException {
        ArrayList<Contacto> contactos = new ArrayList<>();
        BufferedReader r = new BufferedReader(new FileReader(path.concat(".xml")));
        String line;
        while ((line = r.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("<contacto>")) {
                String nombre = line.replace("<contacto>", "").replace("</contacto>", "");
                contactos.add(new Contacto(nombre));
            }
        }
        r.close();
        return contactos;
    }
}
