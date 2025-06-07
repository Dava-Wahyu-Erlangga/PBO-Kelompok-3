package rental;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

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

        model = new DefaultTableModel(new String[] { "ID", "User", "Barang", "Durasi", "Total Harga", "Status" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // Semua kolom read-only agar konsisten
            }
        };
        table = new JTable(model);
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

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(45, 45, 65));
        table.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);

        JButton konfirmasiBtn = new JButton("Konfirmasi Pesanan");
        konfirmasiBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        konfirmasiBtn.setPreferredSize(new Dimension(200, 40));
        konfirmasiBtn.setBackground(new Color(0, 255, 136));
        konfirmasiBtn.setForeground(Color.BLACK);
        konfirmasiBtn.setFocusPainted(false);
        konfirmasiBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        konfirmasiBtn.addActionListener(_ -> konfirmasiPesanan());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(new Color(30, 30, 47));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        btnPanel.add(konfirmasiBtn);

        add(btnPanel, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = KoneksiDatabase.getConnection();
                Statement stmt = conn.createStatement()) {
            String query = "SELECT p.idPesanan, u.username, b.namaBarang, p.durasiSewa, p.totalHarga, p.status " +
                    "FROM pesanan p JOIN user u ON p.idUser = u.idUser " +
                    "JOIN barang b ON p.idBarang = b.idBarang";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("idPesanan"),
                        rs.getString("username"),
                        rs.getString("namaBarang"),
                        rs.getInt("durasiSewa"),
                        rs.getDouble("totalHarga"),
                        rs.getString("status")
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
            String select = "SELECT idBarang, jumlahUnit FROM pesanan WHERE idPesanan = ?";
            try (PreparedStatement stmt = conn.prepareStatement(select)) {
                stmt.setInt(1, idPesanan);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idBarang = rs.getInt("idBarang");
                    jumlahUnit = rs.getInt("jumlahUnit");
                }
            }

            String updateStok = "UPDATE barang SET stok = stok - ? WHERE idBarang = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateStok)) {
                stmt.setInt(1, jumlahUnit);
                stmt.setInt(2, idBarang);
                stmt.executeUpdate();
            }

            // Update status pesanan
            String updateStatus = "UPDATE pesanan SET status = 'Lunas' WHERE idPesanan = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateStatus)) {
                stmt.setInt(1, idPesanan);
                stmt.executeUpdate();
            }

            // Update status pembayaran
            String updatePembayaran = "UPDATE pembayaran SET status = 'Lunas' WHERE idPesanan = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updatePembayaran)) {
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
