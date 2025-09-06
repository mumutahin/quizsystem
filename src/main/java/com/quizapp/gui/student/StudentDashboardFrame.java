package com.quizapp.gui.student;

import javax.swing.*;

import com.quizapp.gui.general.InitialFrame;
import com.quizapp.gui.general.EditProfileFrame;

import java.awt.*;

public class StudentDashboardFrame extends JFrame {

    private final String studentEmail;

    public StudentDashboardFrame(String email) {
        this.studentEmail = email;

        setTitle("Student Dashboard");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JLabel titleLabel = new JLabel("Welcome Student", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton joinQuizBtn = new JButton("Join a Quiz");
        JButton viewResultsBtn = new JButton("View Results");
        JButton editProfileBtn = new JButton("Edit Profile");
        JButton logoutBtn = new JButton("Logout");

        joinQuizBtn.addActionListener(e -> {
            dispose();
            new JoinQuizFrame(studentEmail);
        });

        viewResultsBtn.addActionListener(e -> {
            dispose();
            new StudentResultFrame(studentEmail);
        });

        editProfileBtn.addActionListener(e -> {
            dispose();
            new EditProfileFrame(studentEmail, "student", () -> new StudentDashboardFrame(studentEmail));
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new InitialFrame();
        });

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        panel.add(titleLabel);
        panel.add(joinQuizBtn);
        panel.add(viewResultsBtn);
        panel.add(editProfileBtn);
        panel.add(logoutBtn);

        add(panel);
        setVisible(true);
    }
}
