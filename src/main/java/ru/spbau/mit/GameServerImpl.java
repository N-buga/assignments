package ru.spbau.mit;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class GameServerImpl implements GameServer {

    final private Map<Connection, Integer> connectionIntegerMap = new HashMap<>();
    final private Map<Integer, Connection> integerConnectionMap = new HashMap<>();
    final private Map<Integer, Queue<String>> messageQueue = new HashMap<>();

    private int maxNumber = 0;
    private Game game;

    static private String getNameMethodByKey(String key) {
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

    static private boolean needClose(Connection connection) {
        return  (Thread.interrupted() || connection.isClosed());
    }

    private class MyRunnableClass implements Runnable {

        final private int curID;
        final private Connection connection;

        MyRunnableClass(int curID, Connection connection) {
            synchronized (connection) {
                this.curID = curID;
                this.connection = connection;
            }
        }

        private void sendAndGetMessage() throws InterruptedException {
            while (true) {
                try {
                    if (needClose(connection))
                        return;
//                    int counter = 0;
                    while (!(needClose(connection))) {
//                        counter++;
//                        if (counter == 100) {
//                            counter = 0;
                        Thread.sleep(0, 40);
                        synchronized (connection) {
                            while (!(needClose(connection)) && !messageQueue.get(curID).isEmpty()) {
                                connection.send(messageQueue.get(curID).poll());
                            }
                        }
                        if (needClose(connection)) return;
                        String msg = connection.receive(100);
                        if (needClose(connection)) return;
                        if (msg != null) {
                            game.onPlayerSentMsg(Integer.toString(curID), msg);
                        }
                    }
                    return;
                } catch (NullPointerException e) {}
            }
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    sendAndGetMessage();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    @Override
    public void accept(final Connection connection) {
        integerConnectionMap.put(maxNumber, connection);
        connectionIntegerMap.put(connection, maxNumber);
        messageQueue.put(maxNumber, new LinkedList<String>());
        connection.send(Integer.toString(maxNumber));
        Thread t = new Thread(new MyRunnableClass(maxNumber, connection));
        t.start();
        game.onPlayerConnected(Integer.toString(maxNumber++));
    }

    @Override
    public void broadcast(String message) {
        for (Connection curConnection: connectionIntegerMap.keySet()) {
            synchronized (curConnection) {
                messageQueue.get(connectionIntegerMap.get(curConnection)).add(message);
                curConnection.notify();
            }
        }
    }

    @Override
    public void sendTo(String id, String message) throws NumberFormatException {
        int idInt;
        idInt = Integer.parseInt(id);

        Connection curConnection = integerConnectionMap.get(idInt);
        synchronized (curConnection) {
            messageQueue.get(idInt).add(message);
            curConnection.notify();
        }
    }
}
