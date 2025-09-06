package com.quizapp.gui.teacher;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.quizapp.dao.QuizDao;
import com.quizapp.dao.RoomDao;
import com.quizapp.dao.QuestionDao;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;

public class AddQuizQuestionsFrame extends JFrame {
    private JTextField titleField, classField, sectionField;
    private JPanel questionsPanel;
    private List<QuestionPanel> questionPanels = new ArrayList<>();
    private JButton saveBtn;

    public AddQuizQuestionsFrame(String email, Quiz existingQuiz) {
        setTitle("Add Quiz Questions");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel quizInfoPanel = new JPanel(new GridLayout(3, 2));
        titleField = new JTextField();
        classField = new JTextField();
        sectionField = new JTextField();
        quizInfoPanel.add(new JLabel("Quiz Title:"));
        quizInfoPanel.add(titleField);
        quizInfoPanel.add(new JLabel("Class:"));
        quizInfoPanel.add(classField);
        quizInfoPanel.add(new JLabel("Section:"));
        quizInfoPanel.add(sectionField);
        add(quizInfoPanel, BorderLayout.NORTH);

        // Dynamic questions panel
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));

        // Ensure scroll works properly
        JPanel scrollableContainer = new JPanel(new BorderLayout());
        scrollableContainer.add(questionsPanel, BorderLayout.NORTH); // Important: Use NORTH

        JScrollPane scrollPane = new JScrollPane(scrollableContainer);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smoother scrolling
        add(scrollPane, BorderLayout.CENTER);


        // Add first question panel
        addNewQuestionPanel();

        // Save Button
        saveBtn = new JButton("Save All");
        // Panel with Save and Back buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveBtn = new JButton("Save All");
        JButton backBtn = new JButton("Back");

        bottomPanel.add(backBtn);
        bottomPanel.add(saveBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        if (existingQuiz != null) {
            titleField.setText(existingQuiz.getTitle());
            classField.setText(existingQuiz.getClazz());
            sectionField.setText(existingQuiz.getSection());

            List<Question> existingQuestions = QuestionDao.getQuestionsByQuizId(existingQuiz.getId());

            questionsPanel.removeAll();
            questionPanels.clear();

            if (existingQuestions.isEmpty()) {
                System.out.println("No questions found. Adding a blank question panel.");
                addNewQuestionPanel();  // Fallback to empty panel
            } else {
                for (Question q : existingQuestions) {
                    addNewQuestionPanel(q);
                }
            }
        }

        saveBtn.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a quiz title.");
                return;
            }

            List<Question> questionList = new ArrayList<>();
            boolean allValid = true;
            boolean emptyQuestionFound = false;

            // Only consider non-last panels for mandatory question text
            for (int i = 0; i < questionPanels.size(); i++) {
                QuestionPanel qp = questionPanels.get(i);
                String questionText = qp.getQuestionText().trim();

                boolean isLast = (i == questionPanels.size() - 1);
                boolean hasContent = !questionText.isEmpty() || qp.hasAtLeastTwoOptions() || qp.hasCorrectAnswerSelected();

                if (!isLast && questionText.isEmpty() && hasContent) {
                    emptyQuestionFound = true;
                    break;
                }

                if (questionText.isEmpty() && !hasContent) {
                    continue; // Skip blank panel
                }

                if (questionText.isEmpty() || !qp.hasAtLeastTwoOptions() || !qp.hasCorrectAnswerSelected()) {
                    allValid = false;
                    break;
                }

                Question q = qp.toQuestion();
                if (q != null) questionList.add(q);
            }

            if (emptyQuestionFound) {
                JOptionPane.showMessageDialog(this,
                    "Every question (except the last) must have a question text.",
                    "Missing Question Text",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!allValid) {
                JOptionPane.showMessageDialog(this,
                    "Each question must have at least two options and one correct answer selected.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (questionList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add at least one valid question.");
                return;
            }

            int quizId;
            if (existingQuiz != null) {
                quizId = existingQuiz.getId();
                if (!questionList.isEmpty()) {
                    QuestionDao.deleteQuestionsByQuizId(quizId);
                } else {
                    JOptionPane.showMessageDialog(this, "You must include at least one question to update the quiz.");
                    return;
                }
            } else {
                String code = QuizDao.generateRandomCode();
                quizId = QuizDao.addQuiz(titleField.getText(), classField.getText(), sectionField.getText(), code, email);

                if (quizId == -1) {
                    JOptionPane.showMessageDialog(this, "Failed to create quiz. The title or code might already exist.");
                    return;
                }

                System.out.println("Successfully created quiz with ID: " + quizId);
                RoomDao.createRoom(quizId, code);
            }

            for (Question q : questionList) {
                QuestionDao.addQuestion(quizId, q);
            }

            JOptionPane.showMessageDialog(this, "Quiz saved successfully.");
            dispose();
            new TeacherDashboardFrame(email);
        });

        backBtn.addActionListener(e -> {
            boolean hasUnsaved = questionPanels.stream().anyMatch(qp -> qp.toQuestion() != null);

            if (hasUnsaved || !titleField.getText().trim().isEmpty() || !classField.getText().trim().isEmpty() || !sectionField.getText().trim().isEmpty()) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "You have unsaved changes. Do you want to save them?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (choice == JOptionPane.CANCEL_OPTION) return;
                if (choice == JOptionPane.YES_OPTION) {
                    saveBtn.doClick(); // Triggers existing save logic
                    return;
                }
                // else: discard
            }

            dispose();
            new TeacherDashboardFrame(email);
        });

        setVisible(true);

    }

    private void addNewQuestionPanel() {
        final QuestionPanel[] qpRef = new QuestionPanel[1]; // Mutable reference

        qpRef[0] = new QuestionPanel(() -> {
            SwingUtilities.invokeLater(() -> {
                QuestionPanel last = questionPanels.isEmpty() ? null : questionPanels.get(questionPanels.size() - 1);

                // Always clean trailing empties first
                removeTrailingEmptyPanels();

                // Refresh the last panel reference (could have changed after removing)
                last = questionPanels.isEmpty() ? null : questionPanels.get(questionPanels.size() - 1);

                // Add a new panel only if last is not null and has meaningful input
                if (last != null && last == qpRef[0] && !qpRef[0].getQuestionText().trim().isEmpty()) {
                    addNewQuestionPanel();
                }
            });
        });

        QuestionPanel qp = qpRef[0];
        questionPanels.add(qp);
        questionsPanel.add(qp);
        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalBar = ((JScrollPane) getContentPane().getComponent(1)).getVerticalScrollBar();
            verticalBar.setValue(verticalBar.getMaximum());
        });
    }

    private void removeTrailingEmptyPanels() {
        // Remove trailing question panels that are completely empty
        int i = questionPanels.size() - 1;

        // Leave at least one panel always
        while (i > 0) {
            QuestionPanel qp = questionPanels.get(i);
            boolean isEmpty = qp.getQuestionText().trim().isEmpty()
                    && !qp.hasAtLeastTwoOptions()
                    && !qp.hasCorrectAnswerSelected();

            if (isEmpty) {
                questionPanels.remove(i);
                questionsPanel.remove(i);
                i--;
            } else {
                break;
            }
        }

        revalidate();
        repaint();
    }

    private void addNewQuestionPanel(Question q) {
        final QuestionPanel[] qpRef = new QuestionPanel[1];

        qpRef[0] = new QuestionPanel(() -> {
            SwingUtilities.invokeLater(() -> {
                QuestionPanel last = questionPanels.isEmpty() ? null : questionPanels.get(questionPanels.size() - 1);

                removeTrailingEmptyPanels();
                last = questionPanels.isEmpty() ? null : questionPanels.get(questionPanels.size() - 1);

                if (last != null && last == qpRef[0] && !qpRef[0].getQuestionText().trim().isEmpty()) {
                    addNewQuestionPanel();
                }
            });
        });

        QuestionPanel qp = qpRef[0];
        qp.loadData(q);
        questionPanels.add(qp);
        questionsPanel.add(qp);
        revalidate();
        repaint();
    }

}