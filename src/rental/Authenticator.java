package rental;

import java.sql.*;

public class Authenticator {

    public User login(String username, String password) throws SQLException {
        try (Connection conn = KoneksiDatabase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM user WHERE username = ? AND password = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idUser");
                    String role = rs.getString("role");
                    return new User(id, username, role);
                }
            }
        }
        return null;
    }
}
