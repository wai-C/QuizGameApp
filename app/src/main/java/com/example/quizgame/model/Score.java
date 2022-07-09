package com.example.quizgame.model;

public class Score {
    int correct;
    int wrong;

    public Score() {
    }
    public Score(int correct, int wrong) {
        this.correct = correct;
        this.wrong = wrong;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }
}
