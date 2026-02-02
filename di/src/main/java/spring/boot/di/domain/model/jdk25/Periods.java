//: spring.boot.sfg7.domain.model.jdk25.Periods.java

package spring.boot.di.domain.model.jdk25;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;


class Periods {

    private static void periodIsForDates() {
        LocalTime time = LocalTime.now();
        Period period = Period.of(1, 0, 0);

        /*
         * Will throw `UnsupportedTemporalTypeException`
         * `Period` represents date-based units like years, months, days
         * `LocalTime` represents time of day like hours, minutes, seconds
         * Root Cause: LocalTime doesn't support date-based temporal units
         *             like Years, Months, or Days because it only deals with time
         */
        time.plus(period);
        time.plusHours(2);
        System.out.println(time);
    }

    static void workingWithDates() {

        var date = LocalDate.of(2024, Month.FEBRUARY, 29);

        if(date.isLeapYear()) {

            if(date.equals(LocalDate.of(date.getYear(), 2, 29))) {

//                var periodYear = Period.ofYears(1);
//                var periodDay = Period.ofDays(1);
//                var newDate = date.minus(periodYear).minus(periodDay);
//                // outputs newDate: 2023-02-27
//                System.out.println("newDate:" + newDate);

//                var newDate = date
//                        .minus(1, ChronoUnit.YEARS)
//                        .minus(1, ChronoUnit.DAYS);
//                // outputs newDate: 2023-02-27

                var periodYear = Period.ofYears(1);
                var periodDay = Period.ofDays(1);
                var newDate = date.minus(periodYear).minus(periodDay);
                // outputs newDate: 2023-02-28
                System.out.println("newDate:" + newDate);
            }
        }

    }

    static void main(String... args) {

        //periodIsForDates();

        workingWithDates();

    }

} /// :~