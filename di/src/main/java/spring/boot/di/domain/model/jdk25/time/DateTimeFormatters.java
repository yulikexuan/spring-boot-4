//: spring.boot.di.domain.model.jdk25.time.DateTimeFormatters.java

package spring.boot.di.domain.model.jdk25.time;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


class DateTimeFormatters {

    static DateTimeFormatter create(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }

    /*
     * In Java's date-time pattern syntax, you do not use a backslash `\`
     * to escape a single quote.
     * Instead, you use two consecutive single quotes `''`
     */
    static void main(String... args) {
        var now = LocalDateTime.now();
        var dateTimeMsg = create(
                "' It''s 'h' hours past midnight, and 'mm' minutes' ")
                .format(now);
        System.out.println(dateTimeMsg);
    }

} /// :~