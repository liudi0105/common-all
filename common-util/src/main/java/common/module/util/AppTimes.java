package common.module.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;


@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppTimes {

    public static final DateTimeFormatter DEFAULT_LOCAL_DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter DEFAULT_LOCAL_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter DEFAULT_OFFSET_DATETIME_FORMATTER = new DateTimeFormatterBuilder()
            // date/time
            .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            // offset (hh:mm - "+00:00" when it's zero)
            .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
            // offset (hhmm - "+0000" when it's zero)
            .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
            // offset (hh - "Z" when it's zero)
            .optionalStart().appendOffset("+HH", "Z").optionalEnd()
            // create formatter
            .toFormatter();

    public static final ZoneOffset OFFSET8 = ZoneOffset.ofHours(8);
    public static final ZoneId ZONE_ID8 = ZoneOffset.ofHours(8).normalized();

    public static LocalDateTime parseLocalDatetime(String value) {
        return LocalDateTime.parse(value, DEFAULT_LOCAL_DATETIME_FORMATTER);
    }

    public static ZonedDateTime parseZonedDatetime(String value) {
        return ZonedDateTime.parse(value, DEFAULT_OFFSET_DATETIME_FORMATTER);
    }

    public static OffsetDateTime parseOffsetDatetime(String value) {
        return OffsetDateTime.parse(value, DEFAULT_OFFSET_DATETIME_FORMATTER);
    }

    public static Instant parseInstance(String value) {
        return parseOffsetDatetime(value).toInstant();
    }

    public static LocalDateTime parseLocalDatetime(String value, String pattern) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalTime parseLocalTime(String value, String pattern) {
        return LocalTime.parse(value, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate parseLocalDate(String value) {
        return LocalDate.parse(value, DEFAULT_LOCAL_DATE_FORMATTER);
    }

    public static LocalDate parseLocalDate(String value, String pattern) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
    }

    public static Instant toInstant(LocalDateTime dateTime, ZoneId zoneId) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atZone(zoneId).toInstant();
    }

    public static Date toDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneOffset.systemDefault()).toInstant());
    }

    public static String format(OffsetDateTime time) {
        return format(time, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static String format(OffsetDateTime time, DateTimeFormatter formatter) {
        return time.format(formatter);
    }

    public static String format(Instant time, ZoneOffset zoneOffset) {
        return format(time.atOffset(zoneOffset));
    }

    public static String format(ZonedDateTime time) {
        return format(time.toOffsetDateTime());
    }

    public static String format(ZonedDateTime time, DateTimeFormatter formatter) {
        return format(time.toOffsetDateTime(), formatter);
    }

    public static String format(LocalDate time) {
        return format(time, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String format(LocalDate time, DateTimeFormatter pattern) {
        if (time == null) {
            throw new RuntimeException("待格式化的时间为空");
        }
        return time.format(pattern);
    }

    public static String format(LocalDateTime time) {
        return format(time, DEFAULT_LOCAL_DATETIME_FORMATTER);
    }

    public static String format(LocalDateTime time, String pattern) {
        return format(time, DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDateTime time, DateTimeFormatter pattern) {
        if (time == null) {
            throw new RuntimeException("待格式化的时间为空");
        }
        return time.format(pattern);
    }

    public static LocalDateTime sharpHourBefore(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate().atTime(localDateTime.getHour(), 0);
    }

    public static LocalDateTime sharpHourAfter(LocalDateTime localDateTime) {
        return sharpHourBefore(localDateTime).plusHours(1);
    }

    public static LocalDateTime sharpHour(LocalDateTime localDateTime) {
        if (localDateTime.getMinute() < 30) {
            return sharpHourBefore(localDateTime);
        }
        return sharpHourAfter(localDateTime);
    }

    public static LocalDateTime startOfDay(LocalDateTime time) {
        return time.toLocalDate().atTime(0, 0);
    }

    public static LocalDateTime endOfDay(LocalDateTime time) {
        return startOfDay(time).plusDays(1);
    }

    public static LocalDateTime halfHour(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate().atTime(localDateTime.getHour(), 30, 0);
    }

    public static LocalDateTime toLocalDateTime(Date dateToConvert) {
        return LocalDateTime.ofInstant(
                dateToConvert.toInstant(), ZoneId.systemDefault());
    }
}
