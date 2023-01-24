package ru.itmo.ProjectReactor;

import lombok.AccessLevel;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@Getter
public class Publisher {

    private final Topic topic;
    @Getter(value = AccessLevel.NONE)
    private Mono<Topic> topicMono;

    public Publisher(Topic t) {
        this.topic = t;
        this.topicMono = Mono.just(t);
    }

    public Publisher subscribe(Subscriber subscriber){
        topicMono.subscribe( subscriber);
        return this;
    }

    public Publisher tryDropOnBackpressure (Topic topic, long timeout, TimeUnit unit, BiPredicate<Flow.Subscriber<? super Topic>,? super Topic> onDrop){
//        submissionPublisher.offer(topic, timeout, unit, onDrop);
        topicMono.offer(topic, onDrop);
        return this;
    }

    public Publisher tryBufferOnBackpressure(Topic topic){
        topicMono.submit(topic);
        return this;
    }

    public void publish(Message m) {
        ServerEmulator.getInstance().sendMessage(this.topic, m);
    }

}
