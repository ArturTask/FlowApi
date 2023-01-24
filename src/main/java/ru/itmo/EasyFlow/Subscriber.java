package ru.itmo.EasyFlow;

public class Subscriber {
    public Subscriber(Topic...topics) {
        for (Topic t : topics) {
            ServerEmulator.getInstance().registerSubscriber(this, t);
        }
    }

    public void receivedMessage(Topic t, Message m) {

    }
}
