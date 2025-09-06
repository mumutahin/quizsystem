package com.quizapp.gui.teacher;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.quizapp.dao.QuizDao;
import com.quizapp.model.Quiz;

public class UpdateQuizSetFrame extends JFrame {
    public UpdateQuizSetFrame(String email) {
        setTitle("Update Quiz Questions");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        List<Quiz> quizzes = QuizDao.getAllQuizzes();
        String[] quizTitles = new String[quizzes.size() + 1];
        quizTitles[0] = "-- Select a Quiz --";
        for (int i = 0; i < quizzes.size(); i++) {
            quizTitles[i + 1] = quizzes.get(i).getTitle();
        }
        JComboBox<String> quizDropdown = new JComboBox<>(quizTitles);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(new JLabel("Select Quiz to Update:"), BorderLayout.NORTH);
        centerPanel.add(quizDropdown, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        JButton previewBtn = new JButton("Preview");
        JButton updateBtn = new JButton("Update Questions");
        JButton backBtn = new JButton("Back");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backBtn);
        bottomPanel.add(previewBtn);
        bottomPanel.add(updateBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        previewBtn.addActionListener(e -> {
            int selectedIndex = quizDropdown.getSelectedIndex();
            if (selectedIndex <= 0) {
                JOptionPane.showMessageDialog(this, "Please select a quiz to preview.");
                return;
            }
            Quiz quiz = QuizDao.getQuizById(quizzes.get(selectedIndex - 1).getId());
            if (quiz == null) {
                JOptionPane.showMessageDialog(this, "Failed to load quiz details.");
                return;
            }
            new PreviewQuizFrame(quiz);
        });

        updateBtn.addActionListener(e -> {
            int selectedIndex = quizDropdown.getSelectedIndex();
            if (selectedIndex <= 0) {
                JOptionPane.showMessageDialog(this, "Please select a quiz to update.");
                return;
            }

            Quiz quiz = quizzes.get(selectedIndex - 1); // note: -1
            dispose();
            new AddQuizQuestionsFrame(email, quiz);
        });

        backBtn.addActionListener(e -> {
            dispose();
            new TeacherDashboardFrame(email);
        });

        setVisible(true);
    }
}