package io.redutan.rxjava1.chapter1;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class CalculateTest {
    @Test
    public void syncOperation() throws Exception {
        Observable<Integer> o = Observable.create(s -> {
            s.onNext(1);
            s.onNext(2);
            s.onNext(3);
            s.onNext(4);
            s.onCompleted();
        });
        o.map(i -> "Number " + i)
            .subscribe(System.out::println);
    }

    @Test
    public void asyncOperation() throws Exception {
        Observable.<Integer>create(s -> {
            System.out.println("1 = " + new Thread());
            Thread createThread = new Thread(() -> {
                System.out.println("create thread = " + Thread.currentThread());
                s.onNext(1);
                s.onNext(2);
                s.onNext(3);
                s.onNext(4);
                s.onCompleted();
            });
            System.out.println("2 = " + createThread);
            System.out.println("3 = " + new Thread());
            createThread.start();
        })
            .doOnNext(i -> System.out.println(Thread.currentThread()))
            .filter(i -> i % 2 == 0)
            .map(i -> " 값 " + i + " 는 " + Thread.currentThread() + " 에서 처리된다")
            .subscribe(s -> System.out.println("값 =>" + s));
        System.out.println("값이 출력되지 전에 나온다" + Thread.currentThread());
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    @Test
    public void doNotThisWhenMerge() throws Exception {
        // 이렇게 하지 말 것
        Observable.create(s -> {
            // 스레드 A
            new Thread(() -> {
                s.onNext("one");
                s.onNext("two");
            }).start();

            // 스레드 B
            new Thread(() -> {
                s.onNext("three");
                s.onNext("four");
            }).start();
            // 스레드 경합 문제로 s.onCompleted() 호출을 생략해야 한다
        });
        // 이렇게 하지 말 것
    }

    @Test
    public void doWhenMerge() throws Exception {
        Observable<String> a = Observable.create(s -> new Thread(() -> {
            s.onNext("one");
            s.onNext("two");
            s.onCompleted();
        }).start());

        Observable<String> b = Observable.create(s -> new Thread(() -> {
            s.onNext("three");
            s.onNext("four");
            s.onCompleted();
        }).start());

        // 동시에 a와 b를 구독하여 제 3의 순차적인 스트림으로 병합한다.
        Observable<String> c = Observable.merge(a, b);
        c.subscribe(System.out::println);
        /*
        - one > two 순서는 보장 (동기)
        - three > four 순서는 보장 (동기)
        - one/two 와 three/four 순서는 정할 수 없음 (비동기)
         */
    }


}
