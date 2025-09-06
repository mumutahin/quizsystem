package com.quizapp.gui.general;

import com.quizapp.dao.UserDao;
import java.awt.*;
import javax.swing.*;

public class ForgotPasswordFrame extends JFrame {
    public static String codeSent = "";
    public static String userEmail = "";
    public ForgotPasswordFrame(String role) {
        setTitle("Forgot Password");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(null);

        JLabel emailLabel = new JLabel("Enter your email:");
        emailLabel.setBounds(50, 30, 120, 25);
        JTextField emailField = new JTextField();
        emailField.setBounds(170, 30, 160, 25);

        JButton sendBtn = new JButton("Send Code");
        sendBtn.setBounds(150, 80, 100, 30);

        JButton backBtn = new JButton("Back");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(backBtn);

        sendBtn.addActionListener(e -> {
            String email = emailField.getText();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter your email");
                return;
            }

            if (!UserDao.emailExists(email, role)) {
                JOptionPane.showMessageDialog(this, "Email not found");
                return;
            }

            codeSent = UserDao.generateCode();
            userEmail = email;

            JOptionPane.showMessageDialog(this, "Code sent to your email (demo): " + codeSent);
            dispose();
            new ResetPasswordFrame(role);
        });

        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(role);
        });

        formPanel.add(emailField);
        formPanel.add(sendBtn);
        formPanel.add(emailLabel);
        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}