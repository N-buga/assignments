package ru.spbau.mit;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

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
        NOT_RUN, RUN
    }

    final private ArrayList<QuizTask> listQuestions = new ArrayList<>();

    private Timer myTymer;
    private State state = State.NOT_RUN;
    private String curQuestion;
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

    private synchronized void sendNewLetter() {
        if (state == State.NOT_RUN) {
            return;
        }
        if (curLetterNumber == maxCountLetter) {
            gameServer.broadcast("Nobody guessed, the word was " + curAnswer);
            myTymer.cancel();
            state = State.NOT_RUN;
            startGame();

        } else {
            gameServer.broadcast("Current prefix is " + curAnswer.substring(0, ++curLetterNumber));
            myTymer.schedule(createTaskNewLetter(), delay);
        }
    }

    public void setDelayUntilNextLetter(int delay_) {
        delay = delay_;
    }

    public void setMaxLettersToOpen(int maxCountLetter_) {
        maxCountLetter = maxCountLetter_;
    }

    public void setDictionaryFilename(String dictionaryFilename) throws FileNotFoundException {
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
        curQuestion = listQuestions.get(numberOfQuestion).question;
        curAnswer = listQuestions.get(numberOfQuestion++).answer;
        curLetterNumber = 0;
        gameServer.broadcast("New round started: " + curQuestion);
        myTymer = new Timer();
        myTymer.schedule(createTaskNewLetter(), delay);
    }

    @Override
    public synchronized void onPlayerConnected(String id) {
        if (state == State.RUN) {
            gameServer.sendTo(id, curQuestion);
            if (curLetterNumber != 0) {
                gameServer.sendTo(id, curAnswer.substring(0, curLetterNumber));
            }
        }
    }

    @Override
    public synchronized void onPlayerSentMsg(String id, String msg) {
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
    }
}
