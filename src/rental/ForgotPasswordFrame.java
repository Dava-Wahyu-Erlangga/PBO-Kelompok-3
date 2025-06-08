package rental;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ForgotPasswordFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField newPasswordField;

    public ForgotPasswordFrame() {
        setTitle("🔒 Reset Password");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.decode("#222222"));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(40, 40, 100, 25);
        usernameLabel.setForeground(Color.GREEN);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 40, 180, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password Baru:");
        passLabel.setBounds(40, 80, 100, 25);
        passLabel.setForeground(Color.GREEN);
        add(passLabel);

        newPasswordField = new JPasswordField();
        newPasswordField.setBounds(150, 80, 180, 25);
        add(newPasswordField);

        JButton resetBtn = new JButton("Reset Password");
        resetBtn.setBounds(130, 130, 140, 30);
        resetBtn.setBackground(Color.decode("#80FF00"));
        resetBtn.setFocusPainted(false);
        resetBtn.addActionListener(e -> resetPassword());
        add(resetBtn);

        setVisible(true);
    }

    private void resetPassword() {
        String username = usernameField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());

        if (username.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!");
            return;
        }

        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE user SET password = ? WHERE username = ?")) {

            stmt.setString(1, newPassword);
            stmt.setString(2, username);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Password berhasil direset!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Username tidak ditemukan.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengakses database.");
        }
    }
}
