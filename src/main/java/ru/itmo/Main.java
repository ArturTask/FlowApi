package ru.itmo;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import ru.itmo.FlowImpl.FlowPublisher;
import ru.itmo.FlowImpl.FlowSubscriber;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.SubmissionPublisher;

@Slf4j
public class Main {

    private static final CompletableFuture terminated = new CompletableFuture<>();



    public static void main(String[] args) throws InterruptedException {

//        SubmissionPublisher<String> stringSubmissionPublisher = new SubmissionPublisher<>();
//        stringSubmissionPublisher.offer("",(subscriber, s) -> {
//            subscriber.onError(new RuntimeException("String " + s
//                    + " droped because of backpressure"));
//            return true;
//        } );

//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        executor.scheduleWithFixedDelay(() -> {
//
//        }, 1, 1, TimeUnit.MILLISECONDS);


        FlowPublisher flowPublisher = new FlowPublisher();



        FlowSubscriber<String> subscriber = new FlowSubscriber<>("A");
        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();
        publisher.consume(integer -> {

        });

        flowPublisher.subscribe(subscriber);
        Flux.fromIterable(List.of(1,2,3)).doOnNext(integer -> {

        })
                .subscribe();
    }

}
