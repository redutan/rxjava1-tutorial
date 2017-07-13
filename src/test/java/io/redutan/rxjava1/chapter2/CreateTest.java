package io.redutan.rxjava1.chapter2;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author myeongju.jung
 */
public class CreateTest {
    private static void log(Object msg) {
        System.out.println(Thread.currentThread().getName() + ": " + msg);
    }

    static <T> Observable<T> delayed(T x) {
        return Observable.create(
            subscriber -> {
                Runnable r = () -> {
                    log(Thread.currentThread().getName() + ": Start");
                    sleep(10, SECONDS);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(x);
                        subscriber.onCompleted();
                    }
                };
                final Thread thread = new Thread(r);
                thread.start();
            });
    }

    static <T> Observable<T> delayedWithInterrupt(T x) {
        return Observable.create(
            subscriber -> {
                Runnable r = () -> {
                    log(Thread.currentThread().getName() + ": Start");
                    sleep(10, SECONDS);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(x);
                        subscriber.onCompleted();
                    }
                };
                final Thread thread = new Thread(r);
                thread.start();
                subscriber.add(Subscriptions.create(thread::interrupt));    // 구독해지 시 이벤트 등록 : 스레드 중단(interrupt)
            });
    }

    static void sleep(int timeout, TimeUnit unit) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException ignored) {
            CreateTest.log(Thread.currentThread().getName() + ": Exit");
        }
    }

    @Test
    public void testCreateInt() throws Exception {
        Observable<Integer> ints = Observable.create((Observable.OnSubscribe<Integer>) subscriber -> {
            log("Create");
            subscriber.onNext(5);
            subscriber.onNext(6);
            subscriber.onNext(7);
            subscriber.onCompleted();
            log("Complete");
        });
        log("Starting");
        ints.subscribe(i -> log("Element : " + i));
        log("Exit");
    }

    @Test
    public void testMultiSubscriber() throws Exception {
        Observable<Object> ints = Observable.create(subscriber -> {
            log("Create");
            subscriber.onNext(42);
            subscriber.onCompleted();
        });
        log("Starting");
        ints.subscribe(i -> log("Element A: " + i));
        ints.subscribe(i -> log("Element B: " + i));
        log("Exit");
/*
main: Starting
main: Create
main: Element A: 42
main: Create
main: Element B: 42
main: Exit
 */
    }

    @Test
    public void testMultiSubscriberWithCache() throws Exception {
        Observable<Object> ints = Observable.create(subscriber -> {
            log("Create");
            subscriber.onNext(42);
            subscriber.onCompleted();
        }).cache();
        log("Starting");
        ints.subscribe(i -> log("Element A: " + i));
        ints.subscribe(i -> log("Element B: " + i));
        log("Exit");
/*
main: Starting
main: Create
main: Element A: 42
main: Element B: 42
main: Exit
 */
    }

    @Test
    public void testInfinityNumbers() throws Exception {
        Observable<Object> naturalNumbers = Observable.create(subscriber -> {
            Runnable r = () -> {
                BigInteger i = ZERO;
                while (!subscriber.isUnsubscribed()) {  // !!!
                    subscriber.onNext(i);
                    i = i.add(ONE);
                }
            };
            new Thread(r).start();
        });
        Subscription subscription = naturalNumbers.subscribe(CreateTest::log);
        // 시간이 어느정도 지난 다음
        TimeUnit.MILLISECONDS.sleep(100);
        subscription.unsubscribe();
        log("Exit");
    }

    @Test
    public void testCloseResourceDelayed() throws Exception {
        Observable<Integer> delayedInt = delayed(10);
        log("Start");
        Subscription subscribe = delayedInt.subscribe(CreateTest::log);
        TimeUnit.MILLISECONDS.sleep(1000);
        log("Exit");
    }

    @Test
    public void testCloseResourceFast() throws Exception {
        Observable<Integer> delayedInt = delayedWithInterrupt(10);
        log("Start");
        Subscription subscribe = delayedInt.subscribe(CreateTest::log);
        TimeUnit.MILLISECONDS.sleep(1000);
        subscribe.unsubscribe();
        log("Exit");
    }

    @Test
    public void testLoadAllWithMultiThread() throws Exception {
        Observable<Data> datas = loadAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        log("Start");
        Subscription subscribe = datas.subscribe(CreateTest::log);
        TimeUnit.MILLISECONDS.sleep(1000);
        log("Exit");
    }

    private Observable<Data> loadAll(Collection<Integer> ids) {
        return Observable.create(subscriber -> {
            ExecutorService pool = Executors.newFixedThreadPool(10);
            AtomicInteger countDown = new AtomicInteger(ids.size());
            ids.forEach(id -> pool.submit(() -> {
                final Data data = load(id);
                subscriber.onNext(data);    // 위반:여러스레드에서 동시에 onNext를 호출하고 있음
                if (countDown.decrementAndGet() == 0) {
                    pool.shutdownNow();
                    subscriber.onCompleted();
                }
            }));
        });
    }

    private Data load(Integer id) {
        Data result = new Data();
        result.setId(id);
        result.setTitle(random(String.class));
        return result;
    }

    @Test
    public void testTimer() throws Exception {
        // 1초 지연 후 0 방출
        Observable.timer(1, TimeUnit.SECONDS)
            .subscribe(CreateTest::log);
        TimeUnit.MILLISECONDS.sleep(1001);
    }

    @Test
    public void testInterval() throws Exception {
        // 초당 60개씩 (60Hz) 방출 : 애니메이션 처리 시 유리함
        // ScheduledExecutorService#scheduleAtFixedRate 와 유사하다
        Subscription subscribe = Observable.interval(1_000_000 / 60, MICROSECONDS)
            .subscribe(CreateTest::log);
        TimeUnit.MILLISECONDS.sleep(1000);
        subscribe.unsubscribe();
    }
}
