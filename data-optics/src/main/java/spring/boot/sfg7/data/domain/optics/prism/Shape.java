//: spring.boot.sfg7.data.domain.optics.prism.Shape.java

package spring.boot.sfg7.data.domain.optics.prism;


import org.higherkindedj.optics.annotations.GenerateLenses;
import org.higherkindedj.optics.annotations.GeneratePrisms;


/*
 * // Generated: ShapePrisms.java
 * public final class ShapePrisms {
 *     public static Prism<Shape, Circle> circle() { ... }
 *     public static Prism<Shape, Rectangle> rectangle() { ... }
 *     public static Prism<Shape, Triangle> triangle() { ... }
 * }
 */
@GeneratePrisms
public sealed interface Shape {

    @GenerateLenses
    record Circle(double radius) implements Shape {
        public static Circle of(double radius) {
            return new Circle(radius);
        }
        public Circle withRadius(double newRadius) {
            return Circle.of(newRadius);
        }
    }

    @GenerateLenses
    record Rectangle(double width, double height) implements Shape {
        public static Rectangle of(double width, double height) {
            return new Rectangle(width, height);
        }
    }

    @GenerateLenses
    record Triangle(double a, double b, double c) implements Shape {
        public static Triangle of(double a, double b, double c) {
            return new Triangle(a, b, c);
        }
    }

}
