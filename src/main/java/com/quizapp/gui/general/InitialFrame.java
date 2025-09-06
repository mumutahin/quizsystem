package com.quizapp.gui.general;

import javax.swing.*;
import java.awt.*;

public class InitialFrame extends JFrame {
    public InitialFrame() {
        setTitle("Online Quiz System");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(3, 1, 15, 15));
        JLabel title = new JLabel("Select Your Role", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));

        JButton studentBtn = new JButton("Join a Quiz (Student)");
        JButton teacherBtn = new JButton("Take a Quiz (Teacher)");

        studentBtn.addActionListener(e -> {
            dispose();
            new LoginFrame("student");
        });

        teacherBtn.addActionListener(e -> {
            dispose();
            new LoginFrame("teacher");
        });
        
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        panel.add(title);
        panel.add(studentBtn);
        panel.add(teacherBtn);

        add(panel);
        setVisible(true);
    }
}
