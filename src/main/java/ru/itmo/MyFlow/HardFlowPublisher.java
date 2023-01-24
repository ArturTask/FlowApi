package ru.itmo.MyFlow;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


@Getter
@Slf4j
public class HardFlowPublisher<T> {


    private final SubmissionPublisher<T> submissionPublisher = new SubmissionPublisher<>();
    Consumer<T> consumer;
    @Getter(value = AccessLevel.NONE)

    ExecutorService executor = Executors.newFixedThreadPool(5);
    private Queue<T> tasks = new ConcurrentLinkedQueue<>();
    private List<FlowSubscription> subscriptions = Collections.synchronizedList(new LinkedList<>());

    public HardFlowPublisher(ExecutorService executor) {
        this.executor = executor;
    }

    // default is 5 threads
    public HardFlowPublisher() {
    }

    public HardFlowPublisher<T> setTasks(Queue<T> tasks) {
        this.tasks = tasks;
        return this;
    }

    public HardFlowPublisher<T> doOnNext(Consumer<T> consumer) {
        this.consumer = consumer;
        return this;
    }

//    public HardFlowPublisher<T> setTimeout(Duration duration){
//        timeout = duration;
//        return this;
//    }

    public void subscribe(HardFlowSubscriber<T> subscriber) throws NullPointerException, IllegalStateException {
        if (subscriber == null) {
            throw new NullPointerException();
        }
        try {
            FlowSubscription subscription = new FlowSubscription(subscriber, executor);
            subscriptions.add(subscription);
            subscriber.onSubscribe(subscription, consumer);
        } catch (IllegalStateException e) {
            subscriber.onError(e);
        }
    }

    @Getter
    public class FlowSubscription {

        private final ExecutorService executor;
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        private final HardFlowSubscriber<T> subscriber;
        private final AtomicInteger value = new AtomicInteger(22);
        private final AtomicBoolean isCanceled;

        public FlowSubscription(HardFlowSubscriber<T> subscriber, ExecutorService executor) {
            this.subscriber = subscriber;
            this.executor = executor;

            isCanceled = new AtomicBoolean(false);
        }


        public void request() {
            if (isCanceled.get())
                return;

//            if (n < 0)
//                executor.execute(() -> subscriber.onError(new IllegalArgumentException()));

            while (!tasks.isEmpty()) {
                executor.execute(() -> {
                    T task;
                    task = tasks.poll();
                    if (task != null) {
                        log.info("Processing item: " + task.toString() + " ...");
                        subscriber.onNext(task);
                    }
//                    else {
//                        log.info("Item is null: " + null + " ...");
//                    }
                });
            }
            subscriber.onComplete();

            synchronized (tasks) {
                if (tasks.size() == 0)
                    shutdown();
            }

        }


        public void cancel() {
            isCanceled.set(true);

            synchronized (subscriptions) {
                // remove this
                subscriptions.remove(this);
                // check if there are no subs -> finish
                if (subscriptions.size() == 0)
                    shutdown();
            }
        }


        private void shutdown() {
            log.info("Shut down executor...");
            executor.shutdown();
            scheduler.shutdown();
        }
    }
}
