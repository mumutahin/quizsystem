package com.quizapp.gui.general;

import com.quizapp.dao.UserDao;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    public RegisterFrame(String role) {
        setTitle(role + " Registration");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(null);

        JLabel fnameLabel = new JLabel("First Name:");
        fnameLabel.setBounds(50, 20, 100, 25);
        JTextField fnameField = new JTextField();
        fnameField.setBounds(150, 20, 180, 25);

        JLabel lnameLabel = new JLabel("Last Name:");
        lnameLabel.setBounds(50, 60, 100, 25);
        JTextField lnameField = new JTextField();
        lnameField.setBounds(150, 60, 180, 25);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 100, 100, 25);
        JTextField emailField = new JTextField();
        emailField.setBounds(150, 100, 180, 25);

        JLabel emailStatus = new JLabel(); // Shows validation result
        emailStatus.setBounds(150, 125, 200, 15);
        emailStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        emailStatus.setForeground(Color.RED);
        formPanel.add(emailStatus);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 140, 100, 25);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 140, 180, 25);

        JLabel confirmLabel = new JLabel("Confirm:");
        confirmLabel.setBounds(50, 180, 100, 25);
        JPasswordField confirmField = new JPasswordField();
        confirmField.setBounds(150, 180, 180, 25);

        JLabel mismatch = new JLabel("Passwords do not match");
        mismatch.setForeground(Color.RED);
        mismatch.setBounds(150, 210, 200, 20);
        mismatch.setVisible(false);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(150, 240, 100, 30);

        JLabel loginLink = new JLabel("<HTML>Already have an account? <U>Login here</U></HTML>");
        loginLink.setForeground(Color.BLUE);
        loginLink.setBounds(110, 280, 200, 25);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton backBtn = new JButton("Back");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(backBtn);
        
        confirmField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { check(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { check(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            private void check() {
                mismatch.setVisible(!String.valueOf(confirmField.getPassword()).equals(String.valueOf(passField.getPassword())));
            }
        });

        emailField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { validateEmail(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}

            private void validateEmail() {
                String email = emailField.getText().trim();
                if (email.isEmpty()) {
                    emailStatus.setText("");
                } else if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                    emailStatus.setText("Invalid email format");
                } else if (UserDao.emailExists(email, role)) {
                    emailStatus.setText("Email already registered");
                } else {
                    emailStatus.setText("Email looks good");
                    emailStatus.setForeground(new Color(0, 128, 0)); // Green
                }
            }
        });

        registerBtn.addActionListener(e -> {
            String fname = fnameField.getText().trim();
            String lname = lnameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields");
                return;
            }

            // Email format validation using simple regex
            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                JOptionPane.showMessageDialog(this, "Invalid email format");
                return;
            }

            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
                return;
            }

            if (UserDao.emailExists(email, role)) {
                JOptionPane.showMessageDialog(this, "Email already exists");
                return;
            }

            if (UserDao.register(fname, lname, email, pass, role)) {
                JOptionPane.showMessageDialog(this, "Registered Successfully!");
                dispose();
                new LoginFrame(role);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Try again.");
            }
        });

        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new LoginFrame(role);
            }
        });
        
        backBtn.addActionListener(e -> {
            dispose();
            new InitialFrame();
        });
        
        formPanel.add(fnameLabel); formPanel.add(fnameField);
        formPanel.add(lnameLabel); formPanel.add(lnameField);
        formPanel.add(emailLabel); formPanel.add(emailField);
        formPanel.add(emailStatus);
        formPanel.add(passLabel); formPanel.add(passField);
        formPanel.add(confirmLabel); formPanel.add(confirmField);
        formPanel.add(mismatch); formPanel.add(registerBtn);
        formPanel.add(loginLink);

        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

}
