package com.quizapp.gui.student;

import com.quizapp.dao.ResultDao;
import com.quizapp.model.Result;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentResultFrame extends JFrame {

    private final String studentEmail;

    public StudentResultFrame(String studentEmail) {
        this.studentEmail = studentEmail;
        setupFrame();
        List<Result> results = ResultDao.getStudentResults(studentEmail);
        buildTable(results, null);
        addBackButton();
        setVisible(true);
    }

    public StudentResultFrame(String studentEmail, String quizCode) {
        this.studentEmail = studentEmail;
        setupFrame();
        List<Result> results = ResultDao.getStudentResults(studentEmail);
        buildTable(results, quizCode); // highlight specific quiz
        addBackButton();
        setVisible(true);
    }

    // --- Private helper methods ---

    private void setupFrame() {
        setTitle("Your Results");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void buildTable(List<Result> results, String quizCodeToHighlight) {
        String[] cols = {"Quiz Code", "Score", "Rank", "Time (s)"};
        String[][] data = new String[results.size()][4];
        int highlightRow = -1;

        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            data[i][0] = r.getQuizCode();
            data[i][1] = String.valueOf(r.getScore());
            data[i][2] = String.valueOf(r.getRank());
            data[i][3] = String.valueOf(r.getTimeTaken());

            if (quizCodeToHighlight != null && r.getQuizCode().equals(quizCodeToHighlight)) {
                highlightRow = i;
            }
        }

        JTable table = new JTable(data, cols);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        if (highlightRow != -1) {
            final int rowToHighlight = highlightRow;
            SwingUtilities.invokeLater(() -> {
                table.setRowSelectionInterval(rowToHighlight, rowToHighlight);
                table.scrollRectToVisible(table.getCellRect(rowToHighlight, 0, true));
            });
        }
    }

    private void addBackButton() {
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            dispose();
            new StudentDashboardFrame(studentEmail);
        });
        add(backBtn, BorderLayout.SOUTH);
    }
}
