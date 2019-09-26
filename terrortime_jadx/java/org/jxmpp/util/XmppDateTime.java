package org.jxmpp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmppDateTime {
    private static final Pattern SECOND_FRACTION = Pattern.compile(".*\\.(\\d{1,})(Z|((\\+|-)\\d{4}))");
    /* access modifiers changed from: private */
    public static final TimeZone TIME_ZONE_UTC = TimeZone.getTimeZone("UTC");
    private static final List<PatternCouplings> couplings = new ArrayList();
    private static final DateFormatType dateFormatter = DateFormatType.XEP_0082_DATE_PROFILE;
    private static final Pattern datePattern = Pattern.compile("^\\d+-\\d+-\\d+$");
    private static final DateFormatType dateTimeFormatter = DateFormatType.XEP_0082_DATETIME_MILLIS_PROFILE;
    private static final DateFormatType dateTimeNoMillisFormatter = DateFormatType.XEP_0082_DATETIME_PROFILE;
    private static final Pattern dateTimeNoMillisPattern = Pattern.compile("^\\d+(-\\d+){2}+T(\\d+:){2}\\d+(Z|([+-](\\d+:\\d+)))$");
    private static final Pattern dateTimePattern = Pattern.compile("^\\d+(-\\d+){2}+T(\\d+:){2}\\d+.\\d+(Z|([+-](\\d+:\\d+)))$");
    private static final DateFormatType timeFormatter = DateFormatType.XEP_0082_TIME_MILLIS_ZONE_PROFILE;
    private static final DateFormatType timeNoMillisFormatter = DateFormatType.XEP_0082_TIME_ZONE_PROFILE;
    private static final DateFormatType timeNoMillisNoZoneFormatter = DateFormatType.XEP_0082_TIME_PROFILE;
    private static final Pattern timeNoMillisNoZonePattern = Pattern.compile("^(\\d+:){2}\\d+$");
    private static final Pattern timeNoMillisPattern = Pattern.compile("^(\\d+:){2}\\d+(Z|([+-](\\d+:\\d+)))$");
    private static final DateFormatType timeNoZoneFormatter = DateFormatType.XEP_0082_TIME_MILLIS_PROFILE;
    private static final Pattern timeNoZonePattern = Pattern.compile("^(\\d+:){2}\\d+.\\d+$");
    private static final Pattern timePattern = Pattern.compile("^(\\d+:){2}\\d+.\\d+(Z|([+-](\\d+:\\d+)))$");
    private static final ThreadLocal<DateFormat> xep0091Date6DigitFormatter = new ThreadLocal<DateFormat>() {
        /* access modifiers changed from: protected */
        public DateFormat initialValue() {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMd'T'HH:mm:ss");
            dateFormat.setTimeZone(XmppDateTime.TIME_ZONE_UTC);
            return dateFormat;
        }
    };
    private static final ThreadLocal<DateFormat> xep0091Date7Digit1MonthFormatter = new ThreadLocal<DateFormat>() {
        /* access modifiers changed from: protected */
        public DateFormat initialValue() {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMdd'T'HH:mm:ss");
            dateFormat.setTimeZone(XmppDateTime.TIME_ZONE_UTC);
            dateFormat.setLenient(false);
            return dateFormat;
        }
    };
    private static final ThreadLocal<DateFormat> xep0091Date7Digit2MonthFormatter = new ThreadLocal<DateFormat>() {
        /* access modifiers changed from: protected */
        public DateFormat initialValue() {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMd'T'HH:mm:ss");
            dateFormat.setTimeZone(XmppDateTime.TIME_ZONE_UTC);
            dateFormat.setLenient(false);
            return dateFormat;
        }
    };
    private static final ThreadLocal<DateFormat> xep0091Formatter = new ThreadLocal<DateFormat>() {
        /* access modifiers changed from: protected */
        public DateFormat initialValue() {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
            dateFormat.setTimeZone(XmppDateTime.TIME_ZONE_UTC);
            return dateFormat;
        }
    };
    private static final Pattern xep0091Pattern = Pattern.compile("^\\d+T\\d+:\\d+:\\d+$");

    private enum DateFormatType {
        XEP_0082_DATE_PROFILE("yyyy-MM-dd"),
        XEP_0082_DATETIME_PROFILE("yyyy-MM-dd'T'HH:mm:ssZ"),
        XEP_0082_DATETIME_MILLIS_PROFILE("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
        XEP_0082_TIME_PROFILE("hh:mm:ss"),
        XEP_0082_TIME_ZONE_PROFILE("hh:mm:ssZ"),
        XEP_0082_TIME_MILLIS_PROFILE("hh:mm:ss.SSS"),
        XEP_0082_TIME_MILLIS_ZONE_PROFILE("hh:mm:ss.SSSZ"),
        XEP_0091_DATETIME("yyyyMMdd'T'HH:mm:ss");
        
        private final boolean CONVERT_TIMEZONE;
        private final ThreadLocal<DateFormat> FORMATTER;
        /* access modifiers changed from: private */
        public final String FORMAT_STRING;
        private final boolean HANDLE_MILLIS;

        private DateFormatType(String dateFormat) {
            this.FORMAT_STRING = dateFormat;
            this.FORMATTER = new ThreadLocal<DateFormat>() {
                /* access modifiers changed from: protected */
                public DateFormat initialValue() {
                    DateFormat dateFormat = new SimpleDateFormat(DateFormatType.this.FORMAT_STRING);
                    dateFormat.setTimeZone(XmppDateTime.TIME_ZONE_UTC);
                    return dateFormat;
                }
            };
            boolean z = true;
            if (dateFormat.charAt(dateFormat.length() - 1) != 'Z') {
                z = false;
            }
            this.CONVERT_TIMEZONE = z;
            this.HANDLE_MILLIS = dateFormat.contains("SSS");
        }

        /* access modifiers changed from: private */
        public String format(Date date) {
            String res = ((DateFormat) this.FORMATTER.get()).format(date);
            if (this.CONVERT_TIMEZONE) {
                return XmppDateTime.convertRfc822TimezoneToXep82(res);
            }
            return res;
        }

        /* access modifiers changed from: private */
        public Date parse(String dateString) throws ParseException {
            if (this.CONVERT_TIMEZONE) {
                dateString = XmppDateTime.convertXep82TimezoneToRfc822(dateString);
            }
            if (this.HANDLE_MILLIS) {
                dateString = XmppDateTime.handleMilliseconds(dateString);
            }
            return ((DateFormat) this.FORMATTER.get()).parse(dateString);
        }
    }

    private static class PatternCouplings {
        final DateFormatType formatter;
        final Pattern pattern;

        PatternCouplings(Pattern datePattern, DateFormatType dateFormat) {
            this.pattern = datePattern;
            this.formatter = dateFormat;
        }
    }

    static {
        couplings.add(new PatternCouplings(datePattern, dateFormatter));
        couplings.add(new PatternCouplings(dateTimePattern, dateTimeFormatter));
        couplings.add(new PatternCouplings(dateTimeNoMillisPattern, dateTimeNoMillisFormatter));
        couplings.add(new PatternCouplings(timePattern, timeFormatter));
        couplings.add(new PatternCouplings(timeNoZonePattern, timeNoZoneFormatter));
        couplings.add(new PatternCouplings(timeNoMillisPattern, timeNoMillisFormatter));
        couplings.add(new PatternCouplings(timeNoMillisNoZonePattern, timeNoMillisNoZoneFormatter));
    }

    public static Date parseXEP0082Date(String dateString) throws ParseException {
        for (PatternCouplings coupling : couplings) {
            if (coupling.pattern.matcher(dateString).matches()) {
                return coupling.formatter.parse(dateString);
            }
        }
        return dateTimeNoMillisFormatter.parse(dateString);
    }

    public static Date parseDate(String dateString) throws ParseException {
        if (xep0091Pattern.matcher(dateString).matches()) {
            int length = dateString.split("T")[0].length();
            if (length >= 8) {
                return ((DateFormat) xep0091Formatter.get()).parse(dateString);
            }
            Date date = handleDateWithMissingLeadingZeros(dateString, length);
            if (date != null) {
                return date;
            }
        }
        return parseXEP0082Date(dateString);
    }

    public static String formatXEP0082Date(Date date) {
        return dateTimeFormatter.format(date);
    }

    public static String convertXep82TimezoneToRfc822(String dateString) {
        if (dateString.charAt(dateString.length() - 1) == 'Z') {
            return dateString.replace("Z", "+0000");
        }
        return dateString.replaceAll("([\\+\\-]\\d\\d):(\\d\\d)", "$1$2");
    }

    public static String convertRfc822TimezoneToXep82(String dateString) {
        int length = dateString.length();
        String res = dateString.substring(0, length - 2);
        StringBuilder sb = new StringBuilder();
        sb.append(res);
        sb.append(':');
        String res2 = sb.toString();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(res2);
        sb2.append(dateString.substring(length - 2, length));
        return sb2.toString();
    }

    public static String asString(TimeZone timeZone) {
        int rawOffset = timeZone.getRawOffset();
        int hours = rawOffset / 3600000;
        return String.format("%+d:%02d", new Object[]{Integer.valueOf(hours), Integer.valueOf(Math.abs((rawOffset / 60000) - (hours * 60)))});
    }

    private static Date handleDateWithMissingLeadingZeros(String stampString, int dateLength) throws ParseException {
        if (dateLength == 6) {
            return ((DateFormat) xep0091Date6DigitFormatter.get()).parse(stampString);
        }
        Calendar now = Calendar.getInstance();
        List<Calendar> dates = filterDatesBefore(now, parseXEP91Date(stampString, (DateFormat) xep0091Date7Digit1MonthFormatter.get()), parseXEP91Date(stampString, (DateFormat) xep0091Date7Digit2MonthFormatter.get()));
        if (!dates.isEmpty()) {
            return determineNearestDate(now, dates).getTime();
        }
        return null;
    }

    private static Calendar parseXEP91Date(String stampString, DateFormat dateFormat) {
        try {
            dateFormat.parse(stampString);
            return dateFormat.getCalendar();
        } catch (ParseException e) {
            return null;
        }
    }

    private static List<Calendar> filterDatesBefore(Calendar now, Calendar... dates) {
        List<Calendar> result = new ArrayList<>();
        for (Calendar calendar : dates) {
            if (calendar != null && calendar.before(now)) {
                result.add(calendar);
            }
        }
        return result;
    }

    /* access modifiers changed from: private */
    public static String handleMilliseconds(String dateString) {
        Matcher matcher = SECOND_FRACTION.matcher(dateString);
        if (!matcher.matches()) {
            return dateString;
        }
        int fractionalSecondsDigitCount = matcher.group(1).length();
        if (fractionalSecondsDigitCount == 3) {
            return dateString;
        }
        int posDecimal = dateString.indexOf(".");
        StringBuilder sb = new StringBuilder((dateString.length() - fractionalSecondsDigitCount) + 3);
        if (fractionalSecondsDigitCount > 3) {
            sb.append(dateString.substring(0, posDecimal + 4));
        } else {
            sb.append(dateString.substring(0, posDecimal + fractionalSecondsDigitCount + 1));
            for (int i = fractionalSecondsDigitCount; i < 3; i++) {
                sb.append('0');
            }
        }
        sb.append(dateString.substring(posDecimal + fractionalSecondsDigitCount + 1));
        return sb.toString();
    }

    private static Calendar determineNearestDate(final Calendar now, List<Calendar> dates) {
        Collections.sort(dates, new Comparator<Calendar>() {
            public int compare(Calendar o1, Calendar o2) {
                return Long.valueOf(now.getTimeInMillis() - o1.getTimeInMillis()).compareTo(Long.valueOf(now.getTimeInMillis() - o2.getTimeInMillis()));
            }
        });
        return (Calendar) dates.get(0);
    }
}
