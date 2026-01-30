//: spring.boot.sfg7.domain.model.jdk25.Periods.java

package spring.boot.di.domain.model.jdk25;


import java.time.LocalTime;
import java.time.Period;


class Periods {

    static void main(String... args) {
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

} /// :~