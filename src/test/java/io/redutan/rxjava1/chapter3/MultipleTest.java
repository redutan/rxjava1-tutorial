package io.redutan.rxjava1.chapter3;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

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
}
