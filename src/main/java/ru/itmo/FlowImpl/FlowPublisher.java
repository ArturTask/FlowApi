package ru.itmo.FlowImpl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


@Getter
@Slf4j
public class FlowPublisher implements Flow.Publisher<Object> {

    @Getter(value = AccessLevel.NONE)
    private ExecutorService executor;
    private List<Flow.Subscription> subscriptions = Collections.synchronizedList(new LinkedList<>());

    public FlowPublisher(ExecutorService executor) {
        this.executor = executor;
    }

    // default is 5 threads
    public FlowPublisher() {
        this.executor = Executors.newFixedThreadPool(5);
    }

    /**
     * Adds the given Subscriber if possible.  If already
     * subscribed, or the attempt to subscribe fails due to policy
     * violations or errors, the Subscriber's {@code onError}
     * method is invoked with an {@link IllegalStateException}.
     * Otherwise, the Subscriber's {@code onSubscribe} method is
     * invoked with a new {@link Flow.Subscription}.  Subscribers may
     * enable receiving items by invoking the {@code request}
     * method of this Subscription, and may unsubscribe by
     * invoking its {@code cancel} method.
     *
     * @param subscriber the subscriber
     * @throws NullPointerException if subscriber is null
     */

    public void subscribe(Flow.Subscriber subscriber) throws NullPointerException, IllegalStateException {
        if (subscriber==null){
            throw new NullPointerException();
        }

        try {
            FlowSubscription subscription = new FlowSubscription(subscriber, executor);
            subscriptions.add(subscription);
            subscriber.onSubscribe(subscription);
        }catch (IllegalStateException e){
            subscriber.onError(e);
        }
    }

//    public void doOnNext(Consumer<Flow.Subscriber<Integer>> consumer){
//        for (Flow.Subscription s: this.subscriptions) {
//            FlowSubscription currSubscriprion = (FlowSubscription) s;
//            FlowSubscriber<Integer> subscriber = (FlowSubscriber<Integer>) currSubscriprion.getSubscriber();
//            subscriber.doOnNext();
////            consumer.accept();
//        }
//    }

    @Getter
    private class FlowSubscription implements Flow.Subscription {

        private final ExecutorService executor;
        private final Flow.Subscriber<Integer> subscriber;
        private final AtomicInteger value = new AtomicInteger(22);
        private AtomicBoolean isCanceled = new AtomicBoolean(false);

        public FlowSubscription(Flow.Subscriber<Integer> subscriber, ExecutorService executor) {
            this.subscriber = subscriber;
            this.executor = executor;

            isCanceled = new AtomicBoolean(false);
        }

        /**
         * Adds the given number {@code n} of items to the current
         * unfulfilled demand for this subscription.  If {@code n} is
         * less than or equal to zero, the Subscriber will receive an
         * {@code onError} signal with an {@link
         * IllegalArgumentException} argument.  Otherwise, the
         * Subscriber will receive up to {@code n} additional {@code
         * onNext} invocations (or fewer if terminated).
         *
         * @param n the increment of demand; a value of {@code
         *          Long.MAX_VALUE} may be considered as effectively unbounded
         */
        @Override
        public void request(long n) {
            if (isCanceled.get())
                return;

            if (n < 0)
                executor.execute(() -> subscriber.onError(new IllegalArgumentException()));
            else
                createItems(n);
        }

        /**
         * Causes the Subscriber to (eventually) stop receiving
         * messages.  Implementation is best-effort -- additional
         * messages may be received after invoking this method.
         * A cancelled subscription need not ever receive an
         * {@code onComplete} or {@code onError} signal.
         */
        @Override
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

        private void createItems(long n) {
            for (int i = 0; i < n; i++) {
                executor.execute(() -> {
                    int v = value.incrementAndGet();
                    log.info("Creating item: " + v + " ...");
                    subscriber.onNext(v);
                });
            }
        }

        private void shutdown() {
            log.info("Shut down executor...");
            executor.shutdown();
        }
    }
}
