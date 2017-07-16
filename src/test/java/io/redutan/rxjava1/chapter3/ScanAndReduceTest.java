package io.redutan.rxjava1.chapter3;

import org.junit.Test;
import rx.Observable;

import java.math.BigInteger;


/**
 * {@code scan()}과 {@code reduce}로 순열 훑기
 *
 * @author myeongju.jung
 */
public class ScanAndReduceTest {

    /**
     * http://reactivex.io/documentation/operators/scan.html
     */
    @Test
    public void testAccumulator() throws Exception {
        Observable
            .just(10, 14, 12, 13, 14, 16)
            .scan(Integer::sum)
            .subscribe(System.out::println);
/*
10
24
36
49
63
79
 */
    }

    @Test
    public void testInitializeAndAccumulator() throws Exception {
        Observable<BigInteger> factorials = Observable
            .range(2, 100)
            .scan(BigInteger.ONE, (big, cur) ->
                big.multiply(BigInteger.valueOf(cur)));

        factorials.subscribe(System.out::println);
/*
1
2
6
24
120
720
...
 */
    }
}
