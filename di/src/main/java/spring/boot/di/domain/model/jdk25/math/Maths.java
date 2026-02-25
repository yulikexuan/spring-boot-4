//: spring.boot.di.domain.model.jdk25.math.Maths.java

package spring.boot.di.domain.model.jdk25.math;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


class Maths {

    public static void main(String... args) {

        double pythagorean_1 = pythagorean(3, 4);
        double pythagorean_2 = Math.sqrt(Math.pow(3, 2) + Math.pow(4, 2));

        if (pythagorean_1 != pythagorean_2) {
            throw new IllegalStateException("Pythagorean theorem failed for 3, 4");
        }

        var math = new ArrayList<>();
        math.add(pythagorean_1);
        math.add(area(3, 8));
    }

    public static double pythagorean(int a, int b) {

        // `.5` is a legal floating-point literal
        return Math.pow(Math.pow(a, 2) + Math.pow(b, 2), .5);

        // There is no method named squareRoot
        // Should be: Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
        // Math.squareRoot(Math.pow(a, 2) + Math.pow(b, 2));
    }

    public static double area(int base, int height) {
        return ((double) 1 / 2) * (base * height);
    }

    static void compareDoubleWithInt() {
        List<Double> numbers = new ArrayList<>();
        numbers.add(12.0);
        numbers.add(3.14);
        numbers.add(3.14);
        numbers.add(2.718);
        numbers.add(2.718);

        /*
         * Yes, `10L == 10` is `true` in Java. This is a valid comparison.
         * When you compare a `long` and an `int` with `==`,
         * Java automatically **widens** the `int` to `long` before comparing.
         * So `10L == 10` becomes `10L == 10L`, which is `true`.
         *
         * This is **numeric promotion** Java's binary numeric promotion rules
         * apply to `==`, `<`, `>`, etc.
         * The narrower type is always widened to the wider type before the operation.
         * So `Math.round(n)` returns `long`, and `(int) n.doubleValue()` is an `int`,
         * but the `==` comparison is perfectly valid
         * - the `int` is promoted to `long` before comparing. No issue here.
         */
        Predicate<Double> pred =n -> Math.round(n) == (int) n.doubleValue();

        numbers.removeIf(pred);
        System.out.println(numbers);
    }


} /// :~