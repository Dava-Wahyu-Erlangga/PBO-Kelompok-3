package rental;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PesanBarangPanel extends BaseFormPanel {
    // Field untuk menyimpan data barang dan user
    private String namaBarang;
    private int idBarang;
    private int idUser;
    private double hargaSewa;
    private int stok;

    // Konstruktor menerima data barang dan user
    public PesanBarangPanel(String namaBarang, double hargaSewa, int idBarang, int idUser, int stok) {
        super("Pesanan");
        this.namaBarang = namaBarang;
        this.idBarang = idBarang;
        this.idUser = idUser;
        this.hargaSewa = hargaSewa;
        this.stok = stok;

        // Panel utama untuk form pemesanan
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(45, 45, 65));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(350, 35);

        // Label nama barang
        JLabel lblNamaBarang = new JLabel("Barang: " + namaBarang);
        lblNamaBarang.setForeground(Color.WHITE);
        lblNamaBarang.setFont(labelFont);

        // Label dan spinner untuk durasi sewa
        JLabel lblDurasi = new JLabel("Durasi (hari):");
        lblDurasi.setForeground(Color.WHITE);
        lblDurasi.setFont(labelFont);
        JSpinner spDurasi = new JSpinner(new SpinnerNumberModel(1, 1, 7, 1));
        spDurasi.setFont(fieldFont);
        ((JSpinner.DefaultEditor) spDurasi.getEditor()).getTextField().setFont(fieldFont);
        spDurasi.setPreferredSize(fieldSize);

        // Label dan spinner untuk jumlah unit
        JLabel lblJumlah = new JLabel("Jumlah Unit:");
        lblJumlah.setForeground(Color.WHITE);
        lblJumlah.setFont(labelFont);
        JSpinner spJumlah = new JSpinner(new SpinnerNumberModel(1, 1, stok, 1));
        spJumlah.setFont(fieldFont);
        ((JSpinner.DefaultEditor) spJumlah.getEditor()).getTextField().setFont(fieldFont);
        spJumlah.setPreferredSize(fieldSize);

        // Label untuk menampilkan total harga
        JLabel lblTotal = new JLabel("Total Harga: Rp 0");
        lblTotal.setForeground(new Color(0, 255, 136));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Tombol konfirmasi pesanan
        JButton btnKonfirmasi = new JButton("Konfirmasi Pesanan");
        btnKonfirmasi.setBackground(new Color(0, 255, 136));
        btnKonfirmasi.setForeground(Color.BLACK);
        btnKonfirmasi.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnKonfirmasi.setPreferredSize(new Dimension(200, 35));

        // Tambahkan komponen ke formPanel dengan GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblNamaBarang, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        formPanel.add(lblDurasi, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(spDurasi, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        formPanel.add(lblJumlah, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(spJumlah, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(lblTotal, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(btnKonfirmasi, gbc);

        // Tambahkan formPanel ke panel utama
        add(formPanel, BorderLayout.CENTER);

        // Listener untuk update total harga saat durasi/jumlah diubah
        ChangeListener updateTotal = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int durasi = (int) spDurasi.getValue();
                int jumlah = (int) spJumlah.getValue();
                double total = hargaSewa * durasi * jumlah;
                lblTotal.setText("Total Harga: Rp " + String.format("%,.0f", total));
            }
        };
        spDurasi.addChangeListener(updateTotal);
        spJumlah.addChangeListener(updateTotal);
        updateTotal.stateChanged(null); // Inisialisasi total harga

        // Event tombol konfirmasi: validasi input dan navigasi ke pembayaran
        btnKonfirmasi.addActionListener(e -> {
            int durasi = (int) spDurasi.getValue();
            int jumlah = (int) spJumlah.getValue();
            try {
                // Validasi durasi dan jumlah
                if (durasi < 1 || durasi > 7) {
                    throw new IllegalArgumentException("Durasi harus antara 1 dan 7 hari.");
                }
                if (jumlah < 1 || jumlah > stok) {
                    throw new IllegalArgumentException(
                            "Jumlah unit harus lebih besar dari 0 dan tidak lebih dari stok.");
                }
                // Kirim data ke PembayaranPanel melalui DashboardFrame
                DashboardFrame dashboard = (DashboardFrame) SwingUtilities.getWindowAncestor(this);
                dashboard.showPembayaranPanel(namaBarang, hargaSewa, idBarang, idUser, stok, durasi, jumlah);
            } catch (IllegalArgumentException ex) {
                // Tampilkan pesan error jika input tidak valid
                JOptionPane.showMessageDialog(PesanBarangPanel.this, ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
