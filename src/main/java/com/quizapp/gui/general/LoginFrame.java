package com.quizapp.gui.general;

import com.quizapp.dao.UserDao;
import com.quizapp.gui.student.StudentDashboardFrame;
import com.quizapp.gui.teacher.TeacherDashboardFrame;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    public LoginFrame(String role) {
        setTitle(role + " Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(null);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 30, 80, 25);
        JTextField emailField = new JTextField();
        emailField.setBounds(150, 30, 180, 25);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 70, 80, 25);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 70, 180, 25);

        JLabel forgotPass = new JLabel("<HTML><U>Forgot Password?</U></HTML>");
        forgotPass.setForeground(Color.BLUE);
        forgotPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPass.setBounds(150, 100, 180, 25);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 130, 100, 30);

        JLabel registerLink = new JLabel("<HTML>Got no account? <U>Register here</U></HTML>");
        registerLink.setForeground(Color.BLUE);
        registerLink.setBounds(110, 170, 200, 25);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton backBtn = new JButton("Back");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(backBtn);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());
            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter email and password");
                return;
            }

            if (UserDao.login(email, pass, role)) {
                JOptionPane.showMessageDialog(this, "Login successful");
                if (role.equals("student")) {
                    dispose();
                    new StudentDashboardFrame(email);
                }
                if (role.equals("teacher")) {
                    dispose();
                    new TeacherDashboardFrame(email);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }

        });

        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new RegisterFrame(role);
            }
        });

        forgotPass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new ForgotPasswordFrame(role);
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new InitialFrame();
        });

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passLabel);
        formPanel.add(passField);
        formPanel.add(forgotPass);
        formPanel.add(loginBtn);
        formPanel.add(registerLink);

        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

}
