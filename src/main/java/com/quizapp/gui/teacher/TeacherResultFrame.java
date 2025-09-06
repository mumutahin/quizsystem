package com.quizapp.gui.teacher;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.quizapp.dao.ResultDao;
import com.quizapp.model.Result;

public class TeacherResultFrame extends JFrame {
    public TeacherResultFrame(String email) {
        setTitle("View Results");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        List<Result> results = ResultDao.getAllResults();
        String[] columns = {"Student", "Quiz", "Marks", "Time Taken", "Date"};

        String[][] data = new String[results.size()][5];
        for (int i = 0; i < results.size(); i++) {
            Result r = results.get(i);
            data[i][0] = r.getStudentEmail();
            data[i][1] = r.getQuizCode(); 
            data[i][2] = r.getScore() + "";
            data[i][3] = r.getTimeTaken() + "s";
            data[i][4] = r.getTakenAt();
        }

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smooth scroll

        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with Back button
        JButton backBtn = new JButton("Back");

        backBtn.addActionListener(e -> {
            dispose();
            new TeacherDashboardFrame(email);
        });

        add(backBtn, BorderLayout.SOUTH);

        setVisible(true);
    }
}