package com.quizapp.dao;

import com.quizapp.model.Result;
import com.quizapp.utils.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ResultDao {
    public static void createResultsTable() {
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS results (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_email TEXT,
                    quiz_id INTEGER,
                    quiz_code TEXT,
                    score INTEGER,
                    time_taken INTEGER,
                    taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(quiz_id) REFERENCES quizzes(id)
                );""");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void saveStudentResult(String email, String quizCode, int score, int timeTaken) {
        System.out.println("Attempting to save result for: " + email + " - " + quizCode);
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement getQuizIdStmt = conn.prepareStatement("SELECT id FROM quizzes WHERE quiz_code = ?");
            getQuizIdStmt.setString(1, quizCode);
            ResultSet rs = getQuizIdStmt.executeQuery();

            if (!rs.next()) {
                System.err.println("No quiz found with code: " + quizCode);
                return;
            }

            int quizId = rs.getInt("id");
            
            PreparedStatement checkStmt = conn.prepareStatement("""
                SELECT 1 FROM results WHERE student_email = ? AND quiz_id = ?
            """);
            checkStmt.setString(1, email);
            checkStmt.setInt(2, quizId);
            ResultSet checkRs = checkStmt.executeQuery();
            if (checkRs.next()) {
                System.out.println("Result already exists for: " + email + " - " + quizCode);
                return;
            }

            PreparedStatement stmt = conn.prepareStatement("""
                INSERT INTO results (student_email, quiz_id, quiz_code, score, time_taken)
                VALUES (?, ?, ?, ?, ?)
            """);
            stmt.setString(1, email);
            stmt.setInt(2, quizId);
            stmt.setString(3, quizCode);
            stmt.setInt(4, score);
            stmt.setInt(5, timeTaken);
            stmt.executeUpdate();
            System.out.println("Result saved successfully for: " + email);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Result> getStudentResults(String email) {
        List<Result> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("""
                SELECT q.quiz_code, r.score, r.time_taken
                FROM results r
                JOIN quizzes q ON r.quiz_id = q.id
                WHERE r.student_email = ?
            """);) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String code = rs.getString("quiz_code");
                int score = rs.getInt("score");
                int time = rs.getInt("time_taken");
                int rank = computeRank(conn, code, score, time);
                results.add(new Result(code, score, rank, time));
                System.out.println("Student Result: " + email + " -> " + code + " Score: " + score);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static List<Result> getAllResults() {
        List<Result> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("""
                SELECT u.email, q.title, r.score, r.time_taken, r.taken_at
                FROM results r
                JOIN users u ON r.student_email = u.email
                JOIN quizzes q ON r.quiz_id = q.id
            """)) {
            while (rs.next()) {
                results.add(new Result(
                    rs.getString("email"),
                    rs.getString("title"),
                    rs.getInt("score"),
                    rs.getInt("time_taken"),
                    -1,
                    rs.getString("taken_at")
                ));
                System.out.println("Fetched: " + rs.getString("email") + " - " + rs.getString("title") + " - " + rs.getInt("score"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private static int computeRank(Connection conn, String code, int score, int timeTaken) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "SELECT COUNT(*)+1 FROM results WHERE quiz_code = ? AND (score > ? OR (score = ? AND time_taken < ?))");
        stmt.setString(1, code);
        stmt.setInt(2, score);
        stmt.setInt(3, score);
        stmt.setInt(4, timeTaken);
        ResultSet rs = stmt.executeQuery();
        return rs.next() ? rs.getInt(1) : 1;
    }

}
