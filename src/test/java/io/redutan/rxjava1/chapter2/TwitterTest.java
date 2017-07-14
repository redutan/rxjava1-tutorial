package io.redutan.rxjava1.chapter2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;
import twitter4j.*;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author myeongju.jung
 */
@Slf4j
public class TwitterTest {
    @Test
    public void testTwitterStep1() throws Exception {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                log.info("Status: {}", status);
            }

            @Override
            public void onException(Exception ex) {
                log.error("Error callback", ex);
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
        twitterStream.sample();
        TimeUnit.SECONDS.sleep(10);
        twitterStream.shutdown();
    }

    @Test
    public void testTwitterStep2() throws Exception {
        consume(
            status -> log.info("Status: {}", status),
            ex -> log.info("Error callback", ex)
        );
    }

    void consume(Consumer<Status> onStatus, Consumer<Exception> onException) {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                onStatus.accept(status);
            }

            @Override
            public void onException(Exception ex) {
                onException.accept(ex);
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
        twitterStream.sample();
    }

    @Test
    public void testRxTwitterStep1() throws Exception {
        Subscription subscribe = observe().subscribe(
            status -> log.info("Status: {}", status),
            ex -> log.info("Error callback", ex)
        );
        TimeUnit.SECONDS.sleep(10);
        subscribe.unsubscribe();
    }

    public static Observable<Status> observe() {
        return Observable.create(subscriber -> {
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
            subscriber.add(Subscriptions.create(twitterStream::shutdown));
            twitterStream.sample();
        });
    }

    @Test
    public void testRxTwitterStep2() throws Exception {
        Observable<Status> observe = new TwitterSubject().observe();
        Subscription subscribe = observe.subscribe(
            status -> log.info("Status: {}", status),
            ex -> log.info("Error callback", ex)
        );
        TimeUnit.SECONDS.sleep(10);
        subscribe.unsubscribe();
    }
}
