package io.redutan.rxjava1.chapter3;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;

/**
 * 여러 개의 {@link rx.Observable}
 *
 * @author myeongju.jung
 */
@Slf4j
public class MultipleTest {
    @Test
    public void testMerge() throws Exception {
        CarPhoto photo = null;
        Observable<LicensePlate> all = Observable.merge(
            preciseAlgo(photo),
            fastAlgo(photo),
            expermentalAlgo(photo)
        );
        all
            .map(LicensePlate::toString)
            .subscribe(log::info);
        TimeUnit.MILLISECONDS.sleep(1100);
/*
18:33:34.151 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - LicensePlate(carNumber=fast)
18:33:35.139 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.MultipleTest - LicensePlate(carNumber=slow)
 */
    }

    Observable<LicensePlate> fastAlgo(CarPhoto photo) {
        return Observable
            .just(new LicensePlate("fast"));
    }

    Observable<LicensePlate> preciseAlgo(CarPhoto photo) {
        return Observable
            .just(new LicensePlate("slow"))
            .delay(1, TimeUnit.SECONDS);
    }

    Observable<LicensePlate> expermentalAlgo(CarPhoto photo) {
        // 예츨할 순 없지만, 어쨋든 실행됨
        return Observable.empty();
    }

    @Test
    public void testZipWeather() throws Exception {
        Observable<Temperature> temperatures = Observable.from(randomListOf(10, Temperature.class));    // 10개 방출
        Observable<Wind> winds = Observable.from(randomListOf(20, Wind.class)); // 20개 방출
        temperatures
            .zipWith(winds, Weather::new)
            .subscribe(w -> log.info("{}", w));
/* 10개만 출력된다.
18:54:31.082 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.5343484129545577), wind=Wind(direction=E, value=0.43902898574625915))
18:54:31.092 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.3325412352175223), wind=Wind(direction=E, value=0.7110757101982893))
18:54:31.092 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.5232976470980399), wind=Wind(direction=E, value=0.1226857619684737))
18:54:31.092 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.7116510564097561), wind=Wind(direction=E, value=0.5352820153558627))
18:54:31.092 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.49612897304952), wind=Wind(direction=E, value=0.4053468039123008))
18:54:31.092 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.47403910206840405), wind=Wind(direction=E, value=0.7186298213841095))
18:54:31.092 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.3355810603177731), wind=Wind(direction=E, value=0.002982031990012679))
18:54:31.092 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.07593138160825186), wind=Wind(direction=E, value=0.7760257605741582))
18:54:31.092 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.14108163121526496), wind=Wind(direction=E, value=0.6605941546128724))
18:54:31.092 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Weather(temperature=Temperature(value=0.9156473238745475), wind=Wind(direction=E, value=0.9622290808699685))
 */
    }

    /**
     * 카테시안 곱을 통해서 체스판을 만들어보자
     */
    @Test
    public void testChessBoard() throws Exception {
        Observable<Integer> oneToEight = Observable.range(1, 8);
        Observable<String> ranks = oneToEight
            .map(Object::toString);
        Observable<String> files = oneToEight
            .map(x -> 'a' + x - 1)
            .map(ascii -> (char) ascii.intValue())
            .map(ch -> Character.toString(ch));

        Observable<String> squares = files
            .flatMap(file -> ranks.map(rank -> file + rank));
        squares.subscribe(log::info);
/*
a1, a2, ... a8, ...h8
 */
    }

    /**
     * http://reactivex.io/documentation/operators/zip.html
     */
    @Test
    public void testZip() throws Exception {
        Observable<LocalDate> nextTenDays = Observable
            .range(1, 10)
            .map(i -> LocalDate.now().plusDays(i));

        Observable<Vacation> possibleVacations = Observable
            .just(City.Warsaw, City.London, City.Paris)
            .flatMap(city -> nextTenDays.map(date -> new Vacation(city, date)))
            .flatMap(vacation ->
                Observable.zip(
                    vacation.weather().filter(Weather::isSunny),    // source 1
                    vacation.cheapFlightFrom(City.NewYork),         // source 2
                    vacation.cheapHodel(),                          // source 3
                    (w, f, h) -> vacation                           // 조합 함수
                )
            );
    }

    @Test
    public void testTimestamp() throws Exception {
        Observable<Long> red = Observable.interval(10, TimeUnit.MILLISECONDS);
        Observable<Long> green = Observable.interval(11, TimeUnit.MILLISECONDS);

        Observable.zip(
            red.timestamp(),
            green.timestamp(),
            (r, g) -> r.getTimestampMillis() - g.getTimestampMillis()
        ).forEach(System.out::println);
        TimeUnit.SECONDS.sleep(1);

        /*
        결과 시간이 계속적으로 증가하는 것을 알 수 있다. 결국 zip는 2개의 스트림이 시간 지연이 유사해야한다 안 그러면 느린 스트림은 계속 지연하게 될 것이다.
        이것은 메모리 누수로 이어질 수 있다.
        */
    }

