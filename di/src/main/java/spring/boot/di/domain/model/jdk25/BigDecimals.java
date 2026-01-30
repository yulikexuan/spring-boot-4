//: spring.boot.sfg7.domain.model.jdk25.BigDicimals.java

package spring.boot.di.domain.model.jdk25;


import java.math.BigDecimal;
import java.math.RoundingMode;


class BigDecimals {

    static void main(String... args) {

        // UP: Rounds away from zero.
        //   2.1 → 3
        //   -2.1 → -3
        BigDecimal up = new BigDecimal("2020.4")
                .setScale(0, RoundingMode.UP); // 2021

        // DOWN: Rounds towards zero (truncation).
        //   2.9 → 2
        //   -2.9 → -2
        BigDecimal down = new BigDecimal("2020.4")
                .setScale(0, RoundingMode.DOWN); // 2020

        // HALF_UP: Rounds to the nearest neighbor; if equidistant, rounds up.
        //   2.5 → 3
        //   2.4 → 2
        //   -2.5 → -3
        BigDecimal half_up = new BigDecimal("2020.4")
                .setScale(0, RoundingMode.HALF_UP); // 2020

        // HALF_EVEN: Rounds to the nearest neighbor;
        // if equidistant, rounds to the even neighbor.
        //   2.5 → 2 (even)
        //   3.5 → 4 (even)
        //   2.4 → 2
        BigDecimal even = new BigDecimal("2020.5")
                .setScale(0, RoundingMode.HALF_EVEN); // 2020

        // HALF_DOWN: Rounds to the nearest neighbor; if equidistant, rounds down.
        //   2.5 → 2
        //   2.6 → 3
        BigDecimal half_down = new BigDecimal("2020.6")
                .setScale(0, RoundingMode.HALF_DOWN);

        System.out.print(up + "-" + down + "-" + half_up + "-" + even + "-" + half_down);

        // UNNECESSARY: Asserts the exact result; throws exception if rounding needed.
    }

} /// :~