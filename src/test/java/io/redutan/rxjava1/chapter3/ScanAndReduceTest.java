package io.redutan.rxjava1.chapter3;

import lombok.Data;
import org.junit.Test;
import rx.Observable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;


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

    @Test
    public void testTotal2Case() throws Exception {
        Observable<CashTransfer> transfers =
            Observable.from(randomListOf(20, CashTransfer.class));

        Observable<BigDecimal> total1 = transfers
            .reduce(BigDecimal.ZERO, (totalSoFar, transfer) ->
                totalSoFar.add(transfer.getAmount()));
        total1.subscribe(System.out::println);

        // 위 보다 아래 가 더 간결하다.
        Observable<BigDecimal> total2 = transfers
            .map(CashTransfer::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        total2.subscribe(System.out::println);
    }

    @Test
    public void testReduceLikeCollect() throws Exception {
        Observable<ArrayList<Integer>> all = Observable
            .range(10, 20)
            .reduce(new ArrayList<Integer>(), (list, item) -> {
                list.add(item);
                return list;
            });
        all.subscribe(System.out::println);
/*
[10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29]
 */
    }

    /**
     * http://reactivex.io/documentation/operators/reduce.html
     */
    @Test
    public void testCollect() throws Exception {
        Observable<ArrayList<Integer>> all = Observable
            .range(10, 20)
            .collect(ArrayList::new, List::add);
        all.subscribe(System.out::println);
    }

    @Test
    public void testCollectStringBuilder() throws Exception {
        Observable<String> str = Observable
            .range(1, 10)
            .collect(StringBuilder::new,
                (sb, x) -> sb.append(x).append(", "))
            .map(StringBuilder::toString);
        str.subscribe(System.out::println);
/*
1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
 */
    }
}

@Data
class CashTransfer {
    BigDecimal amount;
}
