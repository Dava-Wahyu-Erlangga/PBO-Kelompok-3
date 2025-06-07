package rental;

public class Pesanan {
    private Barang barang;
    private int jumlah;

    public Pesanan(Barang barang, int jumlah) {
        this.barang = barang;
        this.jumlah = jumlah;
    }

    public boolean konfirmasiPesanan() {
        if (barang.cekKetersediaan(jumlah)) {
            barang.setStok(barang.getStok() - jumlah);
            return true;
        }
        return false;
    }

    public double getTotalHarga() {
        return barang.hitungTotalHarga(jumlah);
    }

    public String getRincianPesanan() {
        return "Barang: " + barang.getNama() +
               "\nJumlah: " + jumlah +
               "\nTotal Harga: Rp " + getTotalHarga();
    }
}
