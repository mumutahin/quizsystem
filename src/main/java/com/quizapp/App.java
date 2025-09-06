package com.quizapp;

import com.quizapp.gui.general.InitialFrame;
import com.quizapp.dao.*;

public class App {
    public static void main(String[] args) {
        UserDao.createUserTable();
        QuizDao.createQuizTable();
        QuestionDao.createTable();
        RoomDao.createTable();
        ResultDao.createResultsTable();
        ParticipantDao.createParticipantTable();
        new InitialFrame();
    }
}
