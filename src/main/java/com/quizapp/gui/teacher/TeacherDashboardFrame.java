package com.quizapp.gui.teacher;

import javax.swing.*;
import java.awt.*;
import com.quizapp.gui.general.InitialFrame;
import com.quizapp.gui.general.EditProfileFrame;

public class TeacherDashboardFrame extends JFrame {
    public TeacherDashboardFrame(String email) {
        
        setTitle("Teacher Dashboard");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JLabel titleLabel = new JLabel("Welcome Teacher", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnTakeQuiz = new JButton("Take a Quiz");
        JButton btnAddQuestions = new JButton("Add Quiz Questions");
        JButton btnUpdateQuiz = new JButton("Update Remaining Ones");
        JButton btnDeleteQuiz = new JButton("Delete Remaining Ones");
        JButton btnViewResults = new JButton("View Results");
        JButton btnEditProfile = new JButton("Edit Profile");
        JButton btnLogout = new JButton("Logout");

        btnTakeQuiz.addActionListener(e -> {
            dispose();
            new TakeQuizFrame(email);
        });

        btnAddQuestions.addActionListener(e -> {
            dispose();
            new AddQuizQuestionsFrame(email, null);
        });

        btnUpdateQuiz.addActionListener(e -> {
            dispose();
            new UpdateQuizSetFrame(email);
        });

        btnDeleteQuiz.addActionListener(e -> {
            dispose();
            new DeleteQuizSetFrame(email);
        });

        btnViewResults.addActionListener(e -> {
            dispose();
            new TeacherResultFrame(email);
        });

        btnEditProfile.addActionListener(e -> {
            dispose();
            new EditProfileFrame(email, "teacher", () -> new TeacherDashboardFrame(email));
        });

        btnLogout.addActionListener(e -> {
            dispose();
            new InitialFrame();
        });

        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        buttonPanel.add(btnTakeQuiz);
        buttonPanel.add(btnAddQuestions);
        buttonPanel.add(btnUpdateQuiz);
        buttonPanel.add(btnDeleteQuiz);
        buttonPanel.add(btnViewResults);
        buttonPanel.add(btnEditProfile);
        buttonPanel.add(btnLogout);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(titleLabel, BorderLayout.NORTH);
        wrapper.add(buttonPanel, BorderLayout.CENTER);

        add(wrapper);
        setVisible(true);
    }
}