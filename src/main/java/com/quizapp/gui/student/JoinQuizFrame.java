package com.quizapp.gui.student;

import com.quizapp.dao.ResultDao;
import com.quizapp.dao.RoomDao;
import com.quizapp.dao.ParticipantDao;
import com.quizapp.dao.QuizDao;
import com.quizapp.model.Question;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JoinQuizFrame extends JFrame {

    private final String studentEmail;
    private List<Question> questions;
    private int score = 0;
    private long startTime;
    private String quizCode;
    private int allowedTimeSec;
    private boolean quizSubmitted = false;
    private boolean resultSaved = false;
    private javax.swing.Timer countdownTimer;

    public JoinQuizFrame(String email) {
        this.studentEmail = email;
        setTitle("Join Quiz");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleWindowClose();
            }
        });
        showCodeInputPanel();
        setVisible(true);
    }

    private void leaveRoom() {
        if (quizCode != null) {
            Integer roomId = RoomDao.getRoomIdByCode(quizCode);
            if (roomId != null) {
                ParticipantDao.removeStudentFromRoom(studentEmail, roomId);
            }
        }
    }

    private void handleWindowClose() {
        // If quiz is running but not completed or timed out, do not save
        leaveRoom();

        dispose(); // Close window
        System.exit(0); // Kill app to simulate full quit
    }

    private void showCodeInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Enter Quiz Key:");
        JTextField codeField = new JTextField(10);
        JButton joinBtn = new JButton("Join");

        joinBtn.addActionListener(e -> {
            quizCode = codeField.getText().trim();
            boolean active = QuizDao.isQuizActive(quizCode);
            if (!active) {
                JOptionPane.showMessageDialog(this, "Invalid or inactive quiz code.");
                return;
            }

            Integer roomId = RoomDao.getRoomIdByCode(quizCode);
            if (roomId == null) {
                JOptionPane.showMessageDialog(this, "Could not join: Room not found.");
                return;
            }
            ParticipantDao.markStudentJoined(studentEmail, roomId);

            JOptionPane.showMessageDialog(this, "You’ve joined the quiz. Waiting for the teacher to start...");

            showWaitingPanel();
        });


        JPanel form = new JPanel();
        form.add(label);
        form.add(codeField);
        form.add(joinBtn);

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            dispose();
            new StudentDashboardFrame(studentEmail);
        });
        form.add(backBtn);
        panel.add(form, BorderLayout.CENTER);
        setContentPane(panel);
        revalidate();
    }

    private void showWaitingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Waiting for the teacher to start the quiz...", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(label, BorderLayout.CENTER);
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            leaveRoom();
            dispose();
            new StudentDashboardFrame(studentEmail);
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(panel);
        revalidate();

        new java.util.Timer().scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                boolean started = QuizDao.hasQuizStarted(quizCode);
                boolean active = QuizDao.isQuizActive(quizCode);

                if (started) {
                    QuizDao.QuizSessionInfo session = QuizDao.getQuestionsForCode(quizCode);

                    if (session == null) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(JoinQuizFrame.this, "Error: Quiz session data not found. Please contact your teacher.");
                            dispose();
                            new StudentDashboardFrame(studentEmail);
                        });
                        this.cancel();
                        return;
                    }

                    if (session.questions == null || session.questions.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(JoinQuizFrame.this, "Quiz session has no questions.");
                            dispose();
                            new StudentDashboardFrame(studentEmail);
                        });
                        this.cancel();
                        return;
                    }

                    questions = session.questions;
                    allowedTimeSec = session.timeLimit;

                    SwingUtilities.invokeLater(() -> {
                        startTime = System.currentTimeMillis();
                        showFullQuizPanel();

                        javax.swing.Timer quizTimer = new javax.swing.Timer(allowedTimeSec * 1000, e -> {
                            int durationSec = (int)((System.currentTimeMillis() - startTime) / 1000);
                            ResultDao.saveStudentResult(studentEmail, quizCode, score, durationSec);
                            JOptionPane.showMessageDialog(JoinQuizFrame.this, "Time’s up! Your quiz has ended. Score: " + score);
                            dispose();
                            new StudentResultFrame(studentEmail, quizCode); // Show result screen
                        });
                        quizTimer.setRepeats(false);
                        quizTimer.start();
                    });
                    this.cancel();
                } else if (!active && !started) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(JoinQuizFrame.this, "The quiz session has ended or was cancelled.");
                        dispose();
                        new StudentDashboardFrame(studentEmail);
                    });
                    this.cancel();
                }
            }
        }, 0, 2000);
    }

    private void showFullQuizPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // === Top: Timer label ===
        JLabel timerLabel = new JLabel("Time Left: " + formatTime(allowedTimeSec), SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(timerLabel, BorderLayout.NORTH);

        // === Center: Scrollable list of questions ===
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));

        ButtonGroup[] buttonGroups = new ButtonGroup[questions.size()];
        JRadioButton[][] optionButtons = new JRadioButton[questions.size()][];

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);

            JPanel qPanel = new JPanel();
            qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
            qPanel.setBorder(BorderFactory.createTitledBorder("Q" + (i + 1) + ". " + q.getText()));

            List<String> options = q.getOptions();
            optionButtons[i] = new JRadioButton[options.size()];
            buttonGroups[i] = new ButtonGroup();

            for (int j = 0; j < options.size(); j++) {
                optionButtons[i][j] = new JRadioButton(options.get(j));
                buttonGroups[i].add(optionButtons[i][j]);
                qPanel.add(optionButtons[i][j]);
            }

            questionsPanel.add(qPanel);
        }

        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // === Bottom: Submit and Quit buttons ===
        JButton submitBtn = new JButton("Submit");
        JButton quitBtn = new JButton("Quit");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(quitBtn);
        bottomPanel.add(submitBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // === Action: Submit ===
        submitBtn.addActionListener(e -> {
            if (quizSubmitted) return;
            quizSubmitted = true;

            if (countdownTimer != null) countdownTimer.stop();

            score = 0;
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                for (int j = 0; j < optionButtons[i].length; j++) {
                    if (optionButtons[i][j].isSelected() && q.getCorrectIndices().contains(j)) {
                        score++;
                    }
                }
            }

            int durationSec = (int)((System.currentTimeMillis() - startTime) / 1000);
            saveResultOnce(score, durationSec, "Manual submit");
            JOptionPane.showMessageDialog(this, "Quiz submitted! Your score: " + score);
            dispose();
            new StudentResultFrame(studentEmail, quizCode);

        });

        // === Action: Quit early ===
        quitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit? Your progress won't be saved.", "Confirm Quit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                leaveRoom();
                dispose();
                new StudentDashboardFrame(studentEmail);
            }
        });

        setContentPane(mainPanel);
        revalidate();

        // === Timer countdown ===
        countdownTimer = new javax.swing.Timer(1000, e -> {
            allowedTimeSec--;
            timerLabel.setText("Time Left: " + formatTime(allowedTimeSec));

            if (!quizSubmitted && allowedTimeSec <= 0) {
                quizSubmitted = true;
                countdownTimer.stop();
                int durationSec = (int)((System.currentTimeMillis() - startTime) / 1000);
                int finalScore = 0;
                for (int i = 0; i < questions.size(); i++) {
                    Question q = questions.get(i);
                    for (int j = 0; j < optionButtons[i].length; j++) {
                        if (optionButtons[i][j].isSelected() && q.getCorrectIndices().contains(j)) {
                            finalScore++;
                        }
                    }
                }

                saveResultOnce(finalScore, durationSec, "Timer expired");

                // Only show message if the user didn’t already submit
                if (!quizSubmitted) {
                    JOptionPane.showMessageDialog(this, "Time's up! Quiz auto-submitted.\nYour score: " + finalScore);
                    dispose();
                    new StudentResultFrame(studentEmail, quizCode);
                }
            }

        });

        countdownTimer.start();
    }

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void saveResultOnce(int finalScore, int durationSec, String reason) {
        if (resultSaved) return;
        resultSaved = true;
        ResultDao.saveStudentResult(studentEmail, quizCode, finalScore, durationSec);
        System.out.println(reason + ": Result saved for " + studentEmail + ", Score: " + finalScore);
    }

}
