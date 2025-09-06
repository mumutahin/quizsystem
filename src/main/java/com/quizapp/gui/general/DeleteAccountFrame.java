package com.quizapp.gui.general;

import com.quizapp.dao.UserDao;

import javax.swing.*;
import java.awt.*;

public class DeleteAccountFrame extends JFrame {
    public DeleteAccountFrame(String email, String role, Runnable onDeleteSuccess, Runnable onCancelBack) {
        setTitle("Delete Account");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JButton deleteBtn = new JButton("Delete");
        JButton backBtn = new JButton("Back");
        panel.add(backBtn);
        panel.add(deleteBtn);

        deleteBtn.addActionListener(e -> {
            String enteredEmail = emailField.getText().trim();
            String enteredPass = new String(passwordField.getPassword()).trim();

            if (enteredEmail.isEmpty() || enteredPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter email and password.");
                return;
            }

            if (!enteredEmail.equals(email)) {
                JOptionPane.showMessageDialog(this, "Entered email does not match your profile.");
                return;
            }

            boolean authenticated = UserDao.login(enteredEmail, enteredPass, role);
            if (!authenticated) {
                JOptionPane.showMessageDialog(this, "Incorrect email or password.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = UserDao.deleteUser(email, role);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Account deleted successfully.");
                    dispose();
                    onDeleteSuccess.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Account deletion failed.");
                }
            }
            // If NO: do nothing, stay on same frame
        });

        backBtn.addActionListener(e -> {
            dispose();
            onCancelBack.run();
        });

        add(panel);
        setVisible(true);
    }
}
