package practico1.tp3;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal que ejecuta el Sistema de Gestión Inmobiliaria.
 * Demuestra:
 * 1. La inicialización y carga de propiedades con diferentes modalidades de negocio.
 * 2. El procesamiento de transacciones (alquiler y venta) utilizando polimorfismo seguro.
 * 3. La explicación didáctica de cómo se violaría el principio LSP y cómo nuestro diseño lo resuelve.
 */
public class AplicacionInmobiliaria {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   SISTEMA DE GESTIÓN INMOBILIARIA - TP3 (LSP)   ");
        System.out.println("==================================================");

        // 1. Crear las propiedades en la inmobiliaria
        PropiedadAlquiler deptoAlquiler = new PropiedadAlquiler(
                101, "Av. San Martín 1500, Mendoza", 45.5, "Marta Gómez", 120000.0);
        
        PropiedadVenta casaVenta = new PropiedadVenta(
                202, "Calle Las Heras 340, Godoy Cruz", 120.0, "Carlos Pérez", 85000.0);
        
        PropiedadAlquilerVenta duplexMixto = new PropiedadAlquilerVenta(
                303, "Ruta 60 Km 12, Luján de Cuyo", 180.0, "Inmobiliaria Aconcagua", 250000.0, 150000.0);

        // Imprimir estado inicial
        System.out.println("\n--- ESTADO INICIAL DE LAS PROPIEDADES ---");
        System.out.println(deptoAlquiler.getDetallesGenerales());
        System.out.println(casaVenta.getDetallesGenerales());
        System.out.println(duplexMixto.getDetallesGenerales());
        System.out.println("==================================================\n");

        // 2. Instanciar el procesador de transacciones (Cliente del sistema)
        ProcesadorTransacciones procesador = new ProcesadorTransacciones();

        // 3. Simular transacciones válidas sin violaciones de tipos
        System.out.println("--- PROCESANDO TRANSACCIONES DE ALQUILER ---");
        // deptoAlquiler implementa Alquilable, es seguro pasarlo
        procesador.procesarAlquiler(deptoAlquiler, "Juan Cruz Berrios", 24);
        
        // duplexMixto también implementa Alquilable, es seguro pasarlo
        procesador.procesarAlquiler(duplexMixto, "Candela Puerta", 12);

        System.out.println("--- PROCESANDO TRANSACCIONES DE VENTA ---");
        // casaVenta implementa Vendible, es seguro pasarlo
        procesador.procesarVenta(casaVenta, "Esteban Quito");
        
        // duplexMixto también implementa Vendible, es seguro pasarlo
        procesador.procesarVenta(duplexMixto, "Sofía Rodríguez");

        // 4. Imprimir estado final de las propiedades
        System.out.println("\n--- ESTADO FINAL DE LAS PROPIEDADES ---");
        System.out.println(deptoAlquiler.getDetallesGenerales());
        System.out.println(casaVenta.getDetallesGenerales());
        System.out.println(duplexMixto.getDetallesGenerales());
        System.out.println("==================================================\n");

        // Explicación de la Violación del LSP
        mostrarExplicacionLSP();
    }

    /**
     * Muestra de forma didáctica en consola la explicación sobre la violación del LSP.
     */
    private static void mostrarExplicacionLSP() {
        System.out.println("==================================================");
        System.out.println("    EXPLICACIÓN DIDÁCTICA: VIOLACIÓN DEL LSP      ");
        System.out.println("==================================================");
        System.out.println("¿Cómo se violaría el Principio de Sustitución de Liskov en este rubro?");
        System.out.println("Si hubiésemos diseñado una única clase base 'Propiedad' con los métodos:");
        System.out.println("   - public double getPrecioAlquiler();");
        System.out.println("   - public void alquilar(String inquilino);");
        System.out.println("   - public double getPrecioVenta();");
        System.out.println("   - public void vender(String comprador);\n");
        System.out.println("Entonces, al crear 'PropiedadVenta', estaríamos obligados a implementar");
        System.out.println("'alquilar()', teniendo que escribir algo como:");
        System.out.println("   @Override");
        System.out.println("   public void alquilar(String inquilino) {");
        System.out.println("       throw new UnsupportedOperationException(\"¡Esta propiedad no se alquila!\");");
        System.out.println("   }\n");
        System.out.println("Si un cliente intentara iterar sobre una lista de Propiedad:");
        System.out.println("   for (Propiedad p : listaPropiedades) {");
        System.out.println("       p.alquilar(\"Inquilino Anonimo\"); // ¡CRASH! en tiempo de ejecución al llegar a la de venta.");
        System.out.println("   }\n");
        System.out.println("Eso rompe el contrato de la superclase. La subclase NO puede sustituir a la superclase.");
        System.out.println("--------------------------------------------------");
        System.out.println("¿Cómo lo resolvimos aplicando LSP?");
        System.out.println("1. Separamos los comportamientos específicos en interfaces: 'Alquilable' y 'Vendible'.");
        System.out.println("2. La clase abstracta 'Propiedad' solo contiene lo común (dirección, metros cuadrados, etc.).");
        System.out.println("3. Las clases cliente ('ProcesadorTransacciones') interactúan con las interfaces.");
        System.out.println("De esta forma, es imposible intentar alquilar una propiedad que es de venta exclusiva");
        System.out.println("en tiempo de compilación. ¡El sistema es robusto, seguro y respeta LSP!");
        System.out.println("==================================================");
    }
}
