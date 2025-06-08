package rental;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PembayaranPanel extends BaseFormPanel {
    // Field untuk menyimpan data pesanan dan user
    private String namaBarang;
    private double hargaSewa;
    private int idBarang, idUser, stok, durasi, jumlah;

    // Konstruktor menerima data pesanan dan user
    public PembayaranPanel(String namaBarang, double hargaSewa, int idBarang, int idUser, int stok, int durasi,
            int jumlah) {
        super("Pembayaran");
        this.namaBarang = namaBarang;
        this.hargaSewa = hargaSewa;
        this.idBarang = idBarang;
        this.idUser = idUser;
        this.stok = stok;
        this.durasi = durasi;
        this.jumlah = jumlah;

        // Panel form pembayaran dengan GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(45, 45, 65));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(350, 35);

        // Label dan combo box metode pembayaran
        JLabel lblMetode = new JLabel("Metode Pembayaran:");
        lblMetode.setForeground(Color.WHITE);
        lblMetode.setFont(labelFont);
        String[] metode = { "E-Wallet", "Transfer Bank" };
        JComboBox<String> cbMetode = new JComboBox<>(metode);
        cbMetode.setFont(fieldFont);
        cbMetode.setPreferredSize(fieldSize);

        // Label dan field nomor rekening
        JLabel lblNoRek = new JLabel("Nomor Rekening:");
        lblNoRek.setForeground(Color.WHITE);
        lblNoRek.setFont(labelFont);
        JTextField tfNoRek = new JTextField();
        tfNoRek.setFont(fieldFont);
        tfNoRek.setPreferredSize(fieldSize);

        // Label dan field nama pemilik rekening
        JLabel lblNamaPemilik = new JLabel("Nama Pemilik Rekening:");
        lblNamaPemilik.setForeground(Color.WHITE);
        lblNamaPemilik.setFont(labelFont);
        JTextField tfNamaPemilik = new JTextField();
        tfNamaPemilik.setFont(fieldFont);
        tfNamaPemilik.setPreferredSize(fieldSize);

        // Label dan spinner tanggal pembayaran
        JLabel lblTanggal = new JLabel("Tanggal Pembayaran:");
        lblTanggal.setForeground(Color.WHITE);
        lblTanggal.setFont(labelFont);
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setFont(fieldFont);
        dateSpinner.setPreferredSize(fieldSize);

        // Tombol konfirmasi pembayaran
        JButton btnKonfirmasi = new JButton("Konfirmasi Pembayaran");
        btnKonfirmasi.setBackground(new Color(0, 255, 136));
        btnKonfirmasi.setForeground(Color.BLACK);
        btnKonfirmasi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnKonfirmasi.setPreferredSize(new Dimension(200, 35));

        // Tambahkan komponen ke formPanel dengan GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(lblMetode, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(cbMetode, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        formPanel.add(lblNoRek, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tfNoRek, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        formPanel.add(lblNamaPemilik, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tfNamaPemilik, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        formPanel.add(lblTanggal, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(btnKonfirmasi, gbc);

        // Tambahkan formPanel ke panel utama
        add(formPanel, BorderLayout.CENTER);

        // Aksi tombol konfirmasi pembayaran
        btnKonfirmasi.addActionListener(e -> {
            // Validasi: semua field harus diisi
            if (tfNoRek.getText().trim().isEmpty() ||
                    tfNamaPemilik.getText().trim().isEmpty() ||
                    cbMetode.getSelectedItem() == null ||
                    dateSpinner.getValue() == null) {
                JOptionPane.showMessageDialog(this, "Isi semua form sebelum melanjutkan!", "Gagal",
                        JOptionPane.ERROR_MESSAGE);
                return; // Hentikan proses jika ada yang kosong
            }

            try (Connection conn = KoneksiDatabase.getConnection()) {
                // 1. Simpan pesanan ke database
                String sql = "INSERT INTO pesanan (idUser, idBarang, durasiSewa, totalHarga, status, jumlahUnit) VALUES (?, ?, ?, ?, ?, ?)";
                double total = hargaSewa * durasi * jumlah;
                String status = "Menunggu";
                int idPesananBaru = -1;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idUser);
                    ps.setInt(2, idBarang);
                    ps.setInt(3, durasi);
                    ps.setDouble(4, total);
                    ps.setString(5, status);
                    ps.setInt(6, jumlah);
                    ps.executeUpdate();
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        idPesananBaru = generatedKeys.getInt(1); // Ambil id pesanan yang baru dibuat
                    }
                }
                // 2. Kurangi stok barang di tabel barang
                String updateStok = "UPDATE barang SET stok = stok - ? WHERE idBarang = ?";
                try (PreparedStatement psStok = conn.prepareStatement(updateStok)) {
                    psStok.setInt(1, jumlah);
                    psStok.setInt(2, idBarang);
                    psStok.executeUpdate();
                }
                // 3. Simpan data pembayaran ke tabel pembayaran
                if (idPesananBaru != -1) {
                    String insertPembayaran = "INSERT INTO pembayaran (idPesanan, metodePembayaran, nomorRek, namaPemilikRek, tglPembayaran, status) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement psPembayaran = conn.prepareStatement(insertPembayaran)) {
                        psPembayaran.setInt(1, idPesananBaru);
                        psPembayaran.setString(2, cbMetode.getSelectedItem().toString());
                        psPembayaran.setString(3, tfNoRek.getText());
                        psPembayaran.setString(4, tfNamaPemilik.getText());
                        java.util.Date utilDate = (java.util.Date) dateSpinner.getValue();
                        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                        psPembayaran.setDate(5, sqlDate);
                        psPembayaran.setString(6, "Menunggu");
                        psPembayaran.executeUpdate();
                    }
                }

                // Tampilkan pesan sukses ke user
                JOptionPane.showMessageDialog(this, "Pembayaran sedang dikonfirmasi!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

                // Pindah ke panel riwayat pesanan
                DashboardFrame dashboard = (DashboardFrame) SwingUtilities.getWindowAncestor(this);
                dashboard.showRiwayatPesananPanel();

            } catch (Exception ex) {
                // Tampilkan pesan error jika gagal menyimpan pembayaran
                JOptionPane.showMessageDialog(this, "Gagal menyimpan pembayaran:\n" + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
