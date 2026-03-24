package practico1.airport;

public class Avion {
    private int nroAvion;
    private String aerolinea;
    private Asiento[] asientos = new Asiento[10];
    public Avion (int nroAvion, String aerolinea){
        this.nroAvion=nroAvion;
        this.aerolinea=aerolinea;
        for(int i=0;i<10;i++){
            asientos[i]=new Asiento(i,false);
        }
    }
    public Asiento getAsientos(int index){
        return asientos[index];
    }
    public int getNroAvion() {
        return nroAvion;
    }
    public String getAerolinea() {
        return aerolinea;
    }
    public void setAerolinea(String aerolinea) {
        this.aerolinea = aerolinea;
    }

    public void setNroAvion(int nroAvion) {
        this.nroAvion = nroAvion;
    }
}
