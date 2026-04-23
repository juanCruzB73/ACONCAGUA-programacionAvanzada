# Práctica de Sockets — Chat con Calculadora

**Programación Avanzada · Universidad ACONCAGUA**

Proyecto de práctica sobre comunicación mediante **Java Sockets (TCP)**. Implementa un esquema cliente-servidor donde ambas partes interactúan a modo de chat, con soporte para resolver expresiones matemáticas en el servidor.

---

## Tabla de contenidos

- [Descripción general](#descripción-general)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Arquitectura y flujo de comunicación](#arquitectura-y-flujo-de-comunicación)
- [Clase Servidor](#clase-servidor)
- [Clase Cliente](#clase-cliente)
- [Protocolo de mensajes](#protocolo-de-mensajes)
- [Evaluador matemático](#evaluador-matemático)
- [Cómo ejecutar](#cómo-ejecutar)
- [Ejemplo de sesión](#ejemplo-de-sesión)

---

## Descripción general

El proyecto consiste en **dos clases independientes**, cada una con su propio `main`:

| Clase | Rol |
|---|---|
| `Servidor.java` | Escucha conexiones en el puerto `5000`, responde mensajes de chat, resuelve ecuaciones y registra todo en un log de consola con timestamp. |
| `Cliente.java` | Se conecta al servidor, permite al usuario escribir mensajes por consola y muestra las respuestas en tiempo real a través de un hilo de recepción dedicado. |

La comunicación se realiza sobre **TCP** usando `ServerSocket` / `Socket` de la librería estándar de Java (`java.net`). El protocolo es de texto plano (líneas terminadas en `\n`), sin dependencias externas.

---

## Estructura del proyecto

```
sockets/
├── src/
│   ├── Servidor.java       ← Clase principal del servidor
│   └── Cliente.java        ← Clase principal del cliente
├── out/
│   ├── Servidor.class
│   ├── Servidor$EvaluadorMatematico.class
│   └── Cliente$HiloReceptor.class
└── README.md               ← Este archivo
```

---

## Arquitectura y flujo de comunicación

```
┌─────────────────────────────────────────────────────┐
│                        SERVIDOR                      │
│                                                      │
│  ServerSocket(5000)                                  │
│       │  accept()                                    │
│       ▼                                              │
│  while(true) ──► manejarCliente(socket)              │
│                        │                             │
│               ┌────────┴────────┐                    │
│               │ BufferedReader  │  ← InputStream     │
│               │ PrintWriter     │  → OutputStream    │
│               └────────┬────────┘                    │
│                        │ readLine()                  │
│                        ▼                             │
│              procesarMensaje()                       │
│                  ├── RESOLVE → evaluarExpresion()    │
│                  ├── EXIT    → break (desconectar)   │
│                  └── otro    → eco del mensaje       │
└─────────────────────────────────────────────────────┘
                          ▲  │
              TCP / 5000  │  │
                          │  ▼
┌─────────────────────────────────────────────────────┐
│                        CLIENTE                       │
│                                                      │
│  Socket("localhost", 5000)                           │
│       │                                              │
│  ┌────┴─────────────────────────────┐                │
│  │ HiloReceptor (Thread daemon)     │                │
│  │  └── readLine() → System.out    │                │
│  └──────────────────────────────────┘                │
│                                                      │
│  main thread:                                        │
│    Scanner(System.in) → salida.println(mensaje)      │
└─────────────────────────────────────────────────────┘
```

---

## Clase Servidor

**Archivo:** `src/Servidor.java`

### Constantes

| Constante | Valor | Descripción |
|---|---|---|
| `PUERTO` | `5000` | Puerto TCP en el que escucha el servidor |
| `FORMATO_HORA` | `HH:mm:ss` | Formato del timestamp en el log |

### Métodos

#### `main(String[] args)`
Punto de entrada del servidor. Crea un `ServerSocket` en el puerto `5000` y entra en un bucle infinito que acepta conexiones de clientes de a **una por vez**. Cada conexión se delega a `manejarCliente()`.

```java
ServerSocket serverSocket = new ServerSocket(PUERTO);
while (true) {
    Socket socketCliente = serverSocket.accept(); // bloquea hasta que llega un cliente
    manejarCliente(socketCliente);
}
```

#### `manejarCliente(Socket socket)`
Gestiona la sesión completa con un cliente. Abre los streams de entrada/salida y:
1. Envía el mensaje de bienvenida con los comandos disponibles.
2. Entra en un bucle `readLine()` que procesa cada mensaje recibido.
3. Registra en log cada mensaje entrante.
4. Rompe el bucle cuando recibe `EXIT`.

#### `procesarMensaje(String mensaje)`
Analiza el texto recibido y retorna la respuesta apropiada:
- Si comienza con `RESOLVE` → extrae la expresión entre comillas y llama a `evaluarExpresion()`.
- Cualquier otro texto → responde con un eco: `[SERVIDOR] Recibido: "..."`.

#### `evaluarExpresion(String expresion)`
Valida que la expresión solo contenga caracteres matemáticos permitidos (`0-9 + - * / % . ( ) espacio`) y la evalúa:
1. **Primario:** intenta usar el motor `JavaScript` de `javax.script` (motor Nashorn incluido en JDK ≤ 14).
2. **Fallback:** si Nashorn no está disponible, usa la clase interna `EvaluadorMatematico`.

#### `log(String mensaje)`
Imprime en `System.out` con formato `[HH:mm:ss] mensaje`. Todos los eventos del servidor pasan por este método.

---

## Clase Cliente

**Archivo:** `src/Cliente.java`

### Constantes

| Constante | Valor | Descripción |
|---|---|---|
| `HOST` | `"localhost"` | Dirección IP/hostname del servidor |
| `PUERTO` | `5000` | Puerto al que conectarse |

### Diseño concurrente

El cliente usa **dos hilos**:

| Hilo | Responsabilidad |
|---|---|
| `main` | Lee la entrada del usuario por `System.in` y la envía al servidor vía `PrintWriter`. |
| `HiloReceptor` (daemon) | Escucha el `InputStream` del socket continuamente e imprime cada línea que llegue del servidor. |

Separar la recepción en un hilo daemon evita que el `readLine()` del servidor bloquee la entrada del usuario.

### Clase interna `HiloReceptor`

```java
static class HiloReceptor implements Runnable {
    private final BufferedReader entrada;
    private volatile boolean activo = true;

    @Override
    public void run() {
        while (activo && (linea = entrada.readLine()) != null) {
            System.out.println("\n[Servidor] " + linea);
            System.out.print("> ");
        }
    }
}
```

El campo `activo` es `volatile` para garantizar visibilidad entre hilos si se llama a `detener()` desde el hilo principal.

---

## Protocolo de mensajes

La comunicación es de texto plano sobre TCP. Cada mensaje es una **línea terminada en `\n`**.

### Comandos del cliente → servidor

| Comando | Formato | Descripción |
|---|---|---|
| `RESOLVE` | `RESOLVE "expresion"` | Pide la resolución de una expresión matemática |
| `EXIT` | `EXIT` | Solicita el cierre de la conexión |
| Chat libre | cualquier texto | El servidor responde con eco |

### Respuestas del servidor → cliente

| Prefijo | Significado |
|---|---|
| `[RESULTADO]` | Resultado de una expresión resuelta |
| `[SERVIDOR]` | Eco de un mensaje de chat |
| `[ERROR]` | Error de formato o evaluación |
| `[INFO]` | Mensajes de estado/bienvenida |

### Log del servidor

Cada evento se registra con timestamp:

```
[16:45:01] >>> Cliente conectado desde: 127.0.0.1
[16:45:10] [RECIBIDO] Hola servidor!
[16:45:10] [ENVIADO]  [SERVIDOR] Recibido: "Hola servidor!"
[16:45:22] [RECIBIDO] RESOLVE "45*23/54+234"
[16:45:22] [ENVIADO]  [RESULTADO] 45*23/54+234 = 253.1666...
[16:45:30] [RECIBIDO] EXIT
[16:45:30] [INFO] Cliente solicitó desconexion.
[16:45:30] <<< Cliente 127.0.0.1 desconectado.
```

---

## Evaluador matemático

El servidor incluye `EvaluadorMatematico`, un **parser recursivo descendente** implementado como clase interna estática. Se usa como fallback cuando el motor Nashorn no está disponible (JDK 15+).

### Gramática soportada

```
expresion  ::= termino  (('+' | '-') termino)*
termino    ::= factor   (('*' | '/' | '%') factor)*
factor     ::= '-' factor
             | '(' expresion ')'
             | numero
numero     ::= [0-9]+ ('.' [0-9]+)?
```

### Operadores soportados

| Operador | Descripción |
|---|---|
| `+` | Suma |
| `-` | Resta / negativo unario |
| `*` | Multiplicación |
| `/` | División (lanza error si divisor = 0) |
| `%` | Módulo |
| `( )` | Agrupación / precedencia |

### Validación de seguridad

Antes de evaluar cualquier expresión se aplica un regex que rechaza todo carácter que no sea:

```
[0-9 + - * / % . ( ) espacio]
```

Esto previene la inyección de código en el motor JavaScript.

---

## Cómo ejecutar

### Prerequisitos

- Java JDK 8 o superior instalado.
- Ambos archivos `.java` disponibles.

### Desde NetBeans

1. **File → New Project → Java Application** (o abrir el proyecto existente).
2. Agregar `Servidor.java` y `Cliente.java` al mismo source package.
3. Ejecutar `Servidor` primero:
   - Click derecho en `Servidor.java` → **Run File**
4. Ejecutar `Cliente` en una segunda instancia:
   - Click derecho en `Cliente.java` → **Run File**

### Desde la terminal (compilación manual)

```bash
# 1. Compilar ambas clases
javac -d out src/Servidor.java src/Cliente.java

# 2. En una terminal: iniciar el servidor
java -cp out Servidor

# 3. En otra terminal: iniciar el cliente
java -cp out Cliente
```

> **Importante:** el servidor debe iniciarse antes que el cliente. Si el cliente intenta conectarse antes de que el servidor esté escuchando, obtendrá un `ConnectException`.

---

## Ejemplo de sesión

### Consola del Servidor

```
[16:00:00] === SERVIDOR SOCKET INICIADO ===
[16:00:00] Escuchando en el puerto 5000...
[16:00:00] Comandos soportados: RESOLVE "expresion" | EXIT
[16:00:00] ================================================
[16:00:00] Esperando conexion de un cliente...
[16:00:05] >>> Cliente conectado desde: 127.0.0.1
[16:00:08] [RECIBIDO] Hola!
[16:00:08] [ENVIADO]  [SERVIDOR] Recibido: "Hola!"
[16:00:15] [RECIBIDO] RESOLVE "45*23/54+234"
[16:00:15] [ENVIADO]  [RESULTADO] 45*23/54+234 = 253.16666666666666
[16:00:20] [RECIBIDO] RESOLVE "(100+50)*2/3"
[16:00:20] [ENVIADO]  [RESULTADO] (100+50)*2/3 = 100.0
[16:00:25] [RECIBIDO] EXIT
[16:00:25] [INFO] Cliente solicitó desconexion.
[16:00:25] <<< Cliente 127.0.0.1 desconectado.
[16:00:25] ================================================
[16:00:25] Esperando conexion de un cliente...
```

### Consola del Cliente

```
=== CLIENTE SOCKET ===
Conectando a localhost:5000...
Conexion establecida!

Escribe un mensaje y presiona ENTER para enviarlo.
Comandos: RESOLVE "expresion"  |  EXIT
----------------------------------------------

[Servidor] Bienvenido al Servidor de Chat/Calculadora!
[Servidor] Comandos disponibles:
[Servidor]   RESOLVE "expresion"  -> resuelve una expresion matematica
[Servidor]   EXIT                  -> cierra la conexion
[Servidor] Cualquier otro mensaje sera respondido como eco.
[Servidor] --------------------------------------------------

> Hola!

[Servidor] [SERVIDOR] Recibido: "Hola!"
> RESOLVE "45*23/54+234"

[Servidor] [RESULTADO] 45*23/54+234 = 253.16666666666666
> RESOLVE "(100+50)*2/3"

[Servidor] [RESULTADO] (100+50)*2/3 = 100.0
> EXIT

[Servidor] Hasta luego! Cerrando conexion...

[Cliente] Conexion cerrada. Hasta luego!
```
