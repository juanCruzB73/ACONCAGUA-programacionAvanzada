package practico1.airport;

public class Asiento {
    private int nroAsiento;
    private boolean ocupado;
    public Asiento(int nroAsiento,boolean ocupado){
        this.nroAsiento=nroAsiento;
        this.ocupado=ocupado;
    }

    public int getNroAsiento() {
        return nroAsiento;
    }
    public void setNroAsiento(int nroAsiento) {
        this.nroAsiento = nroAsiento;
    }
    public boolean getOcupado(){
        return ocupado;
    }
    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }
}
