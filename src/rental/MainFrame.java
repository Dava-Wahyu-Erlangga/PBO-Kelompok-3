package rental;

import javax.swing.JFrame;

public class MainFrame extends JFrame {
    public MainFrame(int userId, String namaUser) {
        setTitle("Beranda - Selamat Datang " + namaUser);
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }
}
