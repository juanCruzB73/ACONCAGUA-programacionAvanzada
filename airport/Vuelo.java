package practico1.airport;
import java.util.ArrayList;
public class Vuelo {
    private int nroVuelo;
     Ciudad partida;
     Ciudad destino;
     ArrayList<Object> pasajeros;
     Avion avion;
     Piloto piloto;
    public Vuelo(int nroVuelo,String partida,String destino,int nroAvion,String aerolinea,String nombrePiloto){
        this.nroVuelo=nroVuelo;
        this.partida=new Ciudad(partida);
        this.destino=new Ciudad(destino);
        this.avion=new Avion(nroAvion,aerolinea);
        this.piloto=new Piloto(nombrePiloto);
        this.pasajeros=new ArrayList<>();
    }

    public Object getPasajeros() {
        return pasajeros.size();
    }
    public void añadirPasajero(String name,int nroPasajero,int nroAsiento){
        if(!pasajeroExistente(nroAsiento)){
            pasajeros.add(new Pasajero(name,nroPasajero,nroAsiento));
        }
    }
    public int getNroVuelo() {
        return nroVuelo;
    }

    public void setNroVuelo(int nroVuelo) {
        this.nroVuelo = nroVuelo;
    }

    public boolean pasajeroExistente(int nroAsiento){
        for(int i=0;i<pasajeros.size();i++) {
            if (this.avion.getAsientos(i).getNroAsiento() == nroAsiento) {
                if(this.avion.getAsientos(i).getOcupado()){
                    return true;
                }
            }
        }
        this.avion.getAsientos(nroAsiento).setOcupado(true);
        return false;
    }
}
