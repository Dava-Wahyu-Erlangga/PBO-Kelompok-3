package rental;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;

// Kelas entitas Barang dengan enkapsulasi
class BarangAdm {
    private int idBarang;
    private String namaBarang;
    private String kategori;
    private double hargaSewa;
    private int stok;
    private String gambarBarang;

    // Constructor
    public BarangAdm(int idBarang, String namaBarang, String kategori, double hargaSewa, int stok, String gambarBarang) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.kategori = kategori;
        this.hargaSewa = hargaSewa;
        this.stok = stok;
        this.gambarBarang = gambarBarang;
    }

    // Getter 
    public int getId() { return idBarang; }
    public String getNama() { return namaBarang; }
    public String getKategori() { return kategori; }
    public double getHargaSewa() { return hargaSewa; }
    public int getStok() { return stok; }
    public String getGambar() { return gambarBarang; }
}

// Kelas BarangAdmin turunan JPanel, UI tetap sama tapi loadData & edit/hapus pakai objek BarangAdm
public class BarangAdmin extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;

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

        table.getColumn("Aksi").setCellRenderer(new ButtonRenderer());
        table.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(45, 45, 65));
        table.setFillsViewportHeight(true);
        add(scrollPane, BorderLayout.CENTER);

        JButton tambahBtn = new JButton("Tambah Barang Baru");
        tambahBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tambahBtn.setPreferredSize(new Dimension(200, 40));
        tambahBtn.setBackground(new Color(0, 255, 136));
        tambahBtn.setForeground(Color.BLACK);
        tambahBtn.setFocusPainted(false);
        tambahBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 136).darker(), 2));
        tambahBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tambahBtn.addActionListener(e -> tampilkanFormTambah());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(30, 30, 47));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        bottomPanel.add(tambahBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        loadData();
    }

    public JTable getTable() {
        return table;
    }

    // Ambil data dari DB dan buat list objek BarangAdm (enkapsulasi data)
    public List<BarangAdm> getBarangListFromDB() {
        List<BarangAdm> list = new ArrayList<>();
        try (Connection conn = KoneksiDatabase.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM barang")) {

            while (rs.next()) {
                BarangAdm b = new BarangAdm(
                        rs.getInt("idBarang"),
                        rs.getString("namaBarang"),
                        rs.getString("kategori"),
                        rs.getDouble("hargaSewa"),
                        rs.getInt("stok"),
                        rs.getString("gambarBarang")
                );
                list.add(b);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Load data ke table model, gunakan objek BarangAdm
    public void loadData() {
        model.setRowCount(0);
        for (BarangAdm b : getBarangListFromDB()) {
            ImageIcon icon = new ImageIcon(new ImageIcon("src/rental/" + b.getGambar())
                    .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
            model.addRow(new Object[]{
                    b.getId(),
                    b.getNama(),
                    b.getKategori(),
                    "Rp. " + String.format("%,.0f", b.getHargaSewa()).replace(",", "."),
                    b.getStok(),
                    icon,
                    ""
            });
        }
    }

    private void tampilkanFormTambah() {
        tampilkanForm(null);
    }

    // Edit barang pakai objek BarangAdm (polimorfisme lewat overload method tampilkanForm)
    public void editBarang(int id) {
        try (Connection conn = KoneksiDatabase.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM barang WHERE idBarang=?");
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BarangAdm b = new BarangAdm(
                            rs.getInt("idBarang"),
                            rs.getString("namaBarang"),
                            rs.getString("kategori"),
                            rs.getDouble("hargaSewa"),
                            rs.getInt("stok"),
                            rs.getString("gambarBarang")
                    );
                    tampilkanForm(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal mengambil data untuk edit.");
        }
    }

    // Overloading method tampilkanForm untuk tambah (null) dan edit (BarangAdm)
    private void tampilkanForm(BarangAdm barang) {
        boolean isEdit = barang != null;

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
            nama.setText(barang.getNama());
            kategori.setText(barang.getKategori());
            harga.setText(String.valueOf(barang.getHargaSewa()));
            stok.setText(String.valueOf(barang.getStok()));
            gambar.setText(barang.getGambar());
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
        browseBtn.addActionListener(e -> {
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

        okBtn.addActionListener(e -> {
            try (Connection conn = KoneksiDatabase.getConnection()) {
                if (isEdit) {
                    String update = "UPDATE barang SET namaBarang=?, kategori=?, hargaSewa=?, stok=?, gambarBarang=? WHERE idBarang=?";
                    PreparedStatement stmt = conn.prepareStatement(update);
                    stmt.setString(1, nama.getText());
                    stmt.setString(2, kategori.getText());
                    stmt.setDouble(3, Double.parseDouble(harga.getText()));
                    stmt.setInt(4, Integer.parseInt(stok.getText()));
                    stmt.setString(5, gambar.getText());
                    stmt.setInt(6, barang.getId());
                    stmt.executeUpdate();
                } else {
                    String insert = "INSERT INTO barang(namaBarang, kategori, hargaSewa, stok, gambarBarang) VALUES(?,?,?,?,?)";
                    PreparedStatement stmt = conn.prepareStatement(insert);
                    stmt.setString(1, nama.getText());
                    stmt.setString(2, kategori.getText());
                    stmt.setDouble(3, Double.parseDouble(harga.getText()));
                    stmt.setInt(4, Integer.parseInt(stok.getText()));
                    stmt.setString(5, gambar.getText());
                    stmt.executeUpdate();
                }
                loadData();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan data: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    // Hapus barang
    public void hapusBarang(int idBarang) {
        int konfirmasi = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus barang ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            try (Connection conn = KoneksiDatabase.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM barang WHERE idBarang=?");
                stmt.setInt(1, idBarang);
                stmt.executeUpdate();
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            }
        }
    }
}

// Renderer dan editor untuk tombol Edit dan Hapus pada kolom Aksi
class ButtonRenderer extends JPanel implements TableCellRenderer {
    private final JButton editBtn = new JButton("Edit");
    private final JButton hapusBtn = new JButton("Hapus");

    public ButtonRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        editBtn.setBackground(new Color(0, 255, 136));
        editBtn.setForeground(Color.BLACK);
        editBtn.setFocusPainted(false);
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hapusBtn.setBackground(new Color(255, 50, 50));
        hapusBtn.setForeground(Color.WHITE);
        hapusBtn.setFocusPainted(false);
        hapusBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        setBackground(new Color(45, 45, 65));
        add(editBtn);
        add(hapusBtn);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JPanel panel;
    protected JButton editBtn;
    protected JButton hapusBtn;
    public BarangAdmin barangAdmin;
    private int selectedRow;

    public ButtonEditor(JCheckBox checkBox, BarangAdmin barangAdmin) {
        super(checkBox);
        this.barangAdmin = barangAdmin;
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        editBtn = new JButton("Edit");
        hapusBtn = new JButton("Hapus");

        editBtn.setBackground(new Color(0, 255, 136));
        editBtn.setForeground(Color.BLACK);
        editBtn.setFocusPainted(false);
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hapusBtn.setBackground(new Color(255, 50, 50));
        hapusBtn.setForeground(Color.WHITE);
        hapusBtn.setFocusPainted(false);
        hapusBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        

        panel.add(editBtn);
        panel.add(hapusBtn);

        editBtn.addActionListener(e -> {
            fireEditingStopped();
            int idBarang = (int) barangAdmin.getTable().getValueAt(selectedRow, 0);
            barangAdmin.editBarang(idBarang);
        });

        hapusBtn.addActionListener(e -> {
            fireEditingStopped();
            int idBarang = (int) barangAdmin.getTable().getValueAt(selectedRow, 0);
            barangAdmin.hapusBarang(idBarang);
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        selectedRow = row;
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }
}
