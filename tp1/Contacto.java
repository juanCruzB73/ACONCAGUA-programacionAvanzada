package practico1.tp1;

public class Contacto {
    private final String nombre;
    private final String apellido;
    private final String dni;
    private final String pasaporte;
    private final String telefono;
    private final String codigoPostal;
    private final String domicilio;

    public Contacto(String nombre, String apellido, String dni, String pasaporte, String telefono, String codigoPostal, String domicilio) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.pasaporte = pasaporte;
        this.telefono = telefono;
        this.codigoPostal = codigoPostal;
        this.domicilio = domicilio;
    }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDni() { return dni; }
    public String getPasaporte() { return pasaporte; }
    public String getTelefono() { return telefono; }
    public String getCodigoPostal() { return codigoPostal; }
    public String getDomicilio() { return domicilio; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Nombre: ").append(nombre).append("\n");
        sb.append("Apellido: ").append(apellido).append("\n");
        if (dni != null && !dni.trim().isEmpty()) {
            sb.append("DNI: ").append(dni).append("\n");
        }
        if (pasaporte != null && !pasaporte.trim().isEmpty()) {
            sb.append("Pasaporte: ").append(pasaporte).append("\n");
        }
        sb.append("Teléfono: ").append(telefono).append("\n");
        sb.append("Código Postal: ").append(codigoPostal).append("\n");
        sb.append("Domicilio: ").append(domicilio);
        return sb.toString();
    }
}
