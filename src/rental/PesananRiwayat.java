package rental;

public class PesananRiwayat {
    private int idPesanan;
    private String namaBarang;
    private String tanggalPesan;
    private String status;
    private double totalHarga;
    private int jumlahUnit;
    private int durasiSewa;
    private String username;

    // Konstruktor untuk mengisi semua atribut saat objek dibuat
    public PesananRiwayat(int idPesanan, String namaBarang, String tanggalPesan, String status, double totalHarga, int jumlahUnit, int durasiSewa, String username) {
        this.idPesanan = idPesanan;
        this.namaBarang = namaBarang;
        this.tanggalPesan = tanggalPesan;
        this.status = status;
        this.totalHarga = totalHarga;
        this.jumlahUnit = jumlahUnit;
        this.durasiSewa = durasiSewa;
        this.username = username;
    }

    // Getter untuk mengambil nilai setiap atribut (digunakan saat menampilkan di tabel)
    public int getIdPesanan() { return idPesanan; }
    public String getNamaBarang() { return namaBarang; }
    public String getTanggalPesan() { return tanggalPesan; }
    public String getStatus() { return status; }
    public double getTotalHarga() { return totalHarga; }
    public int getJumlahUnit() { return jumlahUnit; }
    public int getDurasiSewa() { return durasiSewa; }
    public String getUsername() { return username; }
}
