package com.quizapp.gui.teacher;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.quizapp.dao.ParticipantDao;
import com.quizapp.dao.RoomDao;

public class TeacherWaitingRoomFrame extends JFrame {
    private DefaultListModel<String> studentListModel = new DefaultListModel<>();
    private Timer studentTimer;
    private Timer countdownTimer;
    private boolean quizStarted = false;

    public TeacherWaitingRoomFrame(String email, String roomCode, int roomId, int allowedTimeSec) {
        setTitle("Waiting Room — Code: " + roomCode);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with room code and countdown
        JLabel codeLabel = new JLabel("Room Code: " + roomCode, SwingConstants.CENTER);
        codeLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel countdownLabel = new JLabel("Time Remaining: " + formatTime(allowedTimeSec), SwingConstants.CENTER);
        countdownLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(codeLabel);
        topPanel.add(countdownLabel);
        add(topPanel, BorderLayout.NORTH);

        // Student list
        JList<String> studentList = new JList<>(studentListModel);
        JScrollPane scroll = new JScrollPane(studentList);
        add(scroll, BorderLayout.CENTER);

        // Buttons
        JButton backBtn = new JButton("End Session");
        JButton startBtn = new JButton("Start Quiz");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(backBtn);
        bottom.add(startBtn);
        add(bottom, BorderLayout.SOUTH);

        // Timer for updating student list every 2s
        studentTimer = new Timer(true);
        studentTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<String> names = ParticipantDao.getStudentsInRoom(roomId);
                SwingUtilities.invokeLater(() -> {
                    studentListModel.clear();
                    names.forEach(studentListModel::addElement);
                });
            }
        }, 0, 2000);

        // Event: End session manually
        backBtn.addActionListener(e -> {
            String message = quizStarted
                ? "The quiz has already started. If you end the session now, students will be kicked out and may lose progress.\nAre you sure you want to proceed?"
                : "Are you sure you want to end the session? Students will be removed.";

            int choice = JOptionPane.showConfirmDialog(this, message, "Confirm End Session", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                stopAllTimers();

                RoomDao.markRoomInactiveByRoomId(roomId);
                ParticipantDao.kickStudentsByRoomId(roomId);

                dispose();
                new TeacherDashboardFrame(email);
            }
        });

        // Event: Start quiz
        startBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Start the quiz now?", "Confirm Start", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                quizStarted = true;
                startBtn.setEnabled(false);
                
                RoomDao.markRoomAsStarted(roomId);

                JOptionPane.showMessageDialog(this, "Quiz has started. Timer running.");
                startCountdown(allowedTimeSec, countdownLabel, email, roomId);
            }
        });

        setVisible(true);
    }

    private void startCountdown(int seconds, JLabel countdownLabel, String email, int roomId) {
        final int[] timeLeft = {seconds};
        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    timeLeft[0]--;
                    countdownLabel.setText("Time Remaining: " + formatTime(timeLeft[0]));
                    if (timeLeft[0] <= 0) {
                        stopAllTimers();
                        RoomDao.markRoomInactiveByRoomId(roomId);
                        JOptionPane.showMessageDialog(null, "Time's up! Quiz session ended.");
                        dispose();
                        new TeacherResultFrame(email); // or TeacherResultFrame if you want a custom one
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopAllTimers() {
        if (studentTimer != null) studentTimer.cancel();
        if (countdownTimer != null) countdownTimer.cancel();
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
