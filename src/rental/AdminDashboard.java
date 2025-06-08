package rental;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class AdminDashboard extends JFrame {
    private final JPanel mainPanel;

    public AdminDashboard() {
        setTitle("Admin Dashboard - VenueVibe");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 20, 30));
        header.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel title = new JLabel("♪ VenueVibe Dashboard", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(0, 255, 136));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(30, 30, 30));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        logoutBtn.addActionListener(e -> System.exit(0));

        header.add(title, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // SIDEBAR
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] menuItems = {
            "Dashboard",
            "Kelola Data Barang",
            "Konfirmasi Pesanan"
        };
        for (String item : menuItems) {
            JButton btn = createSidebarButton(item);
            switch (item) {
                case "Dashboard" -> btn.addActionListener(e -> showCompanyProfile());
                case "Kelola Data Barang" -> btn.addActionListener(e -> showBarangAdmin());
                case "Konfirmasi Pesanan" -> btn.addActionListener(e -> showKonfirmasiPesananPanel());
            }
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(btn);
        }

        sidebar.add(Box.createVerticalGlue());
        add(sidebar, BorderLayout.WEST);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 47));
        add(mainPanel, BorderLayout.CENTER);

        showCompanyProfile();
        setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 45));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(Color.BLACK);
        btn.setForeground(new Color(0, 255, 136));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(0, 255, 136));
                btn.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(Color.BLACK);
                btn.setForeground(new Color(0, 255, 136));
            }
        });

        return btn;
    }

    private void showCompanyProfile() {
        mainPanel.removeAll();

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(45, 45, 65));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 255, 136), 1, true),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel titleLabel = new JLabel("VenueVibe");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 255, 136));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JLabel logoLabel = new JLabel("");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(new Color(0, 255, 136));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JTextPane descPane = new JTextPane();
        descPane.setText("""
        VenueVibe adalah perusahaan yang bergerak dalam bidang penyewaan properti event seperti sound system, lighting, panggung, dan lainnya.
        Kami menyediakan layanan terbaik dan peralatan berkualitas untuk mendukung kelancaran acara Anda, baik dalam skala kecil maupun besar.
        Dengan pengalaman dan komitmen terhadap kepuasan pelanggan, VenueVibe hadir sebagai solusi andal untuk kebutuhan event Anda.
        """);
        descPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descPane.setForeground(Color.WHITE);
        descPane.setBackground(new Color(45, 45, 65));
        descPane.setEditable(false);
        descPane.setMaximumSize(new Dimension(700, 300));
        descPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        StyledDocument doc = descPane.getStyledDocument();
        SimpleAttributeSet justify = new SimpleAttributeSet();
        StyleConstants.setAlignment(justify, StyleConstants.ALIGN_JUSTIFIED);
        doc.setParagraphAttributes(0, doc.getLength(), justify, false);

        centerPanel.add(titleLabel);
        centerPanel.add(logoLabel);
        centerPanel.add(descPane);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showBarangAdmin() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new BarangAdmin(), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showKonfirmasiPesananPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new KonfirmasiPesananPanel(), BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminDashboard::new);
    }
}
