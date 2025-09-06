package com.quizapp.dao;

import com.quizapp.utils.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ParticipantDao {

    public static void createParticipantTable() {
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS participants (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_email TEXT NOT NULL,
                    room_id INTEGER NOT NULL,
                    FOREIGN KEY(room_id) REFERENCES quiz_rooms(id) ON DELETE CASCADE
                );
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getStudentsInRoom(int roomId) {
        List<String> students = new ArrayList<>();
        String sql = """
            SELECT DISTINCT u.first_name || ' ' || u.last_name AS full_name
            FROM participants p
            JOIN users u ON p.student_email = u.email
            WHERE p.room_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(rs.getString("full_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public static void markStudentJoined(String studentEmail, int roomId) {
        String checkSql = "SELECT 1 FROM participants WHERE student_email = ? AND room_id = ?";
        String insertSql = "INSERT INTO participants (student_email, room_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, studentEmail);
            checkStmt.setInt(2, roomId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, studentEmail);
                    insertStmt.setInt(2, roomId);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void kickStudentsByRoomId(int roomId) {
        String getCodeSQL = "SELECT code FROM quiz_rooms WHERE id = ?";
        String deleteResultsSQL = "DELETE FROM results WHERE quiz_code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement codeStmt = conn.prepareStatement(getCodeSQL)) {

            codeStmt.setInt(1, roomId);
            ResultSet rs = codeStmt.executeQuery();
            if (rs.next()) {
                String quizCode = rs.getString("code");

                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteResultsSQL)) {
                    deleteStmt.setString(1, quizCode);
                    deleteStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeStudentFromRoom(String studentEmail, int roomId) {
        String sql = "DELETE FROM participants WHERE student_email = ? AND room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentEmail);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
