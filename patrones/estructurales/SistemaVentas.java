package patrones.estructurales;

interface Pasaje {
    String getDescripcion();
    double getCosto();
}

class PasajeBasico implements Pasaje {
    @Override
    public String getDescripcion() {
        return "Pasaje de Clase Económica";
    }

    @Override
    public double getCosto() {
        return 500.0;
    }
}

abstract class PasajeDecorator implements Pasaje {
    protected Pasaje pasajeDecorado;

    public PasajeDecorator(Pasaje pasaje) {
        this.pasajeDecorado = pasaje;
    }

    public String getDescripcion() {
        return pasajeDecorado.getDescripcion();
    }

    public double getCosto() {
        return pasajeDecorado.getCosto();
    }
}

class ComidaPremiumDecorator extends PasajeDecorator {
    public ComidaPremiumDecorator(Pasaje pasaje) {
        super(pasaje);
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Comida Premium";
    }

    @Override
    public double getCosto() {
        return super.getCosto() + 50.0;
    }
}

class EquipajeExtraDecorator extends PasajeDecorator {
    public EquipajeExtraDecorator(Pasaje pasaje) {
        super(pasaje);
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Equipaje Extra (23kg)";
    }

    @Override
    public double getCosto() {
        return super.getCosto() + 30.0;
    }
}

public class SistemaVentas {
    public static void main(String[] args) {
        Pasaje miPasaje = new PasajeBasico();
        System.out.println("Pedido 1: " + miPasaje.getDescripcion() + " | Costo: $" + miPasaje.getCosto());

        miPasaje = new ComidaPremiumDecorator(miPasaje);
        System.out.println("Pedido 2: " + miPasaje.getDescripcion() + " | Costo: $" + miPasaje.getCosto());

        miPasaje = new EquipajeExtraDecorator(miPasaje);
        System.out.println("Pedido 3: " + miPasaje.getDescripcion() + " | Costo: $" + miPasaje.getCosto());
    }
}
