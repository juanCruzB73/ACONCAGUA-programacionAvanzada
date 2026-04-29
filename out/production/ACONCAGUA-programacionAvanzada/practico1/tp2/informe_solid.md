### Análisis del problema original

El código base violaba principalmente el **Principio de Responsabilidad Única (SRP)**. La clase `CuentaBancaria` estaba haciendo demasiadas cosas:
1. Almacenar los datos de la cuenta (estado).
2. Manejar la lógica de transacciones (depositar, retirar).
3. Formatear y mostrar información por consola (`imprimirDetallesCuenta`).
4. Manejar el envío de notificaciones (`enviarNotificacionPorEmail`).

Si mañana quisiéramos cambiar el formato de impresión, o enviar notificaciones por SMS en lugar de Email, tendríamos que modificar la clase `CuentaBancaria`, lo cual también rompe el **Principio de Abierto/Cerrado (OCP)**.

### Cambios realizados

Se dividió el sistema en múltiples clases más pequeñas y cohesivas:

1.  **`CuentaBancaria`**: Ahora es un simple contenedor de datos (POJO). Solo guarda y devuelve el estado.
2.  **`ServicioTransaccion`**: Se encarga exclusivamente de la lógica de negocio (depositar y retirar dinero).
3.  **`ImpresoraCuenta`**: Se encarga únicamente de la presentación y visualización de datos de la cuenta.
4.  **`ServicioNotificacion` (Interfaz) y `ServicioNotificacionEmail` (Clase)**: Se aplicó también el **DIP** y el **OCP** al crear una interfaz para notificaciones. Ahora, si necesitamos notificar por SMS, simplemente creamos `ServicioNotificacionSMS` sin modificar el código existente.

### Código Refactorizado

### Repositorio github:
https://github.com/juanCruzB73/ACONCAGUA-programacionAvanzada/tree/main/tp2

```java
// 1. Clase solo para mantener el Estado (Datos)
public class CuentaBancaria {
    private String titular;
    private String idCuenta;
    private double saldo;

    public CuentaBancaria(String titular, String idCuenta, double saldo) {
        this.titular = titular;
        this.idCuenta = idCuenta;
        this.saldo = saldo;
    }

    public String getTitular() { return titular; }
    public String getIdCuenta() { return idCuenta; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}

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

// 6. Nueva Funcionalidad siguiendo principio SOLID
// Agregar nuevos métodos de notificación (Principio Abierto/Cerrado - OCP)
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

        // Uso del servicio de notificación (Inversión de dependencias)
        ServicioNotificacion notificacionEmail = new ServicioNotificacionEmail();
        notificacionEmail.enviarNotificacion(cuenta, "Notificación exitosa!");
        
        // Uso de la nueva funcionalidad (SMS)
        ServicioNotificacion notificacionSMS = new ServicioNotificacionSMS();
        notificacionSMS.enviarNotificacion(cuenta, "Su transferencia ha sido procesada.");
    }
}
```

### ¿Cómo cumple esto con el Principio Abierto/Cerrado (OCP)?

El principio **OCP (Open/Closed Principle)** establece que las entidades de software deben estar **abiertas para la extensión, pero cerradas para la modificación**.

En este ejemplo añadido, el principio se cumple a la perfección porque:
1. **Abierto para la extensión:** Pudimos agregar un comportamiento completamente nuevo al sistema (el envío de notificaciones por SMS) simplemente creando la nueva clase `ServicioNotificacionSMS`.
2. **Cerrado para la modificación:** No tuvimos que alterar **ni una sola línea** de código de las clases existentes (`CuentaBancaria`, `ServicioNotificacionEmail`, o la interfaz `ServicioNotificacion`) para hacer que esto funcione. 

Al depender de abstracciones (la interfaz `ServicioNotificacion`) en lugar de implementaciones concretas, el sistema permite enchufar nuevas piezas ("plugins" de notificaciones) de forma segura y escalable sin riesgo de romper lo que ya estaba funcionando bien.
