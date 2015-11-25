package ru.spbau.mit;

import java.util.*;


public class SumTwoNumbersGame implements Game {
    private int x;
    private int y;
    private Random random = new Random();
    protected GameServer gameServer;

    public SumTwoNumbersGame(GameServer server) {
        x = java.lang.Math.abs(random.nextInt());
        y = java.lang.Math.abs(random.nextInt());
        gameServer = server;
    }

    @Override
    public synchronized void onPlayerConnected(String id) {
        gameServer.sendTo(id, Integer.toString(x) + " " + Integer.toString(y));
    }

    @Override
    public synchronized void onPlayerSentMsg(String id, String msg) {
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
    }
}
