package ru.itmo.EasyFlow;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;

@Getter
public class Publisher {

    private final Topic topic;
    @Getter(value = AccessLevel.NONE)
    private SubmissionPublisher<Topic> submissionPublisher;

    public Publisher(Topic t) {
        this.topic = t;
        this.submissionPublisher = new SubmissionPublisher<>();
    }

    public Publisher subscribe(Subscriber subscriber){
        submissionPublisher.subscribe(subscriber);
        return this;
    }

    public Publisher tryDropOnBackpressure (Topic topic, long timeout, TimeUnit unit, BiPredicate<Flow.Subscriber<? super Topic>,? super  Topic> onDrop){
//        submissionPublisher.offer(topic, timeout, unit, onDrop);
        submissionPublisher.offer(topic, onDrop);
        return this;
    }

    public Publisher tryBufferOnBackpressure(Topic topic){
        submissionPublisher.submit(topic);
        return this;
    }

    public void publish(Message m) {
        ServerEmulator.getInstance().sendMessage(this.topic, m);
    }

}
