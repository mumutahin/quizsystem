package com.quizapp.model;

import java.util.List;

public class Question {
    private int id;
    private String text;
    private List<String> options;
    private List<Integer> correctIndices;
    private String explanation;

    public Question(String text, List<String> options, List<Integer> correctIndices, String explanation) {
        this.text = text;
        this.options = options;
        this.correctIndices = correctIndices;
        this.explanation = explanation;
    }

    public Question(int id, String text, List<String> options, List<Integer> correctIndices, String explanation) {
        this.id = id;
        this.text = text;
        this.options = options;
        this.correctIndices = correctIndices;
        this.explanation = explanation;
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public List<Integer> getCorrectIndices() { return correctIndices; }
    public String getExplanation() { return explanation; }

    public void setId(int id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setOptions(List<String> options) { this.options = options; }
    public void setCorrectIndices(List<Integer> correctIndices) { this.correctIndices = correctIndices; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
