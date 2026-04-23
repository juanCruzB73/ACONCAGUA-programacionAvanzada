import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Servidor {

    private static final int PUERTO = 5000;
    private static final DateTimeFormatter FORMATO_LOG = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Map<String, ClientHandler> clientesConectados = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        log("=== SERVIDOR MULTI-HILO INICIADO ===");
        log("Escuchando en el puerto " + PUERTO + "...");
        log("================================================");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            while (true) {
                Socket socketCliente = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socketCliente);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            log("[ERROR CRITICO] No se pudo iniciar el servidor: " + e.getMessage());
        }
    }

    private static void log(String mensaje) {
        String hora = LocalDateTime.now().format(FORMATO_LOG);
        System.out.println("[" + hora + "] " + mensaje);
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private String nombreUsuario;
        private PrintWriter salida;
        private BufferedReader entrada;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String ipCliente = socket.getInetAddress().getHostAddress();
            try {
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                // Protocolo de inicio: El cliente debe enviar su nombre de usuario
                salida.println("CONEXION_EXITOSA: Ingrese su nombre de usuario:");
                String nombreSugerido = entrada.readLine();
                if (nombreSugerido == null || nombreSugerido.trim().isEmpty()) {
                    nombreSugerido = "UsuarioAnonimo";
                }
                
                this.nombreUsuario = asignarNombreUnico(nombreSugerido.trim());
                clientesConectados.put(this.nombreUsuario, this);
                
                log(">>> Cliente conectado: " + this.nombreUsuario + " desde " + ipCliente);
                
                enviarMenuBienvenida();

                String mensajeRecibido;
                while ((mensajeRecibido = entrada.readLine()) != null) {
                    log("[" + nombreUsuario + "] " + mensajeRecibido);
                    procesarComando(mensajeRecibido.trim());
                }

            } catch (IOException e) {
                log("[INFO] Conexion perdida con " + (nombreUsuario != null ? nombreUsuario : ipCliente));
            } finally {
                desconectar();
            }
        }

        private String asignarNombreUnico(String base) {
            String nombre = base;
            int contador = 1;
            while (clientesConectados.containsKey(nombre)) {
                nombre = base + "_" + contador++;
            }
            return nombre;
        }

        private void enviarMenuBienvenida() {
            salida.println("--------------------------------------------------");
            salida.println("Bienvenido, " + nombreUsuario + "!");
            salida.println("Comandos disponibles:");
            salida.println("  HELP                  -> Muestra esta ayuda");
            salida.println("  FECHA                 -> Muestra la fecha actual");
            salida.println("  HORA                  -> Muestra la hora actual");
            salida.println("  LIST                  -> Lista clientes conectados");
            salida.println("  RESOLVE \"exp\"         -> Resuelve expresion matematica");
            salida.println("  ALL \"mensaje\"         -> Envia mensaje a todos");
            salida.println("  C<User1>,C<User2> \"msg\" -> Mensaje privado");
            salida.println("  EXIT                  -> Cerrar conexion");
            salida.println("--------------------------------------------------");
        }

        private void procesarComando(String mensaje) {
            String mensajeUpper = mensaje.toUpperCase();

            if (mensajeUpper.equals("EXIT")) {
                salida.println("Hasta luego! Cerrando conexion...");
                desconectar();
            } else if (mensajeUpper.equals("HELP")) {
                enviarMenuBienvenida();
            } else if (mensajeUpper.equals("FECHA")) {
                salida.println("[FECHA] " + LocalDate.now());
            } else if (mensajeUpper.equals("HORA")) {
                salida.println("[HORA] " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            } else if (mensajeUpper.equals("LIST")) {
                salida.println("[CLIENTES] " + String.join(", ", clientesConectados.keySet()));
            } else if (mensajeUpper.startsWith("RESOLVE")) {
                salida.println(evaluarExpresionComando(mensaje));
            } else if (mensajeUpper.startsWith("ALL ")) {
                difundirMensaje(mensaje.substring(4).trim());
            } else if (mensajeUpper.startsWith("C")) {
                manejarMensajePrivado(mensaje);
            } else {
                salida.println("[SERVIDOR] Comando no reconocido. Escriba HELP para ayuda.");
            }
        }

        private void difundirMensaje(String msg) {
            if (msg.startsWith("\"") && msg.endsWith("\"")) {
                msg = msg.substring(1, msg.length() - 1);
            }
            String broadcast = "[ALL] " + nombreUsuario + ": " + msg;
            for (ClientHandler h : clientesConectados.values()) {
                if (h != this) h.salida.println(broadcast);
            }
            salida.println("[OK] Mensaje enviado a todos.");
        }

        private void manejarMensajePrivado(String mensaje) {
            // Formato esperado: C1,C2 "mensaje"
            try {
                int indexComillas = mensaje.indexOf('"');
                if (indexComillas == -1) {
                    salida.println("[ERROR] Formato invalido. Use: C<User> \"mensaje\"");
                    return;
                }

                String destinatariosRaw = mensaje.substring(0, indexComillas).trim();
                String contenido = mensaje.substring(indexComillas).trim();
                if (contenido.startsWith("\"") && contenido.endsWith("\"")) {
                    contenido = contenido.substring(1, contenido.length() - 1);
                }

                String[] destinatarios = destinatariosRaw.split(",");
                List<String> noEncontrados = new ArrayList<>();

                for (String d : destinatarios) {
                    String target = d.trim();
                    if (target.startsWith("C") || target.startsWith("c")) {
                        target = target.substring(1);
                    }
                    
                    ClientHandler handler = clientesConectados.get(target);
                    if (handler != null) {
                        handler.salida.println("[PRIVADO] " + nombreUsuario + ": " + contenido);
                    } else {
                        noEncontrados.add(target);
                    }
                }

                if (noEncontrados.isEmpty()) {
                    salida.println("[OK] Mensaje enviado.");
                } else {
                    salida.println("[AVISO] No se pudo enviar a: " + String.join(", ", noEncontrados));
                }

            } catch (Exception e) {
                salida.println("[ERROR] Error al procesar mensaje privado.");
            }
        }

        private void desconectar() {
            try {
                if (nombreUsuario != null) {
                    clientesConectados.remove(nombreUsuario);
                    log("<<< Cliente desconectado: " + nombreUsuario);
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Silencioso
            }
        }

        // --- Lógica de Calculadora ---
        private String evaluarExpresionComando(String mensaje) {
            int inicio = mensaje.indexOf('"');
            int fin    = mensaje.lastIndexOf('"');
            if (inicio == -1 || fin == -1 || inicio == fin) {
                return "[ERROR] Use: RESOLVE \"expresion\"";
            }
            String expresion = mensaje.substring(inicio + 1, fin).trim();
            return evaluarExpresion(expresion);
        }

        private String evaluarExpresion(String expresion) {
            if (!expresion.matches("[0-9+\\-*/%.() ]+")) {
                return "[ERROR] Expresion invalida.";
            }
            try {
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                if (engine != null) {
                    Object resultado = engine.eval(expresion);
                    return "[RESULTADO] " + expresion + " = " + resultado;
                }
                return "[ERROR] Motor JS no disponible.";
            } catch (ScriptException e) {
                return "[ERROR] Matematico: " + e.getMessage();
            }
        }
    }
}

