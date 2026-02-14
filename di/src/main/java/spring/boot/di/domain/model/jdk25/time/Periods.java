//: spring.boot.di.domain.model.jdk25.time.Periods.java

package spring.boot.di.domain.model.jdk25.time;


import java.time.LocalDate;
import java.time.Month;
import java.time.Period;


/*
 * In Java 25 (as in all versions since Java 8), the answer is Yes, they are equal
 * However, this is actually a "trap" involving how Java handles `static methods`.
 * Here is the breakdown of why this happens and why the second statement is misleading.
 *
 * 1. The Static Method "Trap"
 *    In the `java.time.Period` class, the methods `ofMonths()`, `ofWeeks()`,
 *    `ofYears()`, and `ofDays()` are `static` factory methods.
 *    When you write `Period.ofWeeks(-1).ofMonths(-1)`, the following happens:
 *      a. `Period.ofWeeks(-1)` is executed.
 *         it returns a `Period` object representing `-7 days` ().
 *      b. You then call `.ofMonths(-1)` on that resulting object.
 *      c. Because `ofMonths` is a **static** method, Java `ignores the instance`
 *         you just created. It effectively executes `Period.ofMonths(-1)`
 *         independently.
 *    The compiler will usually give you a warning:
 *     "Static member accessed via instance reference,"
 *     but the code will run and return a `Period` of exactly `-1 month` ().
 *
 * 2. Comparison of the Two Instances
 *    | Expression | Result (ISO-8601) | Years | Months | Days |
 *    | --- | --- | --- | --- | --- |
 *    | `Period.ofMonths(-1)` | `P-1M` | 0 | -1 | 0 |
 *    | `Period.ofWeeks(-1).ofMonths(-1)` | `P-1M` | 0 | -1 | 0 |
 *
 *    Since both expressions evaluate to a `Period` with  years,  month, and  days,
 *    they are considered equal by the `.equals()` method.
 *    ---
 *
 * 3. How to actually "Chain" Periods
 *    If your intention was to create a period that represents both -1 month and -1 week,
 *    you cannot use the `of` methods sequentially.
 *    You must use the `plus` or `minus` instance methods:
 *    ```
 *    // This creates a Period of -1 month AND -7 days (P-1M-7D)
 *    Period combined = Period.ofMonths(-1).plusWeeks(-1);
 *   ```
 *
 * 4. Why this matters in Java 25
 *    In Java 25, **Project Valhalla** is largely integrated.
 *    `Period` is a **Value-Based Class**.
 *    - Equality:** You should always use `.equals()` to compare them.
 *    - Identity:** In Java 25, using `==` on value-based classes like `Period`
 *      may behave differently than in older versions
 *      (it may perform a state-based check rather than a memory-address check),
 *      but `.equals()` remains the definitive way to check if they represent
 *      the same date-based amount.
 *
 * Summary: Both of your code snippets result in a Period of exactly -1 month
 * The `-1 week` in the second snippet is "lost" because it was overwritten by
 * the subsequent static call.
 */
class Periods {

    private static LocalDate plusByPeriod(LocalDate date, Period period) {
        return date.plus(period);
    }

    private static LocalDate withNewMonthInInteger(LocalDate date, int month) {
        return date.withMonth(month);
    }

    static void main(String... args) {

        LocalDate date = LocalDate.of(2025, Month.AUGUST, 4);
        Period period_1 = Period.ofMonths(-1);
        System.out.println(">>> period_1: " + period_1);
        Period period_2 = Period.ofWeeks(-1).ofMonths(-1);
        System.out.println(">>> period_2: " + period_2);

        LocalDate newDate_1 = plusByPeriod(date, period_1);
        System.out.println(">>> newDate_1: " + newDate_1);
        LocalDate newDate_2 = plusByPeriod(date, period_2);
        System.out.println(">>> newDate_2: " + newDate_2);

        if (!newDate_1.equals(newDate_2)) {
            throw new IllegalStateException(">>> Date calculation failed.");
        }
    }

} /// :~