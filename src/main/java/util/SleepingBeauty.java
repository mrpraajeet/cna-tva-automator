package util;

import data.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SleepingBeauty {
    private static final Logger logger = LogManager.getLogger(SleepingBeauty.class);

    public static void sleep(long milliseconds) {
        try {
            if (milliseconds < 0) return;
            logger.info("Sleeping for {}ms", milliseconds);
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new AutomationException(ErrorCode.SLEEP_INTERRUPTED, e);
        }
    }

    public static void sleep(int seconds) {
        sleep(seconds * 1000L);
    }

    public static void sleep(double seconds) {
        sleep((long) (seconds * 1000L));
    }

    public static void sleep(float seconds) {
        sleep((long) (seconds * 1000L));
    }

    // Bell Curve with extremes being 2/3 and 4/3
    public static void randSleep(long milliseconds) {
        double random = Math.random() + Math.random() - Math.random() - Math.random();
        long deviation = (long) (milliseconds * random / 6);
        sleep(milliseconds + deviation);
    }

    public static void randSleep(int seconds) {
        randSleep(seconds * 1000L);
    }

    public static void randSleep(double seconds) {
        randSleep((long) (seconds * 1000L));
    }

    public static void randSleep(float seconds) {
        randSleep((long) (seconds * 1000L));
    }
}
