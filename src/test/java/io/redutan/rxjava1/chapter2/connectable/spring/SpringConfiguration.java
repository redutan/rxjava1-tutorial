package io.redutan.rxjava1.chapter2.connectable.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.subscriptions.Subscriptions;
import twitter4j.*;

@SuppressWarnings("deprecation")
@Slf4j
@ComponentScan("io.redutan.rxjava1.chapter2.connectable.spring")
public class SpringConfiguration implements ApplicationListener<ContextRefreshedEvent> {
    private final ConnectableObservable<Status> observable =
        Observable.<Status>create(subscriber -> {
            log.info("Starting");
            TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
            twitterStream.addListener(new StatusListener() {
                @Override
                public void onStatus(Status status) {
                    subscriber.onNext(status);
                }

                @Override
                public void onException(Exception e) {
                    subscriber.onError(e);
                }

                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                }

                @Override
                public void onTrackLimitationNotice(int i) {

                }

                @Override
                public void onScrubGeo(long l, long l1) {

                }

                @Override
                public void onStallWarning(StallWarning stallWarning) {

                }
            });
            // subscriber가 구독을 해지(unsubscribe())하면 발생시키는 콜백 > 트위터스트림 닫음
            subscriber.add(Subscriptions.create(() -> {
                log.info("Disconnected");
                twitterStream.shutdown();
            }));
            twitterStream.sample();
        }).publish();

    @Bean
    public Observable<Status> observable() {
        return observable;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        log.info("Connecting");
        observable.connect();
    }
}

@Slf4j
@Component
class Foo {
    @Autowired
    public Foo(Observable<Status> tweets) {
        tweets.subscribe(
            status -> log.info(status.getText())
        );
        log.info("Subscribed");
    }
}

@Slf4j
@Component
class Bar {
    @Autowired
    public Bar(Observable<Status> tweets) {
        tweets.subscribe(
            status -> log.info(status.getText())
        );
        log.info("Subscribed");
    }
}
