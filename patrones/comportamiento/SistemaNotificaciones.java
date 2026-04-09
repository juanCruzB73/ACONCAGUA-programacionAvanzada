package patrones.comportamiento;

import java.util.ArrayList;
import java.util.List;


interface PasajeroObserver {
    void actualizar(String vuelo, String estado);
}

class Vuelo {
    private String idVuelo;
    private String estado;
    private List<PasajeroObserver> observadores = new ArrayList<>();

    public Vuelo(String idVuelo) {
        this.idVuelo = idVuelo;
        this.estado = "A tiempo";
    }

    public void registrarObservador(PasajeroObserver p) {
        observadores.add(p);
    }

    public void eliminarObservador(PasajeroObserver p) {
        observadores.remove(p);
    }

    public void setEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
        notificar();
    }

    private void notificar() {
        for (PasajeroObserver p : observadores) {
            p.actualizar(idVuelo, estado);
        }
    }
}

class PasajeroApp implements PasajeroObserver {
    private String nombre;

    public PasajeroApp(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void actualizar(String vuelo, String estado) {
        System.out.println("Notificación para " + nombre + ": El vuelo " + vuelo + " ahora está " + estado);
    }
}

public class SistemaNotificaciones {
    public static void main(String[] args) {
        Vuelo flightAR101 = new Vuelo("AR101");

        PasajeroApp p1 = new PasajeroApp("Juan Perez");
        PasajeroApp p2 = new PasajeroApp("Maria Garcia");

        flightAR101.registrarObservador(p1);
        flightAR101.registrarObservador(p2);

        System.out.println("--- Cambio de estado del vuelo ---");
        flightAR101.setEstado("Demorado");

        System.out.println("\n--- Maria se da de baja de las alertas ---");
        flightAR101.eliminarObservador(p2);

        System.out.println("\n--- Otro cambio de estado ---");
        flightAR101.setEstado("Cancelado");
    }
}
