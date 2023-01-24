package ru.itmo.MyFlow;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class HardFlowSubscriber<T> {

    private String name;
    private HardFlowPublisher<T>.FlowSubscription subscription;
    private Consumer<T> consumer;

    public void setConsumer(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public HardFlowSubscriber(String name) {
        this.name = name;
    }

    public void onSubscribe(HardFlowPublisher<T>.FlowSubscription subscription, Consumer<T> consumer) {
        log.info(this.name + " Subscribed");
        setConsumer(consumer);
        this.subscription = subscription;
        requestItems();
    }

    public void onNext(T item) {
        if (consumer!=null){
            consumer.accept(item);
        }

    }

    public void onComplete() {
        log.info("Complete! ");
    }

    public void onError(Throwable t) {
        log.error("Subscriber Error: ", t);
    }

    // request some items
    private void requestItems() {
        log.info(this.name + " is Requesting " +  " new items...");
        subscription.request();
    }

}
