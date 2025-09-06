package com.quizapp.model;

public class Result {
    private String quizCode;
    private int score, rank, timeTaken;
    private String studentEmail;
    private String takenAt;

    public Result(String quizCode, int score, int rank, int timeTaken) {
        this.quizCode = quizCode;
        this.score = score;
        this.rank = rank;
        this.timeTaken = timeTaken;
    }

    public Result(String studentEmail, String quizCode, int score, int timeTaken, int rank, String takenAt) {
        this.studentEmail = studentEmail;
        this.quizCode = quizCode;
        this.score = score;
        this.rank = rank;
        this.timeTaken = timeTaken;
        this.takenAt = takenAt;
    }

    public String getQuizCode() { return quizCode; }
    public int getScore() { return score; }
    public int getRank() { return rank; }
    public int getTimeTaken() { return timeTaken; }
    public String getStudentEmail() { return studentEmail; }
    public String getTakenAt() { return takenAt; }
}
