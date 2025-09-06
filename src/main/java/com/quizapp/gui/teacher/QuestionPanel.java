package com.quizapp.gui.teacher;

import com.quizapp.model.Question;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionPanel extends JPanel {
    private JTextArea questionArea;
    private JTextArea explanationArea;
    private JPanel choicesPanel;
    private List<JTextField> optionFields = new ArrayList<>();
    private List<JCheckBox> correctBoxes = new ArrayList<>();
    private final Runnable onTypingCallback;
    private JLabel warningLabel;

    public QuestionPanel(Runnable onTypingCallback) {
        this.onTypingCallback = onTypingCallback;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Question"));

        // QUESTION TEXT AREA
        questionArea = new JTextArea(2, 40);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        add(new JLabel("Question:"));
        add(new JScrollPane(questionArea));

        // WARNING LABEL
        warningLabel = new JLabel("At least 2 options and 1 correct answer required.");
        warningLabel.setForeground(Color.RED);
        warningLabel.setVisible(false);
        add(warningLabel);

        // CALLBACK ON QUESTION TYPING
        questionArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { maybeTrigger(); }
            public void removeUpdate(DocumentEvent e) { maybeTrigger(); }
            public void changedUpdate(DocumentEvent e) {}
        });

        // CHOICES PANEL
        choicesPanel = new JPanel();
        choicesPanel.setLayout(new BoxLayout(choicesPanel, BoxLayout.Y_AXIS));
        add(new JLabel("Options:"));
        add(choicesPanel);
        addNewChoiceField();  // Initial field

        // EXPLANATION AREA
        explanationArea = new JTextArea(2, 40);
        explanationArea.setLineWrap(true);
        explanationArea.setWrapStyleWord(true);
        add(new JLabel("Explanation (optional):"));
        add(new JScrollPane(explanationArea));
    }

    // ✅ Adds a new option field
    private void addNewChoiceField() {
        JTextField choiceField = new JTextField(30);
        JCheckBox correctCheck = new JCheckBox("Correct");

        // Listeners for dynamic behavior
        choiceField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                maybeAddOrRemoveFields();
                updateWarningVisibility();
            }

            public void removeUpdate(DocumentEvent e) {
                maybeAddOrRemoveFields();
                updateWarningVisibility();
            }

            public void changedUpdate(DocumentEvent e) {
                updateWarningVisibility();
            }
        });

        correctCheck.addActionListener(e -> updateWarningVisibility());

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(choiceField);
        row.add(correctCheck);

        optionFields.add(choiceField);
        correctBoxes.add(correctCheck);
        choicesPanel.add(row);

        revalidate();
        repaint();
    }

    // ✅ Removes last field if conditions match
    private void maybeAddOrRemoveFields() {
        int size = optionFields.size();
        if (size < 1) return;

        JTextField lastField = optionFields.get(size - 1);
        JTextField secondLastField = (size >= 2) ? optionFields.get(size - 2) : null;

        if (!lastField.getText().trim().isEmpty()) {
            addNewChoiceField();  // Add new field if last one is filled
        } else if (secondLastField != null &&
                   secondLastField.getText().trim().isEmpty() &&
                   size > 1) {
            removeLastChoiceField();  // Remove last field if second last is also empty
        }
    }

    private void removeLastChoiceField() {
        int lastIndex = optionFields.size() - 1;
        optionFields.remove(lastIndex);
        correctBoxes.remove(lastIndex);
        choicesPanel.remove(lastIndex);
        revalidate();
        repaint();
    }

    private void maybeTrigger() {
        SwingUtilities.invokeLater(() -> {
            if (onTypingCallback != null) {
                onTypingCallback.run();
            }
        });
    }

    public Question toQuestion() {
        String text = questionArea.getText().trim();
        if (text.isEmpty()) return null;

        List<String> options = new ArrayList<>();
        List<Integer> correct = new ArrayList<>();

        for (int i = 0; i < optionFields.size(); i++) {
            String opt = optionFields.get(i).getText().trim();
            if (!opt.isEmpty()) {
                options.add(opt);
                if (correctBoxes.get(i).isSelected()) {
                    correct.add(options.size() - 1);  // aligned with options
                }
            }
        }

        if (options.size() < 2 || correct.isEmpty()) return null;

        return new Question(text, options, correct, explanationArea.getText().trim());
    }

    public void loadData(Question q) {
        questionArea.setText(q.getText());
        explanationArea.setText(q.getExplanation());
        choicesPanel.removeAll();
        optionFields.clear();
        correctBoxes.clear();

        for (int i = 0; i < q.getOptions().size(); i++) {
            JTextField field = new JTextField(q.getOptions().get(i), 30);
            JCheckBox chk = new JCheckBox("Correct", q.getCorrectIndices().contains(i));

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(field);
            row.add(chk);
            optionFields.add(field);
            correctBoxes.add(chk);
            choicesPanel.add(row);
        }

        addNewChoiceField();
        updateWarningVisibility();
        revalidate();
        repaint();
    }

    public String getQuestionText() {
        return questionArea.getText();
    }

    public boolean hasAtLeastTwoOptions() {
        int count = 0;
        for (JTextField field : optionFields) {
            if (!field.getText().trim().isEmpty()) {
                count++;
            }
        }
        return count >= 2;
    }

    public boolean hasCorrectAnswerSelected() {
        for (int i = 0; i < correctBoxes.size(); i++) {
            if (correctBoxes.get(i).isSelected() && !optionFields.get(i).getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void updateWarningVisibility() {
        boolean valid = hasAtLeastTwoOptions() && hasCorrectAnswerSelected();
        warningLabel.setVisible(!valid);
    }
}
