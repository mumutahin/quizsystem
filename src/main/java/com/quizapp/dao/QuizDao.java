package com.quizapp.dao;

import com.quizapp.model.Quiz;
import com.quizapp.model.Question;
import com.quizapp.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizDao {
    public static void createQuizTable() {
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            s.executeUpdate("""
                CREATE TABLE IF NOT EXISTS quizzes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT UNIQUE,
                    class TEXT,
                    section TEXT,
                    quiz_code TEXT UNIQUE,
                    teacher_email TEXT
                );""");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String generateRandomCode() {
        String chars = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
        StringBuilder code = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return code.toString();
    }

    public static int addQuiz(String title, String clazz, String section, String code, String teacherEmail) {
        if (doesQuizExist(title, code)) {
            System.err.println("Quiz already exists with the same title or code.");
            return -1;
        }
        String sql = "INSERT INTO quizzes (title, class, section, quiz_code, teacher_email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, clazz);
            stmt.setString(3, section);
            stmt.setString(4, code);
            stmt.setString(5, teacherEmail);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.err.println("Quiz insertion failed, no rows affected.");
                return -1;
            }

            // Manually query the ID of the inserted quiz
            try (PreparedStatement selectStmt = conn.prepareStatement("SELECT id FROM quizzes WHERE title = ? AND quiz_code = ?")) {
                selectStmt.setString(1, title);
                selectStmt.setString(2, code);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int quizId = rs.getInt("id");
                        System.out.println("Manually retrieved quiz ID: " + quizId);
                        return quizId;
                    } else {
                        System.err.println("Quiz inserted but ID could not be found.");
                        return -1;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error adding quiz: " + e.getMessage());
            return -1;
        }
    }

    public static boolean doesQuizExist(String title, String code) {
        String sql = "SELECT 1 FROM quizzes WHERE title = ? OR quiz_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, code);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if a row exists
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // assume exists if query fails
        }
    }

    public static List<Quiz> getAllQuizzes() {
        var quizzes = new ArrayList<Quiz>();
        String sql = "SELECT * FROM quizzes";
        try (Connection conn = DatabaseConnection.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                quizzes.add(new Quiz(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("class"),
                    rs.getString("section"),
                    rs.getString("quiz_code"),
                    rs.getString("teacher_email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public static Quiz getByCode(String code) {
        String sql = "SELECT * FROM quizzes WHERE quiz_code = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Quiz(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("class"),
                        rs.getString("section"),
                        rs.getString("quiz_code"),
                        rs.getString("teacher_email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Quiz getQuizById(int id) {
        String sql = "SELECT * FROM quizzes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Quiz quiz = new Quiz(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("class"),
                    rs.getString("section"),
                    rs.getString("quiz_code"),
                    rs.getString("teacher_email")
                );

                // Load questions
                List<Question> questions = QuestionDao.getQuestionsByQuizId(id);
                quiz.setQuestions(questions);

                return quiz;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteQuiz(int quizId) {
        Connection conn = null;  // Declare here
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);  // Start transaction

            QuestionDao.deleteQuestionsByQuizId(quizId);

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM quiz_rooms WHERE quiz_id = ?")) {
                ps.setInt(1, quizId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM quizzes WHERE id = ?")) {
                ps.setInt(1, quizId);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static class QuizSessionInfo {
        public List<Question> questions;
        public int timeLimit;

        public QuizSessionInfo(List<Question> questions, int timeLimit) {
            this.questions = questions;
            this.timeLimit = timeLimit;
        }
    }

    public static QuizSessionInfo getQuestionsForCode(String code) {
        String sql = "SELECT quiz_id, time_limit FROM quiz_rooms WHERE code = ? AND active = 1";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int quizId = rs.getInt("quiz_id");
                int timeLimit = rs.getInt("time_limit");
                List<Question> questions = QuestionDao.getQuestionsByQuizId(quizId);
                return new QuizSessionInfo(questions, timeLimit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isQuizActive(String code) {
        String sql = "SELECT active FROM quiz_rooms WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean("active");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasQuizStarted(String code) {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT started FROM quiz_rooms WHERE code = ?")) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("started");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
