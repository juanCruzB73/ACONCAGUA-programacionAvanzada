import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {

    private static final String HOST   = "localhost";
    private static final int    PUERTO = 5000;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE SOCKET ===");
        System.out.println("Conectando a " + HOST + ":" + PUERTO + "...");

        try (Socket socket = new Socket(HOST, PUERTO)) {
            System.out.println("Conexion establecida!\n");

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    salida  = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            HiloReceptor receptor = new HiloReceptor(entrada);
            Thread hiloReceptor = new Thread(receptor);
            hiloReceptor.setDaemon(true); 
            hiloReceptor.start();

            Scanner scanner = new Scanner(System.in);
            System.out.println("Escribe un mensaje y presiona ENTER para enviarlo.");
            System.out.println("Comandos: RESOLVE \"expresion\"  |  EXIT");
            System.out.println("----------------------------------------------");
            System.out.print("> ");

            while (scanner.hasNextLine()) {
                String mensajeUsuario = scanner.nextLine().trim();

                if (mensajeUsuario.isEmpty()) {
                    System.out.print("> ");
                    continue;
                }

                salida.println(mensajeUsuario);

                if (mensajeUsuario.equalsIgnoreCase("EXIT")) {
                    Thread.sleep(500);
                    System.out.println("\n[Cliente] Conexion cerrada. Hasta luego!");
                    break;
                }

                Thread.sleep(150);
                System.out.print("> ");
            }

        } catch (ConnectException e) {
            System.err.println("[ERROR] No se pudo conectar al servidor.");
            System.err.println("        Asegurate de que el Servidor este corriendo en " + HOST + ":" + PUERTO);
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
                    System.out.println("\n[Servidor] " + linea);
                    System.out.print("> ");
                }
            } catch (IOException e) {
                if (activo) {
                    System.err.println("\n[Cliente] Conexion con el servidor perdida.");
                }
            }
        }

        void detener() {
            activo = false;
        }
    }
}
