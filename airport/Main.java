package practico1.airport;

public class Main {
    public static void main(String[] args){
        //int nroVuelo,String partida,String destino,int nroAvion,String aerolinea,String nombrePiloto
        Vuelo vuelo1=new Vuelo(1,"ARGENTINA","ESPAÑA",1,"argentinas","manuel");
        vuelo1.añadirPasajero("elliot",1,0);
        vuelo1.añadirPasajero("elliot",1,1);
        vuelo1.añadirPasajero("satan",2,2);
        vuelo1.añadirPasajero("satan",2,2);//no ayade porque asiento esta ocupado
        vuelo1.añadirPasajero("juan",3,4);
        vuelo1.añadirPasajero("juan",3,5);
        vuelo1.añadirPasajero("juan",3,6);

        System.out.println("numero de pasajeros "+vuelo1.getPasajeros());
        System.out.println("numero del vuelo es "+vuelo1.getNroVuelo());
        System.out.println("pais de partida: "+vuelo1.partida.getNombre());
        System.out.println("pais de destino: "+vuelo1.destino.getNombre());
        System.out.println("pais de nombre del piloto: "+vuelo1.piloto.getNombre());
    }
}
