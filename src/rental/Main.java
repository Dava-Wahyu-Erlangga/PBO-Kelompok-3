package rental;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Jalankan GUI di Event Dispatch Thread (EDT) agar aman untuk Swing
        SwingUtilities.invokeLater(() -> {
            new LoginFrame(); // Memulai aplikasi dari Login
        });
    }
}
