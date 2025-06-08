package rental;

import java.sql.*;

public class RegisterHandler {
    public boolean register(User user) throws SQLException {
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO user (nama, username, password, alamat, noTelp, role) VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setString(1, user.getNama());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getAlamat());
            stmt.setString(5, user.getNoTelp());
            stmt.setString(6, user.getRole());

            return stmt.executeUpdate() > 0;
        }
    }
}
