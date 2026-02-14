//: spring.boot.di.domain.model.jdk25.math.Maths.java

package spring.boot.di.domain.model.jdk25.math;


import java.util.ArrayList;


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


} /// :~