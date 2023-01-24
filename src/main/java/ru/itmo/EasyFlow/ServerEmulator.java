package ru.itmo.EasyFlow;

import java.util.Hashtable;
import java.util.List;

public class ServerEmulator {
    private Hashtable<Topic, List<Subscriber>> subscriberLists;

    private static ServerEmulator serverInstance;

    public static ServerEmulator getInstance() {
        if (serverInstance == null) {
            serverInstance = new ServerEmulator();
        }
        return serverInstance;
    }

    private ServerEmulator() {
        this.subscriberLists = new Hashtable<>();
    }

    public void sendMessage(Topic t, Message m) {
        List<Subscriber> subs = subscriberLists.get(t);
        for (Subscriber s : subs) {
            s.receivedMessage(t, m);
        }
    }

    public void registerSubscriber(Subscriber s, Topic t) {
        subscriberLists.get(t).add(s);
    }
}