    /**
     * http://reactivex.io/documentation/operators/combinelatest.html
     */
    @Test
    public void testCombineLast() throws Exception {
        Observable.combineLatest(
            Observable.interval(17, TimeUnit.MILLISECONDS).map(x -> "SLOW" + x),
            Observable.interval(10, TimeUnit.MILLISECONDS).map(x -> "FAST" + x),
            (s, f) -> f + ":" + s
        ).forEach(System.out::println);
        TimeUnit.SECONDS.sleep(1);
/* 빠른 스트림이 느린 스트림을 기다리지 않아도 된다.
FAST0:SLOW0
FAST1:SLOW0
FAST2:SLOW0
FAST2:SLOW1
FAST3:SLOW1
FAST3:SLOW2
...
FAST97:SLOW56
FAST97:SLOW57
FAST98:SLOW57
FAST99:SLOW57
FAST99:SLOW58
 */
    }

    @Test
    public void testWithLatestFrom() throws Exception {
        Observable<String> slow = Observable.interval(17, TimeUnit.MILLISECONDS).map(x -> "SLOW" + x);
        Observable<String> fast = Observable.interval(10, TimeUnit.MILLISECONDS).map(x -> "FAST" + x);

        slow
            .withLatestFrom(fast, (s, f) -> s + ":" + f)
            .forEach(System.out::println);

        TimeUnit.SECONDS.sleep(1);
/* 주 스트림(slow)과 연결되는 부 스트림의 마지막 방출과 결합시킨다. 하지만 FAST 스트림 일부가 버려진다.
SLOW0:FAST1
SLOW1:FAST2
SLOW2:FAST4
SLOW3:FAST6
SLOW4:FAST7
SLOW5:FAST9
...
SLOW54:FAST92
SLOW55:FAST94
SLOW56:FAST96
SLOW57:FAST98
SLOW58:FAST99
 */
    }

    @Test
    public void testWithLatestFromFastDummy() throws Exception {
        Observable<String> fast = Observable.interval(10, TimeUnit.MILLISECONDS)
            .map(x -> "F" + x)
            .delay(100, TimeUnit.MILLISECONDS)
            .startWith("FX");
        Observable<String> slow = Observable.interval(17, TimeUnit.MILLISECONDS)
            .map(x -> "S" + x);
        slow
            .withLatestFrom(fast, (s, f) -> s + ":" + f)
            .forEach(System.out::println);
        TimeUnit.SECONDS.sleep(1);
/*
S0:FX
S1:FX
S2:FX
S3:FX
S4:FX
S5:FX
S6:F1
S7:F3
S8:F4
S9:F6
...
 */
    }

    @Test
    public void testStartWith() throws Exception {
        Observable.just(1, 2)
            .startWith(0)
            .subscribe(System.out::println);
/* 무조건 startWith의 값(0)을 먼저 방출한다.
0
1
2
 */
    }

    /**
     * http://reactivex.io/documentation/operators/amb.html
     */
    @Test
    public void testAmb() throws Exception {
        Observable.amb(
            stream(100, 17, "S"),
            stream(200, 10, "F")
        ).subscribe(log::info);
        TimeUnit.SECONDS.sleep(1);
/*
17:19:12.101 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Subscribe to S
17:19:12.108 [main] INFO io.redutan.rxjava1.chapter3.MultipleTest - Subscribe to F
17:19:12.210 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.MultipleTest - Unsubscribe from F
17:19:12.211 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.MultipleTest - S0
17:19:12.225 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.MultipleTest - S1
17:19:12.240 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.MultipleTest - S2
17:19:12.259 [RxComputationScheduler-1] INFO io.redutan.rxjava1.chapter3.MultipleTest - S3
...
 */
    }

    Observable<String> stream(int initialDelay, int interval, String name) {
        return Observable
            .interval(initialDelay, interval, TimeUnit.MILLISECONDS)
            .map(x -> name + x)
            .doOnSubscribe(() -> log.info("Subscribe to " + name))
            .doOnUnsubscribe(() -> log.info("Unsubscribe from " + name));
    }
}

class Vacation {
    private final City where;
    private final LocalDate when;

    Vacation(City where, LocalDate when) {
        this.where = where;
        this.when = when;
    }

    public Observable<Weather> weather() {
        // TODO
        return Observable.empty();
    }

    public Observable<Flight> cheapFlightFrom(City from) {
        // TODO
        return Observable.empty();
    }

    public Observable<Hotel> cheapHodel() {
        return Observable.empty();
    }
}

enum City {
    London, Paris, NewYork, Warsaw
}

class Flight {

}

class Hotel {

}

@Value
class Weather {
    Temperature temperature;
    Wind wind;

    public boolean isSunny() {
        return true;
    }
}

@Value
class Temperature {
    double value;
}

@Value
class Wind {
    Direction direction;
    double value;

    enum Direction {
        N, E, S, W
    }
}
