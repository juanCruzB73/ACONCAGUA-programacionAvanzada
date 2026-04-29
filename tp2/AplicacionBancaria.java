package practico1.tp2;

// 1. Clase solo para mantener el Estado (Datos)
// 2. Clase para manejar Lógica de Negocio (Responsabilidad Única)
class ServicioTransaccion {
    public void depositar(CuentaBancaria cuenta, double monto) {
        cuenta.setSaldo(cuenta.getSaldo() + monto);
        System.out.println("Depositado: $" + monto);
    }

    public void retirar(CuentaBancaria cuenta, double monto) {
        if (cuenta.getSaldo() >= monto) {
            cuenta.setSaldo(cuenta.getSaldo() - monto);
            System.out.println("Retirado: $" + monto);
        } else {
            System.out.println("¡Saldo insuficiente!");
        }
    }
}

// 3. Clase para manejar la Presentación (Responsabilidad Única)
class ImpresoraCuenta {
    public void imprimirDetalles(CuentaBancaria cuenta) {
        System.out.println("Titular de la cuenta: " + cuenta.getTitular());
        System.out.println("ID de la cuenta: " + cuenta.getIdCuenta());
        System.out.println("Saldo actual: $" + cuenta.getSaldo());
    }
}

// 4. Interfaz y clase para manejar las Notificaciones (DIP y OCP)
interface ServicioNotificacion {
    void enviarNotificacion(CuentaBancaria cuenta, String mensaje);
}

class ServicioNotificacionEmail implements ServicioNotificacion {
    @Override
    public void enviarNotificacion(CuentaBancaria cuenta, String mensaje) {
        System.out.println("Enviando correo a " + cuenta.getTitular() + ": " + mensaje);
    }
}
//6.Nueva Funcionalidad siguiendo principio SOLID
//Agregar nuevos métodos de notificación (Principio Abierto/Cerrado - OCP)
class ServicioNotificacionSMS implements ServicioNotificacion {
    @Override
    public void enviarNotificacion(CuentaBancaria cuenta, String mensaje) {
        System.out.println("Enviando SMS a " + cuenta.getTitular() + ": " + mensaje);
    }
}

// 5. Clase Principal
public class AplicacionBancaria {
    public static void main(String[] args) {
        // Inicialización de la cuenta
        CuentaBancaria cuenta = new CuentaBancaria("Pepe", "12345678", 1000);

        // Uso del servicio de transacciones
        ServicioTransaccion servicioTransaccion = new ServicioTransaccion();
        servicioTransaccion.depositar(cuenta, 500);
        servicioTransaccion.retirar(cuenta, 200);

        // Uso del servicio de impresión
        ImpresoraCuenta impresora = new ImpresoraCuenta();
        impresora.imprimirDetalles(cuenta);

        // Uso de la nueva funcionalidad (SMS)
        ServicioNotificacion notificacionSMS = new ServicioNotificacionSMS();
        notificacionSMS.enviarNotificacion(cuenta, "Su transferencia ha sido procesada.");
    }   
}