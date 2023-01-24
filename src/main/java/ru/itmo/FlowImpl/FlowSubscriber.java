package ru.itmo.FlowImpl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

@Slf4j
public class FlowSubscriber<T> implements Flow.Subscriber<T> {

    private String name;
    private Flow.Subscription subscription;
    private int count;

    public FlowSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        log.info(this.name + " Subscribed");
        this.subscription = subscription;

        count = 3;
        requestItems(count);
    }

    // randomly continue or cancel 50%
    @Override
    public void onNext(T item) {
        if (item != null) {

            synchronized (this) {
                // emulation of work
                log.info(this.name + " is pretending to work with " + item.toString());
                count--;

                // randomly continue
                if (count == 0) {
                    this.onComplete();
                }
            }
        } else {
            log.info("Null Item!");
        }
    }

    public void doOnNext(Consumer<T> consumer, T item){
        consumer.accept(item);
    }

    @Override
    public void onComplete() {
        log.info("Complete! ");
        boolean b = Math.random()<0.5;
        if (b) {
            log.info(this.name + " Wants more");
            count = 3;
            requestItems(count);
        } else {
            log.info(this.name + " is Done for today ...");
            count = 0;
            log.info("Cancelling subscription...");
            subscription.cancel();
        }
    }

    @Override
    public void onError(Throwable t) {
        log.error("Subscriber Error: ", t);
    }

    // request some items
    private void requestItems(int n) {
        log.info(this.name + " is Requesting " + n +  " new items...");
        subscription.request(n);
    }

}
