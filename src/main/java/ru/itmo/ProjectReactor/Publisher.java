package ru.itmo.ProjectReactor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Slf4j
public class Publisher {

    private final Topic topic;
    @Getter(value = AccessLevel.NONE)
    private Flux<Topic> topicFlux;

    public Publisher(Topic t) {
        this.topic = t;
        this.topicFlux = Flux.just(t);
        this.topicFlux = this.topicFlux.onBackpressureDrop();
    }

    public Publisher setTopics(List<Topic> topics){
        topicFlux = Flux.fromIterable(topics);
        return this;
    }

    public Publisher subscribe(Subscriber subscriber){
        topicFlux.subscribe(subscriber);
        return this;
    }
    public Publisher subscribe(Consumer<? super Topic> consumer){
        topicFlux.subscribe(consumer);
        return this;
    }

    public Publisher setTimeout(Duration timeout){
        topicFlux = topicFlux.timeout(timeout);
        return this;
    }

    public Publisher setDelay(Duration duration){
        topicFlux = topicFlux.delayElements(duration);
        return this;
    }

    public Publisher onErrorResume(Function<? super Throwable, ? extends Flux<? extends Topic>> fallback){
        topicFlux = topicFlux.onErrorResume(fallback);
        return this;
    }
    public Publisher onErrorReturn(Topic topic){
        topicFlux = topicFlux.onErrorReturn(topic);
        return this;
    }

    public Publisher setStrategyLatest(){
        topicFlux = topicFlux.onBackpressureLatest();
        return this;
    }

    public Publisher setStrategyDrop(Consumer<? super Topic> onDropped){
        topicFlux = topicFlux.onBackpressureDrop(onDropped);
        return this;
    }

    public Publisher setStrategyBuffer(int bufferSize){
        topicFlux = topicFlux.onBackpressureBuffer(bufferSize);
        return this;
    }

    public void publish(Message m) {
        ServerEmulator.getInstance().sendMessage(this.topic, m);
    }

    public enum Strategy {
        BUFFER, DROP, LATEST
    }
}
