package practico1.tp2;

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
