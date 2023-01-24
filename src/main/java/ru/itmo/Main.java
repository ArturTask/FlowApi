package ru.itmo;

import lombok.extern.slf4j.Slf4j;
import ru.itmo.EasyFlow.Publisher;
import ru.itmo.EasyFlow.Subscriber;
import ru.itmo.EasyFlow.Topic;
import ru.itmo.FlowImpl.FlowPublisher;
import ru.itmo.FlowImpl.FlowSubscriber;
import ru.itmo.MyFlow.HardFlowPublisher;
import ru.itmo.MyFlow.HardFlowSubscriber;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class Main {


    public static void main(String[] args) throws InterruptedException {

// Flow api:
//        trySimpleFlowApiImpl();
//        tryCustomizableFlowApi();
//        tryMainFlowApi();



        Thread.sleep(100 * 1000);
    }

    private static void tryMainFlowApi() {
        List<Topic> topics = List.of(new Topic("LOL"), new Topic("KEKE"),
                new Topic("BANANA"), new Topic("MELON"), new Topic("POOP"));

        List<Publisher> publishers = List.of(new Publisher(topics.get(0)), new Publisher(topics.get(1)),
                new Publisher(topics.get(2)), new Publisher(topics.get(3)), new Publisher(topics.get(4)));

        List<Subscriber> subscribers = new LinkedList<>();

        for (int i = 0; i< 1000; i++){
            Publisher randomPublisher = publishers.get((int) (Math.random() * 5));
            Subscriber subscriber = new Subscriber(String.valueOf(i), randomPublisher.getTopic());
            randomPublisher.subscribe(subscriber);
            subscribers.add(subscriber);
        }

        int index = 0;
        Publisher publisher = publishers.get(index);
        publisher.tryBufferOnBackpressure(topics.get(index));

        // drop on pressure
        for (int i = 0; i < 500; i++){
            Topic topic = new Topic(String.valueOf(i));
            publisher.tryDropOnBackpressure(topic, 1, TimeUnit.NANOSECONDS, (s, t) -> {
                s.onError(new Exception("Can't handle backpressure... Dropping value " + t.getName()));
                return true;
            });

        }

        // try to buffer
        for (int i = 0; i < 500; i++){
            Topic topic = new Topic(String.valueOf(i));
            publisher.tryBufferOnBackpressure(topic);

        }
    }

    private static void trySimpleFlowApiImpl() {
        FlowPublisher<Integer> myPublisher = new FlowPublisher<>();

        FlowSubscriber<Integer> subscriber = new FlowSubscriber<>("A");
        myPublisher.subscribe(subscriber);
    }

    private static void tryCustomizableFlowApi() {
        HardFlowPublisher<Integer> hardFlowPublisher = new HardFlowPublisher<Integer>();
        hardFlowPublisher.doOnNext(integer -> {
            if(integer!=null) {

                if (Math.random()<0.5){
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    int some = (int) (integer * Math.random()*100);
                    log.info("WOW now its " + some);
                }
            }
        });
        HardFlowSubscriber<Integer> hardFlowSubscriber = new HardFlowSubscriber<>("AAAAA");
        HardFlowSubscriber<Integer> hardFlowSubscriber2 = new HardFlowSubscriber<>("bBB");

        ConcurrentLinkedQueue<Integer> tasks = new ConcurrentLinkedQueue<>();
        tasks.add(1);
        tasks.add(2);
        tasks.add(3);
        tasks.add(4);
        tasks.add(5);
        hardFlowPublisher.setTasks(tasks);
        hardFlowPublisher.subscribe(hardFlowSubscriber);
        hardFlowPublisher.subscribe(hardFlowSubscriber2);
    }

}
