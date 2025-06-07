package rental;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;

public class BarangAdmin extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;

    // Konstruktor utama: buat UI tabel dan tombol tambah
    public BarangAdmin() {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 65));

        JLabel title = new JLabel("Kelola Data Barang", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 255, 136));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Nama", "Kategori", "Harga Sewa", "Stok", "Gambar", "Aksi"}, 0) {
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 5 ? ImageIcon.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 6;
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

        // Pasang custom renderer & editor tombol pada kolom "Aksi"
        table.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(45, 45, 65));
        table.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);

        // Panel bawah dengan tombol tambah barang
        JButton tambahBtn = new JButton("Tambah Barang Baru");
        tambahBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tambahBtn.setPreferredSize(new Dimension(200, 40));
        tambahBtn.setBackground(new Color(0, 255, 136));
        tambahBtn.setForeground(Color.BLACK);
        tambahBtn.setFocusPainted(false);
        tambahBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 136).darker(), 2));
        tambahBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tambahBtn.addActionListener(_ -> tampilkanFormTambah());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(30, 30, 47));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        bottomPanel.add(tambahBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        loadData();  // Muat data dari database ke tabel
    }

    public JTable getTable() {
        return table;
    }

    // Load data barang dari database ke model tabel
    public void loadData() {
        model.setRowCount(0);
        try (Connection conn = KoneksiDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM barang")) {

            while (rs.next()) {
                ImageIcon icon = new ImageIcon(new ImageIcon("src/rental/" + rs.getString("gambarBarang"))
                        .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
                model.addRow(new Object[]{
                    rs.getInt("idBarang"),
                    rs.getString("namaBarang"),
                    rs.getString("kategori"),
                    rs.getDouble("hargaSewa"),
                    rs.getInt("stok"),
                    icon,
                    ""
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Tampilkan dialog tambah barang
    private void tampilkanFormTambah() {
        tampilkanForm(null);  // null menandakan ini form tambah baru
    }

    // Tampilkan dialog edit barang dengan data dari ResultSet
    public void editBarang(int id) {
        try (Connection conn = KoneksiDatabase.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM barang WHERE idBarang=?");
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tampilkanForm(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data untuk edit.");
        }
    }

    // Method umum untuk menampilkan form tambah/edit
    // Jika rs null berarti tambah, else edit dengan data di rs
    private void tampilkanForm(ResultSet rs) {
        boolean isEdit = rs != null;

        JDialog dialog = new JDialog((Frame) null, isEdit ? "Edit Barang" : "Tambah Barang", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(45, 45, 65));

        JLabel lblTitle = new JLabel(isEdit ? "Edit Barang" : "Tambah Barang", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 255, 136));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        dialog.add(lblTitle, BorderLayout.NORTH);

        JTextField nama = new JTextField();
        JTextField kategori = new JTextField();
        JTextField harga = new JTextField();
        JTextField stok = new JTextField();
        JTextField gambar = new JTextField();

        if (isEdit) {
            try {
                nama.setText(rs.getString("namaBarang"));
                kategori.setText(rs.getString("kategori"));
                harga.setText(String.valueOf(rs.getDouble("hargaSewa")));
                stok.setText(String.valueOf(rs.getInt("stok")));
                gambar.setText(rs.getString("gambarBarang"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        JTextField[] fields = {nama, kategori, harga, stok, gambar};
        Dimension inputSize = new Dimension(350, 40);
        for (JTextField tf : fields) {
            tf.setPreferredSize(inputSize);
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            tf.setBackground(Color.WHITE);
            tf.setForeground(Color.BLACK);
            tf.setCaretColor(Color.BLACK);
        }

        JButton browseBtn = new JButton("Browse");
        browseBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        browseBtn.setBackground(new Color(0, 255, 136));
        browseBtn.setForeground(Color.BLACK);
        browseBtn.setFocusPainted(false);
        browseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        browseBtn.setPreferredSize(new Dimension(80, 40));
        browseBtn.addActionListener(_ -> {
            JFileChooser chooser = new JFileChooser("src/rental/");
            int result = chooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                gambar.setText(chooser.getSelectedFile().getName());
            }
        });

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(45, 45, 65));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Nama:", "Kategori:", "Harga Sewa:", "Stok:", "Gambar:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            formPanel.add(lbl, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            formPanel.add(fields[i], gbc);

            if (i == 4) {
                gbc.gridx = 2;
                gbc.weightx = 0.0;
                formPanel.add(browseBtn, gbc);
            }
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(45, 45, 65));
        JButton okBtn = new JButton("OK");
        JButton cancelBtn = new JButton("Cancel");

        okBtn.setBackground(new Color(0, 255, 136));
        okBtn.setForeground(Color.BLACK);
        okBtn.setFocusPainted(false);
        okBtn.setPreferredSize(new Dimension(80, 35));
        okBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setPreferredSize(new Dimension(80, 35));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Simpan data tambah atau edit ke DB saat OK ditekan
        okBtn.addActionListener(e -> {
            try (Connection conn = KoneksiDatabase.getConnection()) {
                if (isEdit) {
                    String update = "UPDATE barang SET namaBarang=?, kategori=?, hargaSewa=?, stok=?, tersedia=?, gambarBarang=? WHERE idBarang=?";
                    PreparedStatement updateStmt = conn.prepareStatement(update);
                    updateStmt.setString(1, nama.getText());
                    updateStmt.setString(2, kategori.getText());
                    updateStmt.setDouble(3, Double.parseDouble(harga.getText()));
                    int s = Integer.parseInt(stok.getText());
                    updateStmt.setInt(4, s);
                    updateStmt.setInt(5, s > 0 ? 1 : 0);
                    updateStmt.setString(6, gambar.getText());
                    updateStmt.setInt(7, rs.getInt("idBarang"));
                    updateStmt.executeUpdate();
                } else {
                    String insert = "INSERT INTO barang (namaBarang, kategori, hargaSewa, stok, tersedia, gambarBarang) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insert);
                    insertStmt.setString(1, nama.getText());
                    insertStmt.setString(2, kategori.getText());
                    insertStmt.setDouble(3, Double.parseDouble(harga.getText()));
                    int s = Integer.parseInt(stok.getText());
                    insertStmt.setInt(4, s);
                    insertStmt.setInt(5, s > 0 ? 1 : 0);
                    insertStmt.setString(6, gambar.getText());
                    insertStmt.executeUpdate();
                }
                JOptionPane.showMessageDialog(dialog, "Data berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                dialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // Hapus data barang dari database
   public void hapusBarang(int id) {
    try {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    try (Connection conn = KoneksiDatabase.getConnection()) {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM barang WHERE idBarang=?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
        loadData();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}

// Renderer tombol edit dan hapus di tabel
class ButtonRenderer extends JPanel implements TableCellRenderer {
    private final JButton editButton = new JButton("Edit");
    private final JButton hapusButton = new JButton("Hapus");

    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        setBackground(new Color(45, 45, 65));

        editButton.setBackground(new Color(0, 255, 136));
        editButton.setForeground(Color.BLACK);
        editButton.setFocusPainted(false);
        editButton.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 136).darker()));
        editButton.setPreferredSize(new Dimension(70, 30));
        editButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        hapusButton.setBackground(new Color(0, 255, 136));
        hapusButton.setForeground(Color.BLACK);
        hapusButton.setFocusPainted(false);
        hapusButton.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 136).darker()));
        hapusButton.setPreferredSize(new Dimension(70, 30));
        hapusButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(editButton);
        add(hapusButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? new Color(0, 255, 136, 100) : new Color(45, 45, 65));
        return this;
    }
}

// Editor tombol edit dan hapus di tabel
class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    private final JButton editBtn = new JButton("Edit");
    private final JButton hapusBtn = new JButton("Hapus");
    private JTable table;
    private int currentRow;

    public ButtonEditor(JCheckBox checkBox, BarangAdmin parent) {
        editBtn.setBackground(new Color(0, 255, 136));
        editBtn.setForeground(Color.BLACK);
        editBtn.setFocusPainted(false);
        editBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 136).darker()));
        editBtn.setPreferredSize(new Dimension(70, 30));
        editBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        hapusBtn.setBackground(new Color(0, 255, 136));
        hapusBtn.setForeground(Color.BLACK);
        hapusBtn.setFocusPainted(false);
        hapusBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 136).darker()));
        hapusBtn.setPreferredSize(new Dimension(70, 30));
        hapusBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panel.setBackground(new Color(45, 45, 65));
        panel.add(editBtn);
        panel.add(hapusBtn);

        editBtn.addActionListener(e -> {
            int id = (int) table.getValueAt(currentRow, 0);
            parent.editBarang(id);
            fireEditingStopped();
        });

        hapusBtn.addActionListener(e -> {
            int id = (int) table.getValueAt(currentRow, 0);
            parent.hapusBarang(id);
            fireEditingStopped();
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.table = table;
        this.currentRow = row;
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }
}