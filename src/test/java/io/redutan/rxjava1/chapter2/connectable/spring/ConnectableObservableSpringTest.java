package io.redutan.rxjava1.chapter2.connectable.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author myeongju.jung
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class ConnectableObservableSpringTest {

    @Test
    public void testInitialize() throws Exception {
        TimeUnit.SECONDS.sleep(3);
/*
13:49:27.893 [main] INFO io.redutan.rxjava1.chapter2.connectable.spring.Bar - Subscribed
13:49:27.900 [main] INFO io.redutan.rxjava1.chapter2.connectable.spring.Foo - Subscribed
13:49:27.981 [main] INFO io.redutan.rxjava1.chapter2.connectable.spring.SpringConfiguration - Connecting
13:49:27.982 [main] INFO io.redutan.rxjava1.chapter2.connectable.spring.SpringConfiguration - Starting
13:49:28.162 [Twitter Stream consumer /  [1][initializing]] INFO twitter4j.TwitterStreamImpl - Establishing connection.
13:49:29.937 [Twitter Stream consumer /  [1][Establishing connection]] INFO twitter4j.TwitterStreamImpl - Connection established.
13:49:29.937 [Twitter Stream consumer /  [1][Establishing connection]] INFO twitter4j.TwitterStreamImpl - Receiving status stream.
13:49:29.985 [Twitter4J Async Dispatcher[0]] INFO io.redutan.rxjava1.chapter2.connectable.spring.Bar - 暇な人〜
13:49:29.985 [Twitter4J Async Dispatcher[0]] INFO io.redutan.rxjava1.chapter2.connectable.spring.Foo - 暇な人〜
13:49:30.122 [Twitter4J Async Dispatcher[0]] INFO io.redutan.rxjava1.chapter2.connectable.spring.Bar - RT @ONGHOUSE_TH: YMC ปล่อยรูปอ๋งมาทุกช็อตเลยได้มั้ยคะ ขอร้อง https://t.co/34FfYZbq9S
13:49:30.122 [Twitter4J Async Dispatcher[0]] INFO io.redutan.rxjava1.chapter2.connectable.spring.Foo - RT @ONGHOUSE_TH: YMC ปล่อยรูปอ๋งมาทุกช็อตเลยได้มั้ยคะ ขอร้อง https://t.co/34FfYZbq9S
...
 */
    }
}
