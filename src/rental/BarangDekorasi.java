package rental;

public class BarangDekorasi extends Barang {
    public BarangDekorasi(String nama, int stok, double hargaSewa) {
        super(nama, stok, hargaSewa);
    }

    @Override
    public double hitungTotalHarga(int jumlah) {
        double total = super.hitungTotalHarga(jumlah);
        return total + (0.1 * total); // Pajak/biaya tambahan 10%
    }
}


