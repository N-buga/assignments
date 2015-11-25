package ru.spbau.mit;

import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class QuizGame implements Game {

    public enum State {
        NOT_RUN, RUN
    }

    private State state = State.NOT_RUN;
    private String curAnswer;
    private GameServer gameServer;
    private int delay;
    private int numberOfQuestion = 0;
    private int maxCountLetter;
    private Timer myTymer = new Timer();
    private int curLetterNumber = 0;
    private ArrayList<Pair<String, String>> listQuestions = new ArrayList();

    public QuizGame(GameServer server) {
        gameServer = server;
    }

    private class myTimerTask extends TimerTask {
        @Override
        public void run() {
            sendNewLetter();
        }
    }

    private synchronized void sendNewLetter() {
        if (state == State.NOT_RUN) {
            return;
        }
        if (curLetterNumber == maxCountLetter) {
            gameServer.broadcast("Nobody guessed, the word was " + curAnswer);
            startGame();
        } else {
            gameServer.broadcast("Current prefix is " + curAnswer.substring(0, ++curLetterNumber));
            myTymer.schedule(new myTimerTask(), delay);
        }
    }

    public synchronized void setdelayUntilNextLetter(int delay_) {
        delay = delay_;
    }

    public synchronized void setmaxLettersToOpen(int maxCountLetter_) {
        maxCountLetter = maxCountLetter_;
    }

    public synchronized void setdictionaryFilename(String dictionaryFilename) throws FileNotFoundException {
        Scanner myfile = new Scanner(new File(dictionaryFilename));
        String curString;
        String[] arrayString;
        while (myfile.hasNext()) {
            curString = myfile.nextLine();
            arrayString = curString.split(";");
            listQuestions.add(new Pair(arrayString[0] + " (" + arrayString[1].length() + " letters)", arrayString[1]));
        }
    }

    private synchronized void startGame() {
        if (numberOfQuestion == listQuestions.size())
            numberOfQuestion = 0;
        String question = listQuestions.get(numberOfQuestion).getKey();
        curAnswer = listQuestions.get(numberOfQuestion++).getValue();
        gameServer.broadcast("New round started: " + question);
        myTymer.schedule(new myTimerTask(), delay);
    }

    @Override
    public synchronized void onPlayerConnected(String id) {
    }

    @Override
    public synchronized void onPlayerSentMsg(String id, String msg) {
        if (state == State.NOT_RUN) {
            if (msg == "!start") {
                startGame();
                state = State.RUN;
            }
        } else if (state == State.RUN){
            if (msg == "!stop") {
                state = State.NOT_RUN;
                gameServer.broadcast("Game has been stopped by " + id);
            } else {
                if (msg.compareTo(curAnswer) == 0) {
                    gameServer.broadcast("The winner is " + id);
                    startGame();
                } else {
                    gameServer.sendTo(id, "Wrong try");
                }
            }
        }
    }
}
