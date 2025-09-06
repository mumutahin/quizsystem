package com.quizapp.gui.general;

import com.quizapp.dao.UserDao;

import javax.swing.*;
import java.awt.*;

public class ResetPasswordFrame extends JFrame {
    public ResetPasswordFrame(String role) {
        setTitle("Reset Password");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        JPanel formPanel = new JPanel(null);

        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setBounds(50, 30, 120, 25);
        JPasswordField newPassField = new JPasswordField();
        newPassField.setBounds(170, 30, 160, 25);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setBounds(50, 70, 120, 25);
        JPasswordField confirmField = new JPasswordField();
        confirmField.setBounds(170, 70, 160, 25);

        JLabel mismatch = new JLabel("Passwords do not match");
        mismatch.setForeground(Color.RED);
        mismatch.setBounds(170, 100, 200, 20);
        mismatch.setVisible(false);

        JLabel codeLabel = new JLabel("Enter Code:");
        codeLabel.setBounds(50, 130, 100, 25);
        JTextField codeField = new JTextField();
        codeField.setBounds(170, 130, 160, 25);

        JButton resetBtn = new JButton("Reset");
        resetBtn.setBounds(150, 180, 100, 30);

        JButton backBtn = new JButton("Back");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(backBtn);

        confirmField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { check(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { check(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            private void check() {
                mismatch.setVisible(!String.valueOf(confirmField.getPassword()).equals(String.valueOf(newPassField.getPassword())));
            }
        });

        resetBtn.addActionListener(e -> {
            String pass = new String(newPassField.getPassword());
            String confirm = new String(confirmField.getPassword());
            String code = codeField.getText();

            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
                return;
            }

            if (!code.equals(ForgotPasswordFrame.codeSent)) {
                JOptionPane.showMessageDialog(this, "Invalid code");
                return;
            }

            if (UserDao.resetPassword(ForgotPasswordFrame.userEmail, pass)) {
                JOptionPane.showMessageDialog(this, "Password reset successful");
                dispose();
                new LoginFrame(role);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password");
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new ForgotPasswordFrame(role);
        });

        formPanel.add(newPassLabel);
        formPanel.add(newPassField);
        formPanel.add(confirmLabel);
        formPanel.add(confirmField);
        formPanel.add(mismatch);
        formPanel.add(codeLabel);
        formPanel.add(codeField);
        formPanel.add(resetBtn);
        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}