package practico1.sockets.src;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    private static final String HOST   = "localhost";
    private static final int    PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE CHAT MULTI-HILO ===");
        System.out.println("Conectando a " + HOST + ":" + PUERTO + "...");

        try (Socket socket = new Socket(HOST, PUERTO)) {
            System.out.println("Conexion establecida!\n");

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    salida  = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            Scanner scanner = new Scanner(System.in);

            // Handshake de nombre de usuario
            String servidorMsg = entrada.readLine();
            if (servidorMsg != null && servidorMsg.startsWith("CONEXION_EXITOSA")) {
                System.out.println(servidorMsg.split(":")[1].trim());
                System.out.print("Nombre: ");
                String user = scanner.nextLine().trim();
                salida.println(user.isEmpty() ? "Anonimo" : user);
            }

            HiloReceptor receptor = new HiloReceptor(entrada);
            Thread hiloReceptor = new Thread(receptor);
            hiloReceptor.setDaemon(true); 
            hiloReceptor.start();

            System.out.println("\nComandos: HELP, FECHA, HORA, LIST, RESOLVE \"exp\", ALL \"msg\", C<User> \"msg\", EXIT");
            System.out.println("----------------------------------------------");

            while (scanner.hasNextLine()) {
                System.out.print("> ");
                String mensajeUsuario = scanner.nextLine().trim();

                if (mensajeUsuario.isEmpty()) continue;

                salida.println(mensajeUsuario);

                if (mensajeUsuario.equalsIgnoreCase("EXIT")) {
                    Thread.sleep(500);
                    System.out.println("\n[Cliente] Desconectado. Hasta luego!");
                    break;
                }
            }

        } catch (ConnectException e) {
            System.err.println("[ERROR] No se pudo conectar al servidor.");
        } catch (IOException e) {
            System.err.println("[ERROR] Problema de comunicacion: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static class HiloReceptor implements Runnable {
        private final BufferedReader entrada;
        private volatile boolean activo = true;

        HiloReceptor(BufferedReader entrada) {
            this.entrada = entrada;
        }

        @Override
        public void run() {
            try {
                String linea;
                while (activo && (linea = entrada.readLine()) != null) {
                    System.out.println("\r" + linea);
                    System.out.print("> ");
                }
            } catch (IOException e) {
                if (activo) {
                    System.err.println("\n[Cliente] Conexion con el servidor perdida.");
                    System.exit(0);
                }
            }
        }

        void detener() {
            activo = false;
        }
    }
}
