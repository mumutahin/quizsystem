package com.quizapp.gui.teacher;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.quizapp.dao.*;
import com.quizapp.model.*;

public class TakeQuizFrame extends JFrame {
    public TakeQuizFrame(String email) {
        setTitle("Take a Quiz (Teacher)");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        List<Quiz> quizzes = QuizDao.getAllQuizzes();
        String[] quizTitles = new String[quizzes.size() + 1];
        quizTitles[0] = "-- Select a Quiz --";
        for (int i = 0; i < quizzes.size(); i++) {
            quizTitles[i + 1] = quizzes.get(i).getTitle();
        }
        JComboBox<String> quizDropdown = new JComboBox<>(quizTitles);
        JButton previewBtn = new JButton("Preview");
        JButton startBtn = new JButton("Start Quiz");
        setLayout(new BorderLayout());
        add(quizDropdown, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            dispose();
            new TeacherDashboardFrame(email);
        });
        bottomPanel.add(backBtn, BorderLayout.WEST);
        bottomPanel.add(previewBtn, BorderLayout.CENTER);
        bottomPanel.add(startBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        JTextField sectionField = new JTextField(10);
        JTextField timeField = new JTextField(5);
        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("Section (optional):"));
        top.add(sectionField);
        top.add(new JLabel("Time Limit (min):"));
        top.add(timeField);
        add(top, BorderLayout.NORTH);

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

        startBtn.addActionListener(e -> {
            if (quizzes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No quizzes available to select.");
                return;
            }
            int index = quizDropdown.getSelectedIndex();
            if (index <= 0) {
                JOptionPane.showMessageDialog(this, "Please select a quiz to start.");
                return;
            }
            Quiz quiz = quizzes.get(index - 1);
            String section = sectionField.getText().trim();
            String timeText = timeField.getText().trim();
            if (timeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a time limit.");
                return;
            }
            int timeLimit;
            int minutes;
            try {
                minutes = Integer.parseInt(timeText);
                if (minutes <= 0) throw new NumberFormatException();
                timeLimit = minutes * 60;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Time limit must be a **positive** number.");
                return;
            }
            String code;
            do {
                code = QuizDao.generateRandomCode();
            } while (!RoomDao.isCodeUnique(code));

            int roomId = RoomDao.createRoom(quiz.getId(), code);
            RoomDao.markRoomActiveByRoomId(roomId, section, timeLimit);

            dispose();
            new TeacherWaitingRoomFrame(email, code, roomId, timeLimit);
        });

        setVisible(true);
    }
}