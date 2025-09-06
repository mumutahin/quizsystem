package com.quizapp.dao;

import com.quizapp.model.Question;
import com.quizapp.utils.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class QuestionDao {
    public static void createTable() {
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    quiz_id INTEGER,
                    text TEXT NOT NULL,
                    explanation TEXT,
                    FOREIGN KEY(quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
                );
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS choices (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    question_id INTEGER,
                    text TEXT,
                    is_correct INTEGER,
                    FOREIGN KEY(question_id) REFERENCES questions(id) ON DELETE CASCADE
                );
            """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addQuestion(int quizId, Question question) {
        String insertQ = "INSERT INTO questions (quiz_id, text, explanation) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement qStmt = conn.prepareStatement(insertQ)) {

            qStmt.setInt(1, quizId);
            qStmt.setString(2, question.getText());
            qStmt.setString(3, question.getExplanation());
            qStmt.executeUpdate();

            int questionId = -1;
            try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    questionId = rs.getInt(1);
                    System.out.println("Generated question ID: " + questionId);
                }
            }

            if (questionId == -1) {
                System.err.println("Failed to retrieve question ID. Aborting choice insert.");
                return;
            }

            List<String> options = question.getOptions();
            if (options == null || options.isEmpty()) {
                System.err.println("Warning: Question has no options, skipping: " + question.getText());
                return;
            }

            String insertChoice = "INSERT INTO choices (question_id, text, is_correct) VALUES (?, ?, ?)";
            for (int i = 0; i < options.size(); i++) {
                try (PreparedStatement cStmt = conn.prepareStatement(insertChoice)) {
                    cStmt.setInt(1, questionId);
                    cStmt.setString(2, options.get(i));
                    cStmt.setInt(3, question.getCorrectIndices().contains(i) ? 1 : 0);
                    cStmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Question> getQuestionsByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement qStmt = conn.prepareStatement("SELECT * FROM questions WHERE quiz_id = ?");
            qStmt.setInt(1, quizId);
            ResultSet qRs = qStmt.executeQuery();
            while (qRs.next()) {
                int qid = qRs.getInt("id");
                String text = qRs.getString("text");
                String explanation = qRs.getString("explanation");
                PreparedStatement cStmt = conn.prepareStatement("SELECT * FROM choices WHERE question_id = ?");
                cStmt.setInt(1, qid);
                ResultSet cRs = cStmt.executeQuery();
                List<String> options = new ArrayList<>();
                List<Integer> correctIndices = new ArrayList<>();
                int index = 0;
                while (cRs.next()) {
                    options.add(cRs.getString("text"));
                    if (cRs.getInt("is_correct") == 1) {
                        correctIndices.add(index);
                    }
                    index++;
                }
                questions.add(new Question(text, options, correctIndices, explanation));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public static void deleteQuestionsByQuizId(int quizId) {
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM choices WHERE question_id IN (SELECT id FROM questions WHERE quiz_id = ?)");
            PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM questions WHERE quiz_id = ?")) {
            stmt1.setInt(1, quizId);
            stmt1.executeUpdate();
            stmt2.setInt(1, quizId);
            stmt2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
