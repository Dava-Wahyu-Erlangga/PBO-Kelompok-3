package rental;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private JTextField namaField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea alamatArea;
    private JTextField noTelpField;

    public RegisterFrame() {
        setTitle("📝 Daftar Akun - Rental Properti");
        setSize(800, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel Utama - 2 Kolom
        JPanel contentPanel = new JPanel(new GridLayout(1, 2));
        add(contentPanel);

        // ================= Panel Kiri: Logo =================
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.decode("#111111"));
        leftPanel.setLayout(new GridBagLayout());

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/rental/logo.png"));
        Image scaledImage = logoIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        leftPanel.add(logoLabel);

        // ================ Panel Kanan: Form =================
        JPanel rightPanel = new JPanel(null);
        rightPanel.setBackground(Color.decode("#222222"));

        JLabel titleLabel = new JLabel("Daftar Akun Baru");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(140, 20, 200, 30);
        rightPanel.add(titleLabel);

        JLabel namaLabel = new JLabel("Nama Lengkap:");
        namaLabel.setBounds(50, 70, 100, 20);
        styleLabel(namaLabel, rightPanel);

        namaField = new JTextField();
        namaField.setBounds(160, 70, 200, 25);
        rightPanel.add(namaField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 110, 100, 20);
        styleLabel(usernameLabel, rightPanel);

        usernameField = new JTextField();
        usernameField.setBounds(160, 110, 200, 25);
        rightPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 150, 100, 20);
        styleLabel(passwordLabel, rightPanel);

        passwordField = new JPasswordField();
        passwordField.setBounds(160, 150, 200, 25);
        rightPanel.add(passwordField);

        JLabel alamatLabel = new JLabel("Alamat:");
        alamatLabel.setBounds(50, 190, 100, 20);
        styleLabel(alamatLabel, rightPanel);

        alamatArea = new JTextArea();
        alamatArea.setLineWrap(true);
        alamatArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(alamatArea);
        scrollPane.setBounds(160, 190, 200, 50);
        rightPanel.add(scrollPane);

        JLabel telpLabel = new JLabel("No. Telepon:");
        telpLabel.setBounds(50, 250, 100, 20);
        styleLabel(telpLabel, rightPanel);

        noTelpField = new JTextField();
        noTelpField.setBounds(160, 250, 200, 25);
        rightPanel.add(noTelpField);

        JButton daftarBtn = new JButton("Daftar 📝");
        daftarBtn.setBounds(160, 300, 90, 30);
        daftarBtn.setBackground(Color.decode("#46A0D3"));
        daftarBtn.setForeground(Color.WHITE);
        daftarBtn.setFocusPainted(false);
        daftarBtn.addActionListener(e -> registerUser());
        rightPanel.add(daftarBtn);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(270, 300, 90, 30);
        loginBtn.setBackground(Color.LIGHT_GRAY);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });
        rightPanel.add(loginBtn);

        // Gabungkan ke frame
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        setVisible(true);
    }

    private void styleLabel(JLabel label, JPanel panel) {
        label.setForeground(Color.decode("#80FF00"));
        panel.add(label);
    }

    private void registerUser() {
        String nama = namaField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String alamat = alamatArea.getText().trim();
        String noTelp = noTelpField.getText().trim();

        if (nama.isEmpty() || username.isEmpty() || password.isEmpty() || alamat.isEmpty() || noTelp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }

        User newUser = new User(nama, username, password, alamat, noTelp);
        RegisterHandler handler = new RegisterHandler();

        try {
            boolean success = handler.register(newUser);
            if (success) {
                JOptionPane.showMessageDialog(this, "Pendaftaran berhasil! Silakan login.");
                new LoginFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Pendaftaran gagal.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Username sudah digunakan.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Kesalahan koneksi database.");
        }
    }

}

