 @Override
    public void run() {
        Paquete paquete;
        Servidor sys = Servidor.getInstance();

        try {
            // Inicialización de streams como atributos
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // Bucle principal de recepción de paquetes
            while ((paquete = (Paquete) in.readObject()) != null) {
                switch (paquete.getOperacion()) {
                    case "registro": {
                        Paquete reg = sys.registrarUsuario(
                                (UsuarioDTO) paquete.getContenido(),
                                socket.getInetAddress().getHostAddress(),
                                socket.getPort()
                        );
                        out.writeObject("ACK");
                        out.flush();
                        out.writeObject(reg);
                        out.flush();
                        enviarPendientes(paquete);
                        break;
                    }
                    case "consulta": {
                        Paquete resp = sys.manejarConsulta((UsuarioDTO) paquete.getContenido());
                        out.writeObject("ACK");
                        out.flush();
                        out.writeObject(resp);
                        out.flush();
                        break;
                    }
                    case "mensaje": {
                        Paquete resend = sys.manejarMensaje((MensajeDTO) paquete.getContenido());
                        if (resend != null) {
                            out.writeObject("ACK");
                            out.flush();
                            try {
                                enviarMensaje(resend);
                            } catch (SinConexionException e) {
                                sys.almacenarMensaje((MensajeDTO) paquete.getContenido());
                            }
                        }
                        break;
                    }
                    case "heartbeat": {
                        sys.actualizarHeartbeat((PuertoDTO) paquete.getContenido());
                        out.writeObject("ACK");
                        out.flush();
                        break;
                    }
                    default: {
                        System.err.println("Operación desconocida: " + paquete.getOperacion());
                        out.writeObject("ACK");
                        out.flush();
                        break;
                    }
                }
            }
        } catch (EOFException | SocketException e) {
            System.out.println("Cliente desconectado: " + socket.getRemoteSocketAddress());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
