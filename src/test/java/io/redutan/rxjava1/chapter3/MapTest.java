package io.redutan.rxjava1.chapter3;

import io.redutan.rxjava1.chapter2.TwitterTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import twitter4j.Status;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author myeongju.jung
 */
@Slf4j
public class MapTest {
    @Test
    public void testMap() throws Exception {
        Observable<Status> tweets = TwitterTest.observe();
        Observable<Date> dates = tweets.map(Status::getCreatedAt);
        Subscription subscribe = dates.subscribe(date -> log.info("date = {}", date));
        TimeUnit.SECONDS.sleep(5);
        subscribe.unsubscribe();
    }

    @Test
    public void testMapFilter() throws Exception {
        Observable
            .just(8, 9, 10)
            .filter(i -> i % 3 > 0)
            .map(i -> "#" + i * 10)
            .filter(s -> s.length() < 4)
            .subscribe(log::info);
/*
16:01:02.523 [main] INFO io.redutan.rxjava1.chapter3.MapTest - #80
 */
    }

    @Test
    public void testMapFilterWithDoOnNext() throws Exception {
        Observable
            .just(8, 9, 10)
            .doOnNext(i -> log.info("A: " + i))
            .filter(i -> i % 3 > 0)
            .doOnNext(i -> log.info("B: " + i))
            .map(i -> "#" + i * 10)
            .doOnNext(s -> log.info("C: " + s))
            .filter(s -> s.length() < 4)
            .subscribe(s -> log.info("D: " + s));
/*
15:59:55.232 [main] INFO io.redutan.rxjava1.chapter3.MapTest - A: 8
15:59:55.236 [main] INFO io.redutan.rxjava1.chapter3.MapTest - B: 8
15:59:55.236 [main] INFO io.redutan.rxjava1.chapter3.MapTest - C: #80
15:59:55.236 [main] INFO io.redutan.rxjava1.chapter3.MapTest - D: #80
15:59:55.236 [main] INFO io.redutan.rxjava1.chapter3.MapTest - A: 9
15:59:55.236 [main] INFO io.redutan.rxjava1.chapter3.MapTest - A: 10
15:59:55.236 [main] INFO io.redutan.rxjava1.chapter3.MapTest - B: 10
15:59:55.236 [main] INFO io.redutan.rxjava1.chapter3.MapTest - C: #100
 */
    }
}
