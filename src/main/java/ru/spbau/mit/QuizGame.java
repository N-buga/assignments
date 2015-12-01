package ru.spbau.mit;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


public class QuizGame implements Game {

    private class QuizTask {
        String question;
        String answer;
        QuizTask(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }

    private enum State {
        NOT_RUN, RUN, NOT_CONSTRUCTED
    }

    final private ArrayList<QuizTask> listQuestions = new ArrayList<>();
    final private ReentrantLock lock = new ReentrantLock();

    private Timer myTymer;
    private State state = State.NOT_CONSTRUCTED;
    private String curAnswer;
    private GameServer gameServer;
    private int delay;
    private int numberOfQuestion = 0;
    private int maxCountLetter;
    private int curLetterNumber = 0;

    public QuizGame(GameServer server) {
        gameServer = server;
        state = State.NOT_RUN;
    }

    private TimerTask createTaskNewLetter() {
        return new TimerTask() {
            @Override
            public void run() {
                sendNewLetter();
            }
        };
    }

    private void sendNewLetter() {
        if (state == State.NOT_RUN) {
            return;
        }
        lock.lock();
        try {
            if (curLetterNumber == maxCountLetter) {
                gameServer.broadcast("Nobody guessed, the word was " + curAnswer);
                myTymer.cancel();
                state = State.NOT_RUN;
                startGame();

            } else {
                gameServer.broadcast("Current prefix is " + curAnswer.substring(0, ++curLetterNumber));
                TimerTask nextLetter = new TimerTask() {
                    @Override
                    public void run() {
                        sendNewLetter();
                    }
                };
                myTymer.schedule(createTaskNewLetter(), delay);
            }
        } finally {
            lock.unlock();
        }
    }

    public void setDelayUntilNextLetter(int delay_) {
        while (state == State.NOT_CONSTRUCTED) {}
        delay = delay_;
    }

    public void setMaxLettersToOpen(int maxCountLetter_) {
        while (state == State.NOT_CONSTRUCTED) {}
        maxCountLetter = maxCountLetter_;
    }

    public void setDictionaryFilename(String dictionaryFilename) throws FileNotFoundException {
        while (state == State.NOT_CONSTRUCTED) {}
        Scanner myfile = new Scanner(new File(dictionaryFilename));
        String curString;
        String[] arrayString;
        while (myfile.hasNext()) {
            curString = myfile.nextLine();
            arrayString = curString.split(";");
            listQuestions.add(new QuizTask(arrayString[0] + " (" + arrayString[1].length() + " letters)", arrayString[1]));
        }
    }

    private void startGame() {
        if (numberOfQuestion == listQuestions.size())
            numberOfQuestion = 0;
        String curQuestion = listQuestions.get(numberOfQuestion).question;
        curAnswer = listQuestions.get(numberOfQuestion++).answer;
        gameServer.broadcast("New round started: " + curQuestion);
        myTymer = new Timer();
        myTymer.schedule(createTaskNewLetter(), delay);
    }

    @Override
    public void onPlayerConnected(String id) {
    }

    @Override
    public void onPlayerSentMsg(String id, String msg) {
        lock.lock();
        try {
            if (state == State.NOT_RUN) {
                if (msg.equals("!start")) {
                    startGame();
                    state = State.RUN;
                }
            } else if (state == State.RUN) {
                if (msg.equals("!stop")) {
                    myTymer.cancel();
                    state = State.NOT_RUN;
                    gameServer.broadcast("Game has been stopped by " + id);
                } else {
                    if (msg.equals(curAnswer)) {
                        gameServer.broadcast("The winner is " + id);
                        startGame();
                    } else {
                        gameServer.sendTo(id, "Wrong try");
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
