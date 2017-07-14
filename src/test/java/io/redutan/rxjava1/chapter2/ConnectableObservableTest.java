package io.redutan.rxjava1.chapter2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.subscriptions.Subscriptions;
import twitter4j.*;

import java.util.concurrent.TimeUnit;

/**
 * @author myeongju.jung
 */
@Slf4j
public class ConnectableObservableTest {
    @Test
    public void testTwoSubscribe() throws Exception {
        Observable<Integer> firstMillion = Observable.range(1, 1000_000).sample(7, TimeUnit.MILLISECONDS);

        firstMillion.subscribe(
            it -> log.info("Subscriber #1:{}", it),
            it -> log.info("Error:" + it.getMessage()),
            () -> log.info("Sequence #1 complete")
        );

        firstMillion.subscribe(
            it -> log.info("Subscriber #2:{}", it),
            it -> log.info("Error:" + it.getMessage()),
            () -> log.info("Sequence #2 complete")
        );
/*
12:01:47.933 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:25465
12:01:47.938 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:47256
12:01:47.946 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:55457
12:01:47.952 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:57556
12:01:47.959 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:96722
12:01:47.966 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:125621
12:01:47.974 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:200177
12:01:47.980 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:207686
12:01:47.987 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:255089
12:01:47.995 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:274736
12:01:48.001 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:578039
12:01:48.009 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:939280
12:01:48.012 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:1000000
12:01:48.013 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Sequence #1 complete
12:01:48.028 [RxComputationScheduler-3] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #2:341928
12:01:48.035 [RxComputationScheduler-3] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #2:835699
12:01:48.039 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #2:1000000
12:01:48.039 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Sequence #2 complete
 */
    }

    @Test
    public void testTwoSubscribeWithConnectable() throws Exception {
        // Observable 에 접근할 수 있게 배포 해 둠
        ConnectableObservable<Integer> firstMillion =
            Observable.range(1, 1000_000).sample(7, TimeUnit.MILLISECONDS).publish();

        // 해당 Observable에 구독 시작 : 하지만 스트림이 연결 되지는 않음
        firstMillion.subscribe(
            it -> log.info("Subscriber #1:{}", it),
            it -> log.info("Error:" + it.getMessage()),
            () -> log.info("Sequence #1 complete")
        );

        firstMillion.subscribe(
            it -> log.info("Subscriber #2:{}", it),
            it -> log.info("Error:" + it.getMessage()),
            () -> log.info("Sequence #2 complete")
        );

        firstMillion.connect(); // 해당 구독의 스트림이 시작됨. connect 하지 않으면 구독되지 않는다 (연결이 안되었으므로 - 중간에 중재 전용 Subscriber 가 연결을 해준다.)
/*
12:01:47.668 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:42503
12:01:47.674 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #2:42503
12:01:47.675 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:201785
12:01:47.675 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #2:201785
12:01:47.675 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:218424
12:01:47.675 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #2:218424
12:01:47.684 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:726316
12:01:47.684 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #2:726316
12:01:47.690 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:806454
12:01:47.690 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #2:806454
12:01:47.693 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #1:1000000
12:01:47.693 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscriber #2:1000000
12:01:47.693 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Sequence #1 complete
12:01:47.693 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Sequence #2 complete
 */
    }

    @Test
    public void testTwitterEachConnection() throws Exception {
        Observable<Object> observable = observe();

        Subscription sub1 = observable.subscribe();
        log.info("Subscribed 1");
        Subscription sub2 = observable.subscribe();
        log.info("Subscribed 2");

        sub1.unsubscribe();
        log.info("Unsubscribed 1");

        sub2.unsubscribe();
        log.info("Unsubscribed 2");

/*
12:06:48.281 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Establishing connection
12:06:48.414 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscribed 1
12:06:48.414 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Establishing connection
12:06:48.414 [Twitter Stream consumer /  [1][initializing]] INFO twitter4j.TwitterStreamImpl - Establishing connection.
12:06:48.414 [Twitter Stream consumer /  [1][Establishing connection]] DEBUG twitter4j.TwitterStreamImpl - Twitter Stream consumer /  [1][Establishing connection]
12:06:48.415 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscribed 2
12:06:48.415 [Twitter Stream consumer /  [2][initializing]] INFO twitter4j.TwitterStreamImpl - Establishing connection.
12:06:48.415 [Twitter Stream consumer /  [2][Establishing connection]] DEBUG twitter4j.TwitterStreamImpl - Twitter Stream consumer /  [2][Establishing connection]
12:06:48.415 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Disconnection
12:06:48.415 [main] DEBUG twitter4j.TwitterStreamImpl - Twitter Stream consumer /  [1][Disposing thread]
12:06:48.416 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Unsubscribed 1
12:06:48.416 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Disconnection
12:06:48.416 [main] DEBUG twitter4j.TwitterStreamImpl - Twitter Stream consumer /  [2][Disposing thread]
12:06:48.416 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Unsubscribed 2
 */
    }

    private Observable<Object> observe() {
        return Observable.create(subscriber -> {
            log.info("Establishing connection");
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
            subscriber.add(Subscriptions.create(() -> {
                log.info("Disconnection");
                twitterStream.shutdown();
            }));
            twitterStream.sample();
        });
    }

    @Test
    public void testTwitterOneConnection() throws Exception {
        Observable<Object> lazy = observe().share();
        // == Observable<Object> lazy = observe().publish().refCount()

        log.info("Before subscribers");
        Subscription sub1 = lazy.subscribe();
        log.info("Subscribed 1");
        Subscription sub2 = lazy.subscribe();
        log.info("Subscribed 2");

        sub1.unsubscribe();
        log.info("Unsubscribed 1");

        sub2.unsubscribe();
        log.info("Unsubscribed 2");

/*
12:06:26.667 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Before subscribers
12:06:26.725 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Establishing connection
12:06:26.794 [Twitter Stream consumer /  [1][initializing]] INFO twitter4j.TwitterStreamImpl - Establishing connection.
12:06:26.795 [Twitter Stream consumer /  [1][Establishing connection]] DEBUG twitter4j.TwitterStreamImpl - Twitter Stream consumer /  [1][Establishing connection]
12:06:26.797 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscribed 1
12:06:26.797 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Subscribed 2
12:06:26.798 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Unsubscribed 1
12:06:26.798 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Disconnection
12:06:26.798 [main] DEBUG twitter4j.TwitterStreamImpl - Twitter Stream consumer /  [1][Disposing thread]
12:06:26.801 [main] INFO io.redutan.rxjava1.chapter2.ConnectableObservableTest - Unsubscribed 2
 */
    }
}
