package com.quizapp.dao;

import com.quizapp.utils.DatabaseConnection;
import java.sql.*;
import java.util.Random;

public class UserDao {

    public static void createUserTable() {
        try (Connection c = DatabaseConnection.getConnection();
            Statement s = c.createStatement()) {
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    UNIQUE(email, role)
                );""");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static boolean register(String fname, String lname, String email, String password, String role) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, role);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage()); // 👈 Add this line
            return false;
        }
    }

    public static boolean login(String email, String password, String role) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, role);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean resetPassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean emailExists(String email, String role) {
        String sql = "SELECT id FROM users WHERE email = ? AND role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, role);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public static boolean updateName(String email, String role, String fname, String lname) {
        String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE email = ? AND role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setString(3, email);
            ps.setString(4, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ResultSet getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            return ps.executeQuery();  // caller must handle closing
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean updateUser(String email, String name, String password) {
        // We'll assume "name" is a concatenation of first + last name
        String[] names = name.trim().split(" ", 2);
        String first = names.length > 0 ? names[0] : "";
        String last = names.length > 1 ? names[1] : "";

        String sql = "UPDATE users SET first_name = ?, last_name = ?, password = ? WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, first);
            ps.setString(2, last);
            ps.setString(3, password);
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUser(String email, String role) {
        String sql = "DELETE FROM users WHERE email = ? AND role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, role);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
