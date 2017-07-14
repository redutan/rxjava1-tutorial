package io.redutan.rxjava1.chapter3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;

import java.util.List;
import java.util.Random;

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static io.redutan.rxjava1.chapter3.Sound.DAH;
import static io.redutan.rxjava1.chapter3.Sound.DI;
import static rx.Observable.empty;
import static rx.Observable.just;

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
