package rental;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BarangPanel extends JPanel {
    private JPanel cardsPanel;
    private SewaListener sewaListener;

    // Konstruktor BarangPanel, menerima listener untuk aksi sewa
    public BarangPanel(SewaListener sewaListener) {
        this.sewaListener = sewaListener;
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 65));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === Judul Panel ===
        JLabel title = new JLabel("Daftar Barang Tersedia");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(0, 255, 136));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // === Panel untuk menampung kartu barang ===
        cardsPanel = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 kolom
        cardsPanel.setBackground(new Color(45, 45, 65));
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(scrollPane, BorderLayout.CENTER);

        // Muat data barang dari database
        loadData();
    }

    // Method untuk mengambil dan menampilkan data barang dari database
    private void loadData() {
        cardsPanel.removeAll();

        try (Connection conn = KoneksiDatabase.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM barang")) {

            while (rs.next()) {
                // Panel kartu untuk setiap barang
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBackground(new Color(60, 60, 80));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 255, 136), 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));

                // Ambil data barang dari database
                int stok = rs.getInt("stok");
                int idBarang = rs.getInt("idBarang");
                String namaBarang = rs.getString("namaBarang");
                double hargaSewa = rs.getDouble("hargaSewa");

                // Ambil dan tampilkan gambar barang
                String gambarPath = rs.getString("gambarBarang");
                if (gambarPath != null && !gambarPath.isEmpty()) {
                    // Cari gambar di folder rental
                    java.net.URL imgURL = getClass().getResource("/rental/" + gambarPath);
                    ImageIcon icon;
                    if (imgURL != null) {
                        icon = new ImageIcon(imgURL);
                        Image img = icon.getImage().getScaledInstance(120, 90, Image.SCALE_SMOOTH);
                        JLabel lblGambar = new JLabel(new ImageIcon(img));
                        lblGambar.setAlignmentX(Component.CENTER_ALIGNMENT);
                        card.add(lblGambar);
                    } else {
                        // Jika gambar tidak ditemukan
                        JLabel lblGambar = new JLabel("Gambar tidak ditemukan");
                        lblGambar.setForeground(Color.RED);
                        lblGambar.setAlignmentX(Component.CENTER_ALIGNMENT);
                        card.add(lblGambar);
                    }
                    card.add(Box.createVerticalStrut(8));
                } else {
                    // Jika tidak ada gambar
                    JLabel lblGambar = new JLabel("Tidak ada gambar");
                    lblGambar.setForeground(Color.GRAY);
                    lblGambar.setAlignmentX(Component.CENTER_ALIGNMENT);
                    card.add(lblGambar);
                    card.add(Box.createVerticalStrut(8));
                }

                // Tampilkan nama barang
                JLabel lblNama = new JLabel(rs.getString("namaBarang"));
                lblNama.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lblNama.setForeground(new Color(0, 255, 136));
                lblNama.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(lblNama);

                card.add(Box.createVerticalStrut(8));

                // Tampilkan kategori barang
                JLabel lblKategori = new JLabel("Kategori: " + rs.getString("kategori"));
                lblKategori.setForeground(Color.WHITE);
                lblKategori.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(lblKategori);

                // Tampilkan harga sewa
                JLabel lblHarga = new JLabel("Rp " + rs.getDouble("hargaSewa"));
                lblHarga.setForeground(Color.WHITE);
                lblHarga.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(lblHarga);

                // Tampilkan stok barang
                JLabel lblStok = new JLabel("Stok: " + stok);
                lblStok.setForeground(Color.WHITE);
                lblStok.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(lblStok);

                // Tambahkan tombol Sewa
                JButton btnSewa = new JButton("Sewa");
                btnSewa.setAlignmentX(Component.CENTER_ALIGNMENT);
                btnSewa.setBackground(new Color(0, 255, 136));
                btnSewa.setForeground(Color.BLACK);

                btnSewa.setMaximumSize(new Dimension(80, 28));
                btnSewa.setPreferredSize(new Dimension(80, 28));
                btnSewa.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                btnSewa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                // Disable tombol jika stok habis
                if (stok <= 0) {
                    btnSewa.setEnabled(false);
                    btnSewa.setText("Tidak Tersedia");
                }

                // Event klik tombol Sewa, kirim data ke listener
                btnSewa.addActionListener(e -> {
                    if (sewaListener != null) {
                        sewaListener.onSewa(namaBarang, idBarang, hargaSewa, stok);
                    }
                });

                card.add(Box.createVerticalStrut(10));
                card.add(btnSewa);

                // Tambahkan kartu ke panel utama
                cardsPanel.add(card);
            }

            // Setelah semua kartu barang ditambahkan, update tampilan panel:
            cardsPanel.revalidate(); // Memastikan layout panel diperbarui setelah perubahan isi
            cardsPanel.repaint(); // Meminta panel untuk menggambar ulang agar perubahan langsung terlihat

        } catch (SQLException ex) {
            // Jika terjadi error saat mengambil data dari database, tampilkan pesan error
            // ke user
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
