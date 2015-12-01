package ru.spbau.mit;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


public class SumTwoNumbersGame implements Game {

    final private Random random = new Random();
    private enum State {
        RUN, NOT_CONSTRUCTED
    }

    private int x;
    private int y;
    final private ReentrantLock lock = new ReentrantLock();
    private State state = State.NOT_CONSTRUCTED;
    protected GameServer gameServer;

    public SumTwoNumbersGame(GameServer server) {
        x = java.lang.Math.abs(random.nextInt());
        y = java.lang.Math.abs(random.nextInt());
        gameServer = server;
        state = State.RUN;
    }

    @Override
    public void onPlayerConnected(String id) {
        while (state == State.NOT_CONSTRUCTED) {}
        gameServer.sendTo(id, Integer.toString(x) + " " + Integer.toString(y));
    }

    @Override
    public void onPlayerSentMsg(String id, String msg) {
        lock.lock();
        while (state == State.NOT_CONSTRUCTED) {}
        try {
            int idInt;
            try {
                idInt = Integer.parseInt(msg);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new UnsupportedOperationException("TODO: implement");
            }
            if (idInt == x + y) {
                assert (gameServer != null);
                gameServer.sendTo(id, "Right");
                gameServer.broadcast(id + "won");
                assert (random != null);
                x = java.lang.Math.abs(random.nextInt());
                y = java.lang.Math.abs(random.nextInt());
            } else {
                gameServer.sendTo(id, "Wrong");
            }
        } finally {
            lock.unlock();
        }
    }
}
