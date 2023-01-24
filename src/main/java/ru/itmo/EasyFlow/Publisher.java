package ru.itmo.EasyFlow;

public class Publisher {

    private final Topic topic;

    public Publisher(Topic t) {
        this.topic = t;
    }

    public void publish(Message m) {
        ServerEmulator.getInstance().sendMessage(this.topic, m);
    }
}
