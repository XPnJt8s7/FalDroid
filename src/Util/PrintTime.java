package Util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PrintTime {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
    private static Instant instant;
    private static ZonedDateTime zdt;
    private static String output;

    public static void PrintTime() {
    }

    public static String PrintMilis(long milis) {
        instant = Instant.ofEpochMilli(milis);
        zdt = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
        output = formatter.format(zdt);
        return output;
    }
}
