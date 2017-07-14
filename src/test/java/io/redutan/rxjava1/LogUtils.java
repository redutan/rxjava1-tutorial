package io.redutan.rxjava1;

/**
 * @author myeongju.jung
 */
public class LogUtils {
    public static void log(Object msg) {
        System.out.println(Thread.currentThread().getName() + ": " + msg);
    }
}
