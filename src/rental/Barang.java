package rental;

public class Barang {
    private String nama;
    private int stok;
    private double hargaSewa;

    public Barang(String nama, int stok, double hargaSewa) {
        this.nama = nama;
        this.stok = stok;
        this.hargaSewa = hargaSewa;
    }

    public String getNama() {
        return nama;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public double getHargaSewa() {
        return hargaSewa;
    }

    public double hitungTotalHarga(int jumlah) {
        return hargaSewa * jumlah;
    }

    public boolean cekKetersediaan(int jumlah) {
        return stok >= jumlah;
    }
}
