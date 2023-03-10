package ru.itmo.EasyFlow;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.itmo.EasyFlow.Topic;
import java.util.concurrent.Flow;

@Slf4j
@Getter
public class Subscriber implements Flow.Subscriber<ru.itmo.EasyFlow.Topic> {

    private final String name;
    private Flow.Subscription subscription;


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
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(ru.itmo.EasyFlow.Topic item) {
        
        log.info(Thread.currentThread().getName() + " | " + this.getName() + " Received Topic " + item.getName());
        // 3000 mills delay to simulate slow subscriber with 50% variety
        if (Math.random()<0.5){
            try {
                log.info(Thread.currentThread().getName() + " | " + this.getName() + ", topic " + item.getName() + " seems to take a little more time...");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info(Thread.currentThread().getName() + " | " + this.getName() + " Finished working with " + item.getName());
        // Processing of item is done so request next value.
        subscription.request(1);
    }


    @Override
    public void onError(Throwable throwable) {
        log.info(Thread.currentThread().getName() + " | ERROR = "
                + throwable.getClass().getSimpleName() + " | " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("Completed!!!");
    }
}
