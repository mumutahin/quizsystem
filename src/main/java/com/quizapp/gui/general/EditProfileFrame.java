package com.quizapp.gui.general;

import com.quizapp.dao.UserDao;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;

public class EditProfileFrame extends JFrame {
    public EditProfileFrame(String email, String role, Runnable onSuccessRedirect) {
        setTitle("Edit Profile");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField fnameField = new JTextField();
        JTextField lnameField = new JTextField();
        JPasswordField passField = new JPasswordField();

        try (ResultSet rs = UserDao.getUserByEmail(email)) {
            if (rs != null && rs.next()) {
                fnameField.setText(rs.getString("first_name"));
                lnameField.setText(rs.getString("last_name"));
                passField.setText(rs.getString("password"));
            } else {
                JOptionPane.showMessageDialog(this, "User not found.");
                dispose();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.add(new JLabel("First Name:"));
        panel.add(fnameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lnameField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        JLabel deleteLabel = new JLabel("<html><u>Want to delete account?</u></html>");
        deleteLabel.setForeground(Color.BLUE);
        deleteLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deletePanel.add(deleteLabel);
        panel.add(new JLabel()); // empty cell
        panel.add(deletePanel);

        JButton updateBtn = new JButton("Update");
        JButton cancelBtn = new JButton("Cancel");

        deleteLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new DeleteAccountFrame(email, role, () -> {
                    // Redirect to initial screen after deletion
                    new InitialFrame();
                }, () -> {
                    // Return to edit profile if user canceled
                    new EditProfileFrame(email, role, onSuccessRedirect);
                });
            }
        });

        updateBtn.addActionListener(e -> {
            String fname = fnameField.getText().trim();
            String lname = lnameField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (fname.isEmpty() || lname.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            boolean success = UserDao.updateUser(email, fname + " " + lname, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Profile updated!");
                dispose();
                onSuccessRedirect.run();  // Redirect to dashboard or other screen
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        });

        cancelBtn.addActionListener(e -> {
            dispose();
            onSuccessRedirect.run(); // Go back without saving
        });

        panel.add(updateBtn);
        panel.add(cancelBtn);

        add(panel);
        setVisible(true);
    }
}
