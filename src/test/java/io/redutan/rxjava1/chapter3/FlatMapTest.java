package io.redutan.rxjava1.chapter3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static io.redutan.rxjava1.chapter3.Sound.DAH;
import static io.redutan.rxjava1.chapter3.Sound.DI;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.*;

/**
 * @author myeongju.jung
 */
@Slf4j
public class FlatMapTest {
    @Test
    public void testMapFilter() throws Exception {
        Observable
            .range(1, 10)
            .map(x -> x * 2)
            .filter(x -> x != 10)
            .subscribe(x -> log.info(x.toString()));
    }

    @Test
    public void testMapFilterToFlatMap() throws Exception {
        // testMapFilter 과 같다
        Observable
            .range(1, 10)
            .flatMap(x -> just(x * 2))
            .flatMap(x -> (x != 10) ? just(x) : empty())
            .subscribe(x -> log.info(x.toString()));
    }

    @Test
    public void testFlatMapLicensePlates() throws Exception {
        cars()
            .flatMap(this::recognize)
            .map(LicensePlate::toString)
            .subscribe(
                log::info,
                e -> log.error(e.getMessage()));
    }

    Observable<CarPhoto> cars() {
        return Observable.from(randomListOf(10, CarPhoto.class));
    }

    Observable<LicensePlate> recognize(CarPhoto carPhoto) {
        int r = new Random().nextInt(5);
        if (r == 0) {
            return Observable.empty();
        } else if (r == 1) {
            return Observable.error(new IllegalStateException("Fail"));
        } else if (r == 2) {
            return Observable.from(randomListOf(2, LicensePlate.class));
        } else {
            return Observable.just(new LicensePlate("1"));
        }
    }

    @Test
    public void testFlatMapOrders() throws Exception {
        customers()
            .map(Customer::getOrders)
            .flatMap(Observable::from)
            .map(Order::toString)
            .subscribe(log::info);
    }

    Observable<Customer> customers() {
        return Observable.from(randomListOf(4, Customer.class));
    }

    /**
     * testFlatMapOrders 과 똑같다.
     */
    @Test
    public void testFlatMapIterableOrders() throws Exception {
        customers()
            .flatMapIterable(Customer::getOrders)   // map(Customer::getOrders) + flatMap(Observable::from)
            .map(Order::toString)
            .subscribe(log::info);
    }

    @Test
    public void testMorseCode() throws Exception {
        just('S', 'p', 'a', 'r', 't', 'a')
            .map(Character::toLowerCase)
            .flatMap(this::toMorseCode)
            .map(Sound::toString)
            .subscribe(log::info);
    }

    private Observable<Sound> toMorseCode(char ch) {
        switch (ch) {
            case 'a':
                return just(DI, DAH);
            case 'b':
                return just(DAH, DI, DI, DI);
            case 'c':
                return just(DAH, DI, DAH, DI);
            case 'p':
                return just(DI, DAH, DAH, DI);
            case 'r':
                return just(DI, DAH, DI);
            case 's':
                return just(DI, DI, DI);
            case 't':
                return just(DAH);
            default:
                return empty();
        }
    }

    @Test
    public void testDelayedBackground_But() throws Exception {
        Observable
            .just("Lorem", "ipsum", "dolor", "sit", "amet",
                "consectetur", "adipiscing", "alit")
            .delay(word -> timer(word.length(), SECONDS))
            .subscribe(log::info);
        TimeUnit.SECONDS.sleep(15);
        // 책 설명 상(p.80)상으로는 출력이 안되어야 하나 잘 됨
/*
17:29:01.929 [RxComputationScheduler-4] INFO io.redutan.rxjava1.chapter3.FlatMapTest - sit
17:29:02.925 [RxComputationScheduler-4] INFO io.redutan.rxjava1.chapter3.FlatMapTest - alit
17:29:02.928 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - amet
17:29:03.922 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Lorem
17:29:03.925 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - ipsum
17:29:03.926 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - dolor
17:29:08.926 [RxComputationScheduler-3] INFO io.redutan.rxjava1.chapter3.FlatMapTest - adipiscing
17:29:09.924 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - consectetur
 */
    }

