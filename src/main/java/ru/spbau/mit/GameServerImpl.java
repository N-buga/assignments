package ru.spbau.mit;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import com.sun.xml.internal.ws.wsdl.writer.document.ParamType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


public class GameServerImpl implements GameServer {

    private int maxNumber = 0;
    private Map<Connection, Integer> connectionIntegerMap = new HashMap<>();
    private Map<Integer, Connection> integerConnectionMap = new HashMap<>();
    private Game game;
    private Map<Integer, Queue<String>> messageQueue = new HashMap<>();

    private String getNameMethodByKey(String key) {
        return "set" + key;
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
            if (sValue.matches("[-+]?\\d+")) {
                curMethod = gameClass.getMethod(sMethod, new Class[] { int.class });
                curMethod.invoke(game, Integer.parseInt(sValue));
            } else {
                curMethod = gameClass.getMethod(sMethod, new Class[] {String.class});
                curMethod.invoke(game, sValue);
            }
        }
    }

    @Override
    public void accept(final Connection connection) {
        integerConnectionMap.put(maxNumber, connection);
        connectionIntegerMap.put(connection, maxNumber);
        messageQueue.put(maxNumber, new LinkedList<String>());
        connection.send(Integer.toString(maxNumber));
        Thread t = new Thread(new Runnable() {
            final int curID = maxNumber;
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        synchronized (connection) {
                            if (connection.isClosed())
                                return;
                            while (!Thread.interrupted() && !connection.isClosed()) {
                                connection.wait(100);
                                while (!Thread.interrupted() && !connection.isClosed() && !messageQueue.get(curID).isEmpty()) {
                                    connection.send(messageQueue.get(curID).poll());
                                }
                                if (connection.isClosed() || Thread.interrupted()) return;
                                String msg = connection.receive(100);
                                if (connection.isClosed() || Thread.interrupted()) return;
                                if (msg != null) {
                                    game.onPlayerSentMsg(Integer.toString(curID), msg);
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
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
