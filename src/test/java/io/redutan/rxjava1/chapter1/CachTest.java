package io.redutan.rxjava1.chapter1;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;
import rx.Single;

import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * @author redutan
 * @since 2017. 5. 20.
 */
@RunWith(MockitoJUnitRunner.class)
public class CachTest {
    @Mock
    private DataHolder<Integer, String> dataHolder;

    private final Integer notCachedKey = 1;
    private final String notCachedValue = "notCachedValue";
//    final Integer cachedKey = 2;
//    final String cachedValue = "cacheValue";

    @Before
    public void setUp() throws Exception {
        // mock not cached
        when(dataHolder.getFromCache(eq(notCachedKey))).thenReturn(null);
        when(dataHolder.getDataAsynchronously(eq(notCachedKey))).then(i -> {
            // 조회 지연 : 외부 통신, 무거운 연산 등
            TimeUnit.MILLISECONDS.sleep(1000);
            return Single.just(notCachedValue);
        });
    }

    @Test
    public void notCached() throws Exception {
        //noinspection deprecation
        Observable.create(s -> {
            String fromCache = dataHolder.getFromCache(notCachedKey);
            if (fromCache != null) {
                // 동기적인 방출
                s.onNext(fromCache);
                s.onCompleted();
            } else {
                // 비동기로 가져온다
                dataHolder.getDataAsynchronously(notCachedKey)
                    .subscribe(value -> {
                        s.onNext(value);
                        s.onCompleted();
                    }, s::onError);
            }
        }).subscribe(System.out::println);

        verify(dataHolder, times(1)).getFromCache(eq(notCachedKey));
        verify(dataHolder, times(1)).getDataAsynchronously(eq(notCachedKey));
    }
}

interface DataHolder<K, V> {
    V getFromCache(K key);

    Single<V> getDataAsynchronously(K key);
}
