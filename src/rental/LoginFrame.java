package rental;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Login - Rental Properti Event");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBackground(Color.decode("#111111"));

        JLabel titleLabel = new JLabel("🎵 VenueVibe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.decode("#80FF00"));
        titleLabel.setBounds(60, 20, 250, 30);
        leftPanel.add(titleLabel);

        JLabel usernamelabel = new JLabel("Username");
        usernamelabel.setForeground(Color.GREEN);
        usernamelabel.setBounds(60, 80, 100, 20);
        leftPanel.add(usernamelabel);

        emailField = new JTextField();
        emailField.setBounds(60, 100, 250, 30);
        leftPanel.add(emailField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.GREEN);
        passLabel.setBounds(60, 140, 100, 20);
        leftPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(60, 160, 250, 30);
        leftPanel.add(passwordField);

        JLabel forgotPass = new JLabel("Forgot Password?");
        forgotPass.setForeground(Color.LIGHT_GRAY);
        forgotPass.setFont(new Font("Arial", Font.PLAIN, 11));
        forgotPass.setBounds(60, 195, 150, 20);
        leftPanel.add(forgotPass);

        forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordFrame();
            }
        });


        JButton signUpBtn = new JButton("Sign Up");
        signUpBtn.setBounds(60, 220, 120, 35);
        signUpBtn.setBackground(Color.decode("#80FF00"));
        signUpBtn.setForeground(Color.BLACK);
        signUpBtn.setFocusPainted(false);
        leftPanel.add(signUpBtn);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(190, 220, 120, 35);
        loginBtn.setBackground(Color.LIGHT_GRAY);
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setFocusPainted(false);
        leftPanel.add(loginBtn);

        loginBtn.addActionListener(e -> {
        String username = emailField.getText();
        String password = new String(passwordField.getPassword());

        Authenticator auth = new Authenticator();
        try {
            User user = auth.login(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(null, "Login Berhasil sebagai " + user.getRole());

                UserRoleHandler handler;
                if (user.getRole().equalsIgnoreCase("admin")) {
                    handler = new AdminHandler();
                } else {
                    handler = new CustomerHandler();
                }

                handler.openDashboard(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Username atau Password salah!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Koneksi ke database gagal!");
        }
    });


        signUpBtn.addActionListener(e -> {
            new RegisterFrame();
            dispose();
        });

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.decode("#222222"));
        rightPanel.setLayout(new GridBagLayout());

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/rental/logo.png"));
        Image scaledImage = logoIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);
        rightPanel.add(logoLabel);

        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        add(contentPanel);

        setVisible(true);
    }
}
