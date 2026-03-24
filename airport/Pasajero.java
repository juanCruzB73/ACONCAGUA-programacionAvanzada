package practico1.airport;

public class Pasajero {
    private String name;
    private int nroPasajero;
    private int nroAsiento;

    public Pasajero(String name,int nroPasajero,int nroAsiento){
        this.name=name;
        this.nroPasajero=nroPasajero;
        this.nroAsiento=nroAsiento;
    }

    public int getNroAsiento() {
        return nroAsiento;
    }

    public int getNroPasajero() {
        return nroPasajero;
    }

    public String getName() {
        return name;
    }

    public void setNroAsiento(int nroAsiento) {
        this.nroAsiento = nroAsiento;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNroPasajero(int nroPasajero) {
        this.nroPasajero = nroPasajero;
    }

}
