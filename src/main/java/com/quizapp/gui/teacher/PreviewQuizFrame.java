package com.quizapp.gui.teacher;

import com.quizapp.model.Question;
import com.quizapp.model.Quiz;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PreviewQuizFrame extends JFrame {
    public PreviewQuizFrame(Quiz quiz) {
        setTitle("Preview: " + quiz.getTitle());
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        displayArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        StringBuilder sb = new StringBuilder();
        List<Question> questions = quiz.getQuestions();

        if (questions == null || questions.isEmpty()) {
            sb.append("No questions found in this quiz.");
        } else {
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                sb.append("Q").append(i + 1).append(". ").append(q.getText()).append("\n");

                List<String> options = q.getOptions();
                List<Integer> correctIndices = q.getCorrectIndices();

                for (int j = 0; j < options.size(); j++) {
                    boolean isCorrect = correctIndices.contains(j);
                    String prefix = isCorrect ? "✔ " : "   ";
                    sb.append(prefix)
                      .append((char) ('A' + j)).append(". ")
                      .append(options.get(j)).append("\n");
                }

                String explanation = q.getExplanation();
                if (explanation != null && !explanation.isBlank()) {
                    sb.append("📘 Explanation: ").append(explanation).append("\n");
                }

                sb.append("\n");
            }
        }

        displayArea.setText(sb.toString());
        displayArea.setCaretPosition(0); // Scroll to top

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose()); // Dispose window on click

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(closeButton);

        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}
