import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Servidor {

    private static final int PUERTO = 5000;
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        log("=== SERVIDOR SOCKET INICIADO ===");
        log("Escuchando en el puerto " + PUERTO + "...");
        log("Comandos soportados: RESOLVE \"expresion\" | EXIT");
        log("================================================");

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {

            while (true) {
                log("Esperando conexion de un cliente...");

                try (Socket socketCliente = serverSocket.accept()) {
                    String ipCliente = socketCliente.getInetAddress().getHostAddress();
                    log(">>> Cliente conectado desde: " + ipCliente);

                    manejarCliente(socketCliente);

                    log("<<< Cliente " + ipCliente + " desconectado.");
                    log("================================================");
                }
            }

        } catch (IOException e) {
            log("[ERROR CRITICO] No se pudo iniciar el servidor: " + e.getMessage());
        }
    }

    private static void manejarCliente(Socket socket) {
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter salida   = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)
        ) {
            // Mensaje de bienvenida al cliente
            salida.println("Bienvenido al Servidor de Chat/Calculadora!");
            salida.println("Comandos disponibles:");
            salida.println("  RESOLVE \"expresion\"  -> resuelve una expresion matematica");
            salida.println("  EXIT                  -> cierra la conexion");
            salida.println("Cualquier otro mensaje sera respondido como eco.");
            salida.println("--------------------------------------------------");

            String mensajeRecibido;

            while ((mensajeRecibido = entrada.readLine()) != null) {
                log("[RECIBIDO] " + mensajeRecibido);

                if (mensajeRecibido.trim().equalsIgnoreCase("EXIT")) {
                    salida.println("Hasta luego! Cerrando conexion...");
                    log("[INFO] Cliente solicitó desconexion.");
                    break;
                }

                String respuesta = procesarMensaje(mensajeRecibido.trim());
                salida.println(respuesta);
                log("[ENVIADO] " + respuesta);
            }

        } catch (IOException e) {
            log("[ERROR] Problema de comunicacion con el cliente: " + e.getMessage());
        }
    }

    private static String procesarMensaje(String mensaje) {
        if (mensaje.toUpperCase().startsWith("RESOLVE")) {
            int inicio = mensaje.indexOf('"');
            int fin    = mensaje.lastIndexOf('"');

            if (inicio == -1 || fin == -1 || inicio == fin) {
                return "[ERROR] Formato invalido. Use: RESOLVE \"expresion\"  (ej: RESOLVE \"45*23/54+234\")";
            }

            String expresion = mensaje.substring(inicio + 1, fin).trim();

            if (expresion.isEmpty()) {
                return "[ERROR] La expresion esta vacia.";
            }

            return evaluarExpresion(expresion);
        }

        return "[SERVIDOR] Recibido: \"" + mensaje + "\"";
    }

    private static String evaluarExpresion(String expresion) {
        if (!expresion.matches("[0-9+\\-*/%.() ]+")) {
            return "[ERROR] Expresion invalida. Solo se permiten numeros y operadores: + - * / % ()";
        }

        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            if (engine == null) {
                return evaluarSimple(expresion);
            }

            Object resultado = engine.eval(expresion);
            return "[RESULTADO] " + expresion + " = " + resultado;

        } catch (ScriptException e) {
            return "[ERROR] No se pudo evaluar la expresion: " + e.getMessage();
        }
    }

    private static String evaluarSimple(String expresion) {
        try {
            double resultado = new EvaluadorMatematico(expresion).evaluar();
            if (resultado == Math.floor(resultado) && !Double.isInfinite(resultado)) {
                return "[RESULTADO] " + expresion + " = " + (long) resultado;
            }
            return "[RESULTADO] " + expresion + " = " + resultado;
        } catch (Exception e) {
            return "[ERROR] Expresion matematica invalida: " + e.getMessage();
        }
    }

    private static void log(String mensaje) {
        String hora = LocalDateTime.now().format(FORMATO_HORA);
        System.out.println("[" + hora + "] " + mensaje);
    }

    static class EvaluadorMatematico {
        private final String expresion;
        private int pos;

        EvaluadorMatematico(String expresion) {
            this.expresion = expresion.replaceAll("\\s+", "");
            this.pos = 0;
        }

        double evaluar() {
            double resultado = parsarExpresion();
            if (pos < expresion.length()) {
                throw new RuntimeException("Caracter inesperado en posicion " + pos + ": " + expresion.charAt(pos));
            }
            return resultado;
        }

        private double parsarExpresion() {
            double resultado = parsarTermino();
            while (pos < expresion.length() && (expresion.charAt(pos) == '+' || expresion.charAt(pos) == '-')) {
                char op = expresion.charAt(pos++);
                double derecha = parsarTermino();
                resultado = (op == '+') ? resultado + derecha : resultado - derecha;
            }
            return resultado;
        }

        private double parsarTermino() {
            double resultado = parsarFactor();
            while (pos < expresion.length() &&
                   (expresion.charAt(pos) == '*' || expresion.charAt(pos) == '/' || expresion.charAt(pos) == '%')) {
                char op = expresion.charAt(pos++);
                double derecha = parsarFactor();
                if (op == '*') resultado *= derecha;
                else if (op == '/') {
                    if (derecha == 0) throw new RuntimeException("Division por cero");
                    resultado /= derecha;
                } else {
                    resultado %= derecha;
                }
            }
            return resultado;
        }

        private double parsarFactor() {
            if (pos >= expresion.length()) {
                throw new RuntimeException("Expresion incompleta");
            }

            char c = expresion.charAt(pos);

            if (c == '-') {
                pos++;
                return -parsarFactor();
            }

            if (c == '(') {
                pos++; 
                double resultado = parsarExpresion();
                if (pos >= expresion.length() || expresion.charAt(pos) != ')') {
                    throw new RuntimeException("Parentesis de cierre faltante");
                }
                pos++; // consumir ')'
                return resultado;
            }

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (pos < expresion.length() && (Character.isDigit(expresion.charAt(pos)) || expresion.charAt(pos) == '.')) {
                    sb.append(expresion.charAt(pos++));
                }
                return Double.parseDouble(sb.toString());
            }

            throw new RuntimeException("Caracter no reconocido: " + c);
        }
    }
}
