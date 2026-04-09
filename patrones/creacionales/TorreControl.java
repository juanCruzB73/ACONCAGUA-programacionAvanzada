package patrones.creacionales;

/**
 * Ejemplo de Patrón Creacional: Singleton
 * Representa la Torre de Control de un Aeropuerto. Solo puede haber una.
 */
public class TorreControl {
    private static TorreControl instancia;
    private String nombre;

    // Constructor privado para evitar instanciación externa
    private TorreControl() {
        this.nombre = "Torre de Control Central - Aconcagua";
    }

    // Método estático para obtener la única instancia
    public static synchronized TorreControl getInstancia() {
        if (instancia == null) {
            instancia = new TorreControl();
        }
        return instancia;
    }

    public void enviarInstrucciones(String vuelo, String pista) {
        System.out.println(nombre + ": Vuelo " + vuelo + " autorizado para aterrizar en " + pista);
    }

    public static void main(String[] args) {
        TorreControl torre1 = TorreControl.getInstancia();
        TorreControl torre2 = TorreControl.getInstancia();

        torre1.enviarInstrucciones("AR1234", "Pista 1");

        // Comprobación de que es la misma instancia
        if (torre1 == torre2) {
            System.out.println("Ambas variables referencian a la misma instancia de la Torre de Control.");
        }
    }
}
