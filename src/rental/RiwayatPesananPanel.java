package rental;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.JTableHeader; // <-- Tambahkan baris ini

public class RiwayatPesananPanel extends JPanel {
    public RiwayatPesananPanel(int idUser) {
        // Set layout dan warna background panel utama
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 65));

        // Judul panel
        JLabel label = new JLabel("Riwayat Pesanan");
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(new Color(0, 255, 136));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(label, BorderLayout.NORTH);

        // Ambil data pesanan dari database untuk user ini
        ArrayList<PesananRiwayat> daftarPesanan = getPesananFromDB(idUser);

        // Definisikan nama kolom tabel
        String[] kolom = { "ID Pesanan", "Nama Barang", "Tanggal Pesan", "Status", "Total Harga", "Jumlah Unit",
                "Durasi Sewa", "Aksi" };
        // Siapkan data untuk tabel
        Object[][] data = new Object[daftarPesanan.size()][kolom.length];
        for (int i = 0; i < daftarPesanan.size(); i++) {
            PesananRiwayat p = daftarPesanan.get(i);
            data[i][0] = p.getIdPesanan();
            data[i][1] = p.getNamaBarang();
            data[i][2] = p.getTanggalPesan();
            String status = p.getStatus();
            data[i][3] = status;
            data[i][4] = "Rp " + String.format("%,.0f", p.getTotalHarga());
            data[i][5] = p.getJumlahUnit();
            data[i][6] = p.getDurasiSewa() + " hari";
            // Jika status Lunas, tombol aktif, jika tidak, tombol nonaktif
            data[i][7] = status.equalsIgnoreCase("Lunas") ? "Cetak Struk" : "Tidak Tersedia";
        }

        // Buat tabel untuk menampilkan data pesanan
        JTable table = new JTable(data, kolom) {
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Hanya kolom aksi yang bisa diklik
            }
        };
        table.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), daftarPesanan));

        // Atur tampilan tabel agar lebih nyaman dibaca
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Styling header kolom agar senada dengan card dan font putih
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setBackground(new Color(45, 45, 65)); // warna card
        header.setForeground(Color.WHITE); // font putih
        header.setReorderingAllowed(false); // opsional

        table.setBackground(new Color(45, 45, 65));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0, 255, 136));
        table.setSelectionForeground(Color.BLACK);

        // Bungkus tabel dengan JScrollPane agar bisa discroll jika data banyak
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(45, 45, 65));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Tambahkan scrollPane (yang berisi tabel) ke panel utama
        add(scrollPane, BorderLayout.CENTER);

        System.out.println("idUser di RiwayatPesananPanel: " + idUser);
    }

    // Method untuk mengambil data pesanan dari database berdasarkan idUser
    private ArrayList<PesananRiwayat> getPesananFromDB(int idUser) {
        ArrayList<PesananRiwayat> list = new ArrayList<>();
        try (Connection conn = KoneksiDatabase.getConnection()) {
            // Query join ke tabel barang dan pembayaran untuk mendapatkan info lengkap
            String sql = "SELECT p.idPesanan, b.namaBarang, pb.tglPembayaran, p.status, p.totalHarga, p.jumlahUnit, p.durasiSewa, u.username "
                    +
                    "FROM pesanan p " +
                    "JOIN barang b ON p.idBarang = b.idBarang " +
                    "JOIN user u ON p.idUser = u.idUser " +
                    "LEFT JOIN pembayaran pb ON p.idPesanan = pb.idPesanan " +
                    "WHERE p.idUser = ? " +
                    "ORDER BY p.idPesanan DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idUser);
            ResultSet rs = ps.executeQuery();
            // Proses setiap baris hasil query menjadi objek PesananRiwayat
            while (rs.next()) {
                list.add(new PesananRiwayat(
                        rs.getInt("idPesanan"),
                        rs.getString("namaBarang"),
                        rs.getString("tglPembayaran") != null ? rs.getString("tglPembayaran") : "-",
                        rs.getString("status"),
                        rs.getDouble("totalHarga"),
                        rs.getInt("jumlahUnit"),
                        rs.getInt("durasiSewa"),
                        rs.getString("username") // <-- gunakan username
                ));
            }
        } catch (Exception e) {
            // Tampilkan pesan error jika gagal mengambil data
            JOptionPane.showMessageDialog(this, "Gagal mengambil data pesanan: " + e.getMessage());
        }
        return list;
    }

    // Renderer untuk tombol di tabel
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText((value == null) ? "" : value.toString());
            // Jika tombol tidak aktif, tampilkan abu-abu
            if ("Tidak Tersedia".equals(value)) {
                setBackground(Color.LIGHT_GRAY);
                setForeground(Color.DARK_GRAY);
                setEnabled(false);
            } else {
                setBackground(new Color(0, 255, 136));
                setForeground(Color.BLACK);
                setEnabled(true);
            }
            return this;
        }
    }

    // Editor untuk aksi tombol di tabel
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private PesananRiwayat pesanan;
        private ArrayList<PesananRiwayat> daftarPesanan;
        private String label;

        public ButtonEditor(JCheckBox checkBox, ArrayList<PesananRiwayat> daftarPesanan) {
            super(checkBox);
            this.daftarPesanan = daftarPesanan;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            pesanan = daftarPesanan.get(row);
            // Jika status belum lunas, disable tombol
            if (!"Cetak Struk".equals(label)) {
                button.setEnabled(false);
                button.setBackground(Color.LIGHT_GRAY);
                button.setForeground(Color.DARK_GRAY);
            } else {
                button.setEnabled(true);
                button.setBackground(new Color(0, 255, 136));
                button.setForeground(Color.BLACK);
            }
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked && "Cetak Struk".equals(label)) {
                showStrukDialog(pesanan);
            }
            clicked = false;
            return label;
        }

        private void showStrukDialog(PesananRiwayat p) {
            JPanel panel = new JPanel();
            panel.setBackground(new Color(45, 45, 65));
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 255, 136), 2, true),
                    BorderFactory.createEmptyBorder(20, 30, 20, 30)));

            JLabel title = new JLabel("Sewa Barang VenueVibe");
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(new Color(0, 255, 136));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(title);
            panel.add(Box.createVerticalStrut(18));

            // Panel isi struk dengan GridBagLayout agar label dan nilai rata
            JPanel isiPanel = new JPanel(new GridBagLayout());
            isiPanel.setBackground(new Color(45, 45, 65));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 0, 3, 0);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0;
            gbc.gridy = 0;

            Font isiFont = new Font("Segoe UI", Font.PLAIN, 15);
            Color labelColor = new Color(0, 255, 136);
            Color valueColor = Color.WHITE;

            // Helper untuk tambah baris
            addStrukRow(isiPanel, gbc, "Nama Pembeli", p.getUsername(), isiFont, labelColor, valueColor);
            addStrukRow(isiPanel, gbc, "ID Pesanan", String.valueOf(p.getIdPesanan()), isiFont, labelColor, valueColor);
            addStrukRow(isiPanel, gbc, "Nama Barang", p.getNamaBarang(), isiFont, labelColor, valueColor);
            addStrukRow(isiPanel, gbc, "Tanggal", p.getTanggalPesan(), isiFont, labelColor, valueColor);
            addStrukRow(isiPanel, gbc, "Status", "Lunas", isiFont, labelColor, valueColor);
            addStrukRow(isiPanel, gbc, "Jumlah", String.valueOf(p.getJumlahUnit()), isiFont, labelColor, valueColor);
            addStrukRow(isiPanel, gbc, "Durasi", p.getDurasiSewa() + " hari", isiFont, labelColor, valueColor);
            addStrukRow(isiPanel, gbc, "Total", "Rp " + String.format("%,.0f", p.getTotalHarga()), isiFont, labelColor,
                    valueColor);

            panel.add(isiPanel);

            // Styling tombol OK JOptionPane (global)
            UIManager.put("Button.background", new Color(0, 255, 136));
            UIManager.put("Button.foreground", Color.BLACK);
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
            UIManager.put("OptionPane.background", new Color(45, 45, 65));
            UIManager.put("Panel.background", new Color(45, 45, 65));

            JOptionPane.showMessageDialog(null, panel, "Struk Pembelian", JOptionPane.PLAIN_MESSAGE);

            // Reset UIManager
            UIManager.put("Button.background", null);
            UIManager.put("Button.foreground", null);
            UIManager.put("Button.font", null);
            UIManager.put("OptionPane.background", null);
            UIManager.put("Panel.background", null);
        }

        // Helper method untuk menambah baris struk
        private void addStrukRow(JPanel panel, GridBagConstraints gbc, String label, String value, Font font,
                Color labelColor, Color valueColor) {
            JLabel lLabel = new JLabel(label + " :");
            lLabel.setFont(font);
            lLabel.setForeground(labelColor);
            gbc.gridx = 0;
            gbc.weightx = 0.4;
            panel.add(lLabel, gbc);

            JLabel lValue = new JLabel(value);
            lValue.setFont(font);
            lValue.setForeground(valueColor);
            gbc.gridx = 1;
            gbc.weightx = 0.6;
            panel.add(lValue, gbc);

            gbc.gridy++;
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}
