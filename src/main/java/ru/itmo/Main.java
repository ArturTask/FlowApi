package ru.itmo;

import lombok.extern.slf4j.Slf4j;
import ru.itmo.MyFlow.HardFlowPublisher;
import ru.itmo.MyFlow.HardFlowSubscriber;

import java.util.concurrent.*;

@Slf4j
public class Main {

    static ExecutorService service = Executors.newFixedThreadPool(5);
    static ScheduledExecutorService canceller = Executors.newScheduledThreadPool(5);

    public static <T> Future<T> tryExecute(Callable<T> c, long timeoutMS){
        final Future<T> future = service.submit(c);
        canceller.schedule(() -> {
            future.cancel(true);
            return null;
        }, timeoutMS, TimeUnit.MILLISECONDS);
        return future;

    }

    private static final CompletableFuture terminated = new CompletableFuture<>();



    public static void main(String[] args) throws InterruptedException {

//        SubmissionPublisher<String> stringSubmissionPublisher = new SubmissionPublisher<>();
//        stringSubmissionPublisher.offer("",(subscriber, s) -> {
//            subscriber.onError(new RuntimeException("String " + s
//                    + " droped because of backpressure"));
//            return true;
//        } );
//
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//        executor.scheduleWithFixedDelay(() -> {
//
//        }, 1, 1, TimeUnit.MILLISECONDS);


        SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>();
        publisher.offer(1, (subscriber1, integer) -> {
            subscriber1.onError(new Exception());
            return true;
        });


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
//                .setTimeout(Duration.ofSeconds(1));
        hardFlowPublisher.subscribe(hardFlowSubscriber);
        hardFlowPublisher.subscribe(hardFlowSubscriber2);


//        Future<Boolean> booleanFuture = tryExecute(() -> {
//            System.out.println("da");
//            Thread.sleep(10000);
//            System.out.println("net");
//            return true;
//        }, 1000);
//
//        Future<Integer> integerFuture = tryExecute(new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//                return 9*7;
//            }
//        }, 1000);
//
//        service.shutdown();
//        canceller.shutdown();


//        FlowPublisher<Integer> myPublisher = new FlowPublisher<>();
//
//        FlowSubscriber<String> subscriber = new FlowSubscriber<>("A");
//        myPublisher.subscribe(subscriber);

        System.out.println();


    }

}
