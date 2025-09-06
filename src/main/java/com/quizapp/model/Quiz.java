package com.quizapp.model;

import java.util.List;

public class Quiz {
    private int id;
    private String title;
    private String clazz;
    private String section;
    private String quizCode;
    private String teacherEmail;
    private List<Question> questions;

    // Constructor matching the one used in QuizDao
    public Quiz(int id, String title, String clazz, String section, String quizCode, String teacherEmail) {
        this.id = id;
        this.title = title;
        this.clazz = clazz;
        this.section = section;
        this.quizCode = quizCode;
        this.teacherEmail = teacherEmail;
    }

    // Getters and setters (optional but recommended)
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getClazz() {
        return clazz;
    }

    public String getSection() {
        return section;
    }

    public String getQuizCode() {
        return quizCode;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setQuizCode(String quizCode) {
        this.quizCode = quizCode;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}
