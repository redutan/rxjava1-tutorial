package io.redutan.rxjava1.chapter1;

import org.junit.Test;
import rx.Observable;

/**
 * 기본 테스트
 *
 * @author redutan
 * @since 2017. 5. 20.
 */
public class BasicTest {

    @Test
    public void helloworld() throws Exception {
        //noinspection deprecation
        Observable.create(s -> {
            s.onNext("Hello World");
            s.onCompleted();
        }).subscribe(System.out::println);
    }

    @Test
    public void multiGet() throws Exception {
        //noinspection deprecation
        Observable.create(s -> {
            s.onNext(1);
            s.onNext(2);
            s.onNext(3);
            s.onCompleted();
        }).subscribe(System.out::println);
    }
}
