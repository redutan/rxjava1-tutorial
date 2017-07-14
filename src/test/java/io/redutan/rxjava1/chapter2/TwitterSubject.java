package io.redutan.rxjava1.chapter2;

import rx.Observable;
import rx.subjects.PublishSubject;
import twitter4j.*;

/**
 * @author myeongju.jung
 */
public class TwitterSubject {
    private final PublishSubject<Status> subject = PublishSubject.create();

    public TwitterSubject() {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new StatusListener() {
            @Override
            public void onStatus(Status status) {
                subject.onNext(status);
            }

            @Override
            public void onException(Exception e) {
                subject.onError(e);
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

    public Observable<Status> observe() {
        return subject;
    }
}
