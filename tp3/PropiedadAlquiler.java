package practico1.tp3;

/**
 * Clase que representa una propiedad exclusiva para alquiler.
 * Cumple con LSP ya que no posee métodos de venta inoperantes.
 */
public class PropiedadAlquiler extends Propiedad implements Alquilable {
    private double precioAlquiler;
    private boolean alquilada;
    private String inquilino;
    private int mesesContrato;

    public PropiedadAlquiler(int id, String direccion, double superficie, String propietario, double precioAlquiler) {
        super(id, direccion, superficie, propietario);
        this.precioAlquiler = precioAlquiler;
        this.alquilada = false;
        this.inquilino = null;
        this.mesesContrato = 0;
    }

    @Override
    public double getPrecioAlquiler() {
        return precioAlquiler;
    }

    @Override
    public void alquilar(String inquilino, int mesesContrato) {
        if (this.alquilada) {
            throw new IllegalStateException("La propiedad ya se encuentra alquilada a " + this.inquilino);
        }
        this.alquilada = true;
        this.inquilino = inquilino;
        this.mesesContrato = mesesContrato;
    }

    @Override
    public boolean estaAlquilada() {
        return alquilada;
    }

    @Override
    public String getInquilino() {
        return inquilino;
    }

    @Override
    public int getMesesContrato() {
        return mesesContrato;
    }

    @Override
    public String getDetallesGenerales() {
        return super.getDetallesGenerales() + String.format(" | Tipo: Alquiler | Precio Alquiler: $%.2f/mes | Estado: %s", 
                precioAlquiler, alquilada ? "Alquilada a " + inquilino : "Disponible");
    }
}
