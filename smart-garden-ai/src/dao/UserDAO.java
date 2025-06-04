package dao;

import java.sql.*;

public class UserDAO {

    public void createUser(String username, String password) throws SQLException {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "INSERT INTO user (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
        }
    }

    public int getUserId(String username) throws SQLException {
        try (Connection conn = DBManager.getConnection()) {
            String sql = "SELECT id FROM user WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        return -1;
    }
}

