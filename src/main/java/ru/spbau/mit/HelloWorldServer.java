package ru.spbau.mit;


public class HelloWorldServer implements Server {

    @Override
    public void accept(final Connection connection) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                connection.send("Hello world");
                connection.close();
            }
        });
        t.start();
    }
}