    @Test
    public void testTimerWithFlatMap() throws Exception {
        Observable
            .just("Lorem", "ipsum", "dolor", "sit", "amet",
                "consectetur", "adipiscing", "alit")
            .flatMap(word -> timer(word.length(), SECONDS).map(x -> word))
            .subscribe(log::info);
        TimeUnit.SECONDS.sleep(15);
/*
17:34:03.078 [RxComputationScheduler-4] INFO io.redutan.rxjava1.chapter3.FlatMapTest - sit
17:34:04.074 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - amet
17:34:04.080 [RxComputationScheduler-4] INFO io.redutan.rxjava1.chapter3.FlatMapTest - alit
17:34:05.073 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Lorem
17:34:05.077 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - ipsum
17:34:05.078 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - dolor
17:34:10.078 [RxComputationScheduler-3] INFO io.redutan.rxjava1.chapter3.FlatMapTest - adipiscing
17:34:11.078 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - consectetur
 */
    }

    @Test
    public void testReverseNumberFlatMap() throws Exception {
        just(9, 7, 5, 3, 1)
            .flatMap(x -> just(x).delay(x, TimeUnit.SECONDS))
            .subscribe(System.out::println);
        TimeUnit.SECONDS.sleep(10);
/*
1 3 5 7 9
 */
    }

    @Test
    public void testWeekDayFlatMap() throws Exception {
        Observable
            .just(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            .flatMap(this::loadRecordsFor)
            .subscribe(log::info);
        TimeUnit.SECONDS.sleep(1);
/* 순서가 보장되지 않는다.
17:53:49.019 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-0 :  65ms
17:53:49.039 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-0 :  90ms
17:53:49.081 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-1 : 130ms = 65 * 2
17:53:49.127 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-1 : 180ms = 90 * 2
17:53:49.144 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-2 : 195ms = 65 * 3
17:53:49.207 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-3 : 260ms = 65 * 4
17:53:49.218 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-2 : 270ms = 90 * 3
17:53:49.273 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-4 : 325ms = 65 * 5
17:53:49.309 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-3 : 360ms = 90 * 4
17:53:49.398 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-4 : 450ms = 90 * 5
 */
    }

    private Observable<String> loadRecordsFor(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case SUNDAY:
                return Observable
                    .interval(90, MILLISECONDS)
                    .take(5)
                    .map(i -> "Sun-" + i);
            case MONDAY:
                return Observable
                    .interval(65, MILLISECONDS)
                    .take(5)
                    .map(i -> "Mon-" + i);
            default:
                return empty();
        }
    }

    /**
     * flatMap과는 다르게 순서가 지켜진다
     */
    @Test
    public void testWeekDayConcatMap() throws Exception {
        Observable
            .just(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
            .concatMap(this::loadRecordsFor)
            .subscribe(log::info);
        TimeUnit.SECONDS.sleep(1);

/* 대략 Sun은 간격이 90m 이며 Mon은 간격이 65ms 이다
17:52:08.619 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-0
17:52:08.704 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-1
17:52:08.796 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-2
17:52:08.887 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-3
17:52:08.975 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Sun-4
17:52:09.045 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-0
17:52:09.108 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-1
17:52:09.173 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-2
17:52:09.236 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-3
17:52:09.305 [RxComputationScheduler-2] INFO io.redutan.rxjava1.chapter3.FlatMapTest - Mon-4
 */
    }

    @Test
    public void testGetProfilesWithFlatMap() throws Exception {
        List<User> veryLargeUsers = randomListOf(100, User.class);
        Observable
            .from(veryLargeUsers)
            .flatMap(User::loadProfile)  // 동시적으로 실행할 수 있는 수(스레드 수)가 2개로 제한
            .map(Profile::toString)
            .subscribe(log::info);
        /* sleep 없이 동기적으로 실행됨 */
    }

    @Test
    public void testGetProfilesWith2ThreadsFlatMap() throws Exception {
        List<User> veryLargeUsers = randomListOf(100, User.class);
        Observable
            .from(veryLargeUsers)
            .flatMap(User::loadProfile, 2)  // 동시적으로 실행할 수 있는 수(스레드 수)가 2개로 제한
            .map(Profile::toString)
            .subscribe(log::info);
        /* sleep 이 요구되는 비동기적으로 실행됨 */
        TimeUnit.SECONDS.sleep(10);
    }
}

class User {
    Observable<Profile> loadProfile() {
        return Observable
            .just(random(Profile.class))
            .delay(1, TimeUnit.SECONDS);    // api 통신이라 가정하고 1초 지연을 준다.
    }
}

@Data
class Profile {
    long profileNo;
}

enum Sound {
    DI, DAH
}

class CarPhoto {

}

@Data
@AllArgsConstructor
class LicensePlate {
    String carNumber;
}

@Data
class Customer {
    List<Order> orders;
}

@Data
class Order {
    String orderNo;
}
