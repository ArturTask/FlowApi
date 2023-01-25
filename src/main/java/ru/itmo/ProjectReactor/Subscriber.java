package ru.itmo.ProjectReactor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.*;
import reactor.core.publisher.BaseSubscriber;

@Slf4j
@Getter
public class Subscriber extends BaseSubscriber<Topic> {

    private final String name;
    private Subscription subscription;


    public Subscriber( String name, Topic...topics) {
        this.name = name;
        for (Topic t : topics) {
            ServerEmulator.getInstance().registerSubscriber(this, t);
        }
    }

    public void receivedMessage(Topic t, Message m) {
        log.info("Subsriber " + this.getName() + " recieved " + m.getText() + " [topic - " + t.getName() + " ]");
    }


    @Override
    public void hookOnNext(Topic item) {
        
        log.info(Thread.currentThread().getName() + " | " + this.getName() + " Received Topic " + item.getName());
//        // 3000 mills delay to simulate slow subscriber with 50% variety
//        if (Math.random()<0.5){
//            try {
//                log.info(Thread.currentThread().getName() + " | " + this.getName() + ", topic " + item.getName() + " seems to take a little more time...");
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        log.info(Thread.currentThread().getName() + " | " + this.getName() + " Finished working with " + item.getName());
        // Processing of item is done so request next value.
        subscription.request(12);
    }


    @Override
    public void hookOnError(Throwable throwable) {
        log.info(Thread.currentThread().getName() + " | ON ERROR = "
                + throwable.getClass().getSimpleName() + " | " + throwable.getMessage());
    }

    @Override
    public void hookOnComplete() {
        log.info("Completed!!!");
    }
}
