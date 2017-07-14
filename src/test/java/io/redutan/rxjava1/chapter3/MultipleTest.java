package io.redutan.rxjava1.chapter3;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;

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
}

@Value
class Weather {
    Temperature temperature;
    Wind wind;
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
