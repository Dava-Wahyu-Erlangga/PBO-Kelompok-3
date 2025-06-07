package rental;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

// Representasi data Pesanan (Encapsulation)
class Pesanan {
    private int id;
    private String username;
    private String namaPemilikRek;
    private String metodePembayaran;
    private String tglPembayaran;
    private String namaBarang;
    private int durasiSewa;
    private int jumlahUnit;
    private double totalHarga;
    private String status;

    // Constructor
    public Pesanan(int id, String username, String namaPemilikRek, String metodePembayaran, String tglPembayaran,
                   String namaBarang, int durasiSewa, int jumlahUnit, double totalHarga, String status) {
        this.id = id;
        this.username = username;
        this.namaPemilikRek = namaPemilikRek;
        this.metodePembayaran = metodePembayaran;
        this.tglPembayaran = tglPembayaran;
        this.namaBarang = namaBarang;
        this.durasiSewa = durasiSewa;
        this.jumlahUnit = jumlahUnit;
        this.totalHarga = totalHarga;
        this.status = status;
    }

    // Getter
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getNamaPemilikRek() { return namaPemilikRek; }
    public String getMetodePembayaran() { return metodePembayaran; }
    public String getTglPembayaran() { return tglPembayaran; }
    public String getNamaBarang() { return namaBarang; }
    public int getDurasiSewa() { return durasiSewa; }
    public int getJumlahUnit() { return jumlahUnit; }
    public double getTotalHarga() { return totalHarga; }
    public String getStatus() { return status; }
}


public class KonfirmasiPesananPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;

    public KonfirmasiPesananPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 65));

        JLabel title = new JLabel("Konfirmasi Pesanan", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 255, 136));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
            "ID", "username", "Pemilik Rekening", "Metode Pembayaran",
            "Tanggal Pembayaran", "Barang", "Durasi", "Jumlah Unit", "Total Harga", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // Polymorphism: override untuk proteksi edit
            }
        };

        table = new JTable(model);
        setupTableUI();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(45, 45, 65));
        table.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);

        JButton konfirmasiBtn = new JButton("Konfirmasi Pesanan");
        konfirmasiBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        konfirmasiBtn.setPreferredSize(new Dimension(200, 40));
        konfirmasiBtn.setBackground(new Color(0, 255, 136));
        konfirmasiBtn.setForeground(Color.BLACK);
        konfirmasiBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        konfirmasiBtn.addActionListener(_ -> konfirmasiPesanan());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(new Color(30, 30, 47));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        btnPanel.add(konfirmasiBtn);
        add(btnPanel, BorderLayout.SOUTH);

        loadData();
    }

    private void setupTableUI() {
        table.setRowHeight(70);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(new Color(45, 45, 65));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0, 255, 136, 100));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 80));
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(0, 255, 136));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);
    }

    private void loadData() {
    model.setRowCount(0);
    try (Connection conn = KoneksiDatabase.getConnection();
         Statement stmt = conn.createStatement()) {

        String query = "SELECT p.idPesanan, u.username, b.namaBarang, p.durasiSewa, p.jumlahUnit, p.totalHarga, p.status, " +
                       "py.metodePembayaran, py.namaPemilikRek, py.tglPembayaran " +
                       "FROM pesanan p " +
                       "JOIN user u ON p.idUser = u.idUser " +
                       "JOIN barang b ON p.idBarang = b.idBarang " +
                       "LEFT JOIN pembayaran py ON p.idPesanan = py.idPesanan";

        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            Pesanan pesanan = new Pesanan(
                rs.getInt("idPesanan"),
                rs.getString("username"),
                rs.getString("namaPemilikRek"),
                rs.getString("metodePembayaran"),
                rs.getString("tglPembayaran"),
                rs.getString("namaBarang"),
                rs.getInt("durasiSewa"),
                rs.getInt("jumlahUnit"),
                rs.getDouble("totalHarga"),
                rs.getString("status")
            );

            model.addRow(new Object[]{
                pesanan.getId(),
                pesanan.getUsername(),
                pesanan.getNamaPemilikRek(),
                pesanan.getMetodePembayaran(),
                pesanan.getTglPembayaran(),
                pesanan.getNamaBarang(),
                pesanan.getDurasiSewa() + " hari",
                pesanan.getJumlahUnit(),
                "Rp. " + String.format("%,.0f", pesanan.getTotalHarga()).replace(",", "."),
                pesanan.getStatus()
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void konfirmasiPesanan() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pesanan yang ingin dikonfirmasi.");
            return;
        }

        int idPesanan = (int) model.getValueAt(selectedRow, 0);

        try (Connection conn = KoneksiDatabase.getConnection()) {
            conn.setAutoCommit(false);

            int idBarang = 0, jumlahUnit = 0;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT idBarang, jumlahUnit FROM pesanan WHERE idPesanan = ?")) {
                stmt.setInt(1, idPesanan);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idBarang = rs.getInt("idBarang");
                    jumlahUnit = rs.getInt("jumlahUnit");
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement("UPDATE barang SET stok = stok - ? WHERE idBarang = ?")) {
                stmt.setInt(1, jumlahUnit);
                stmt.setInt(2, idBarang);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("UPDATE pesanan SET status = 'Lunas' WHERE idPesanan = ?")) {
                stmt.setInt(1, idPesanan);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement("UPDATE pembayaran SET status = 'Lunas' WHERE idPesanan = ?")) {
                stmt.setInt(1, idPesanan);
                stmt.executeUpdate();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Pesanan berhasil dikonfirmasi.");
            loadData();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengonfirmasi pesanan.");
        }
    }
}
