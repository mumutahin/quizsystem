package com.quizapp.model;

public class Room {
    private int id;
    private int quizId;
    private String code;

    public Room(int id, int quizId, String code) {
        this.id = id;
        this.quizId = quizId;
        this.code = code;
    }

    public int getId() { return id; }
    public int getQuizId() { return quizId; }
    public String getCode() { return code; }

    public void setId(int id) { this.id = id; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public void setCode(String code) { this.code = code; }
}
