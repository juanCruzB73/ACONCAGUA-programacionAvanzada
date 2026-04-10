package patrones.creacionales;


public class TorreControl {
    private static TorreControl instancia;
    private String nombre;

    private TorreControl() {
        this.nombre = "Torre de Control Central - Aconcagua";
    }

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

        if (torre1 == torre2) {
            System.out.println("Ambas variables referencian a la misma instancia de la Torre de Control.");
        }
    }
}
