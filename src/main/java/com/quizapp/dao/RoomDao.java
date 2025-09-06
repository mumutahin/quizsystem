package com.quizapp.dao;

import com.quizapp.utils.DatabaseConnection;

import java.sql.*;

public class RoomDao {
    public static void createTable() {
        String createSql = """
            CREATE TABLE IF NOT EXISTS quiz_rooms (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                quiz_id INTEGER NOT NULL,
                code TEXT UNIQUE NOT NULL,
                active INTEGER DEFAULT 0 NOT NULL,
                section TEXT,
                time_limit INTEGER,
                started INTEGER DEFAULT 0,
                FOREIGN KEY(quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
            );
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createSql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCodeUnique(String code) {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM quiz_rooms WHERE code = ?")) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            return !rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int createRoom(int quizId, String code) {
        String sql = "INSERT INTO quiz_rooms (quiz_id, code) VALUES (?, ?)";
        String idSql = "SELECT last_insert_rowid()";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, quizId);
                stmt.setString(2, code);
                stmt.executeUpdate();
            }

            // Fetch the last inserted ID (room ID)
            try (Statement idStmt = conn.createStatement();
                ResultSet rs = idStmt.executeQuery(idSql)) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void markRoomActiveByRoomId(int roomId, String section, int timeLimitSeconds) {
        String sql = "UPDATE quiz_rooms SET active = 1, section = ?, time_limit = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, section);
            ps.setInt(2, timeLimitSeconds);
            ps.setInt(3, roomId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void markRoomInactiveByRoomId(int roomId) {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE quiz_rooms SET active = 0 WHERE id = ?")) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getQuizIdByCode(String code) {
        String sql = "SELECT quiz_id FROM quiz_rooms WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("quiz_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getQuizIdFromRoomId(int roomId) {
        String sql = "SELECT quiz_id FROM quiz_rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("quiz_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getRoomIdByCode(String code) {
        String sql = "SELECT id FROM quiz_rooms WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSectionForCode(String code) {
        String sql = "SELECT section FROM quiz_rooms WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("section");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void markRoomAsStarted(int roomId) {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE quiz_rooms SET started = TRUE WHERE id = ?")) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}