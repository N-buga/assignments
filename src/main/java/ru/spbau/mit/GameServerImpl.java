package ru.spbau.mit;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class GameServerImpl implements GameServer {

    private final Map<Integer, ConnectionManager> integerConnectionMap = new HashMap<>();
    ReadWriteLock lock = new ReentrantReadWriteLock();

    private int maxNumber = 0;
    private Game game;

    private static String getNameMethodByKey(String key) {
        return "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
    }

    public GameServerImpl(String gameClassName, Properties properties) throws ClassNotFoundException, NoSuchMethodException,
                                                                            InstantiationException, IllegalAccessException,
                                                                            InvocationTargetException {
        Class gameClass = Class.forName(gameClassName);
        Constructor constructorGameClass = gameClass.getConstructor(GameServer.class);
        game = (Game) constructorGameClass.newInstance(this);
        Set<String> propertiesNames = properties.stringPropertyNames();
        for (String s: propertiesNames) {
            String sMethod = getNameMethodByKey(s);
            String sValue = properties.getProperty(s);
            Method curMethod;
            try {
                Integer.parseInt(sValue);
                curMethod = gameClass.getMethod(sMethod, int.class);
                curMethod.invoke(game, Integer.parseInt(sValue));
            } catch (NumberFormatException e) {
                curMethod = gameClass.getMethod(sMethod, String.class);
                curMethod.invoke(game, sValue);
            }
        }
    }


    private class ConnectionManager implements Runnable {

        final private int curID;
        final private Connection connection;
        final private Queue<String> messageQueue = new LinkedList<>();

        ConnectionManager(int curID, Connection connection) {
                this.curID = curID;
                this.connection = connection;
        }

        private synchronized void addMessage(String msg) {
            messageQueue.add(msg);
        }

        private boolean needClose() {
            return  (Thread.interrupted() || connection.isClosed());
        }

        @Override
        public void run() {
            try {
                while (!needClose()) {
                    synchronized (this) {
                        while (!needClose() && !messageQueue.isEmpty()) {
                            connection.send(messageQueue.poll());
                        }
                    }
                    if (needClose()) return;
                    String msg = connection.receive(100);
                    if (needClose()) return;
                    if (msg != null) {
                        game.onPlayerSentMsg(Integer.toString(curID), msg);
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    @Override
    public void accept(final Connection connection) {
        lock.writeLock().lock();
        try {
            ConnectionManager curConnectionManager = new ConnectionManager(maxNumber, connection);
            integerConnectionMap.put(maxNumber, curConnectionManager);
            curConnectionManager.addMessage(Integer.toString(maxNumber));
            Thread t = new Thread(curConnectionManager);
            game.onPlayerConnected(Integer.toString(maxNumber++));
            t.start();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void broadcast(String message) {
        lock.readLock().lock();
        try {
            for (Integer curID: integerConnectionMap.keySet()) {
                integerConnectionMap.get(curID).addMessage(message);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void sendTo(String id, String message) throws NumberFormatException {
        lock.readLock().lock();
        try {
            int idInt;
            idInt = Integer.parseInt(id);
            integerConnectionMap.get(idInt).addMessage(message);
        }finally {
            lock.readLock().unlock();
        }
    }
}
