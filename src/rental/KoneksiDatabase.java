package rental;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KoneksiDatabase {
    public static Connection getConnection() {
        Connection conn = null;
        String url = "jdbc:mysql://127.0.0.1:3306/dbrental1"; // Nama DB
        String user = "root"; // Username MySQL
        String password = ""; // Password MySQL

        try {
            // Load Driver (opsional untuk Java 8+, tapi tetap aman digunakan)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Buat koneksi
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Koneksi berhasil!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver tidak ditemukan!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Koneksi gagal!");
            e.printStackTrace();
        }

        return conn;
    }

    // Untuk testing koneksi
    public static void main(String[] args) {
        getConnection();
    }
}
