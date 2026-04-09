# Informe de Técnicas de Patrones de Diseño

Este informe detalla las técnicas utilizadas para cada uno de los tres tipos de patrones de diseño (Creacional, Estructural y de Comportamiento) implementados en el proyecto.

---

## 1. Patrón Creacional: **Singleton** (Técnica: Torre de Control)

### ¿Qué es?
El patrón Singleton garantiza que una clase tenga una **única instancia** y proporciona un punto de acceso global a ella. En nuestro ejemplo, la clase `TorreControl` solo puede existir una vez por aeropuerto para evitar conflictos de mando.

### ¿Cómo se utiliza?
Se logra haciendo que el constructor de la clase sea **privado**, de modo que no se pueda usar `new` desde fuera. Se define un método estático (normalmente `getInstancia()`) que crea la instancia la primera vez que se llama y la devuelve en las llamadas subsiguientes.

### ¿Qué ventajas tiene?
- **Control de acceso a recursos compartidos:** Evita inconsistencias al tener múltiples instancias tratando de gestionar el mismo recurso.
- **Ahorro de memoria:** No se crean múltiples objetos idénticos innecesariamente.
- **Acceso global:** Facilita el acceso a la instancia desde cualquier parte del programa sin pasarla como parámetro.

---

## 2. Patrón Estructural: **Decorator** (Técnica: Ticket Extras)

### ¿Qué es?
El patrón Decorator permite añadir funcionalidades a un objeto de forma **dinámica** envolviéndolo en otros objetos. Es una alternativa flexible a la herencia para extender funcionalidades.

### ¿Cómo se utiliza?
Se crea una interfaz común (`Pasaje`) y una clase concreta (`PasajeBasico`). Luego, una clase base decoradora implementa la misma interfaz y contiene una referencia al objeto original. Los decoradores concretos (`ComidaPremiumDecorator`, `EquipajeExtraDecorator`) heredan de esta base y añaden su lógica propia (precio, descripción) antes o después de llamar al objeto envuelto.

### ¿Qué ventajas tiene?
- **Principio de Responsabilidad Única:** No sobrecarga la clase base con todas las combinaciones posibles de características.
- **Flexibilidad en tiempo de ejecución:** Se pueden combinar múltiples "extras" simplemente envolviendo unos sobre otros.
- **Cumple el principio Open/Closed:** Podemos añadir nuevos decoradores sin modificar la clase original.

---

## 3. Patrón de Comportamiento: **Observer** (Técnica: Alertas de Vuelo)

### ¿Qué es?
El patrón Observer define una dependencia de uno a muchos entre objetos, de forma que cuando el objeto "Sujeto" cambia su estado, todos sus "Observadores" son **notificados automáticamente**.

### ¿Cómo se utiliza?
El Sujeto (`Vuelo`) mantiene una lista de observadores (`PasajeroApp`). Proporciona métodos para registrarse o darse de baja. Cuando ocurre un evento importante (ej. `setEstado`), el sujeto recorre su lista de observadores y llama a un método de actualización en cada uno de ellos.

### ¿Qué ventajas tiene?
- **Acoplamiento mínimo:** El Sujeto no conoce los detalles de los Observadores, solo sabe que implementan una interfaz común.
- **Soporte de comunicación broadcast:** Permite enviar actualizaciones a un número dinámico de suscriptores sin modificar el emisor.
- **Flexibilidad:** Se pueden añadir o quitar observadores en cualquier momento durante la ejecución del programa.
