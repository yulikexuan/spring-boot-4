//: spring.boot.di.domain.model.jdk25.hkj.optics.Shape.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import java.util.Optional;

import org.jspecify.annotations.NullMarked;


@NullMarked
public sealed interface Shape permits Shape.Circle, Shape.Rectangle, Shape.Triangle {

    record Circle(double radius) implements Shape {

        public static final class Lenses {

            public static Lens<Circle, Double> radius() {
                return Lens.of(Circle::radius, (r, c) -> new Circle(r));
            }
        }
    }

    record Rectangle(double width, double height) implements Shape {

        public static final class Lenses {

            public static Lens<Rectangle, Double> width() {
                return Lens.of(Rectangle::width, (w, r) -> new Rectangle(w, r.height()));
            }

            public static Lens<Rectangle, Double> height() {
                return Lens.of(Rectangle::height, (h, r) -> new Rectangle(r.width(), h));
            }
        }
    }

    record Triangle(double a, double b, double c) implements Shape {

        public static final class Lenses {

            public static Lens<Triangle, Double> a() {
                return Lens.of(Triangle::a, (val, t) -> new Triangle(val, t.b(), t.c()));
            }

            public static Lens<Triangle, Double> b() {
                return Lens.of(Triangle::b, (val, t) -> new Triangle(t.a(), val, t.c()));
            }

            public static Lens<Triangle, Double> c() {
                return Lens.of(Triangle::c, (val, t) -> new Triangle(t.a(), t.b(), val));
            }
        }
    }

    static final class Prisms {

        public static Prism<Shape, Circle> circle() {
            return Prism.of(
                s -> s instanceof Circle c ? Optional.of(c) : Optional.empty(),
                c -> c
            );
        }

        public static Prism<Shape, Rectangle> rectangle() {
            return Prism.of(
                s -> s instanceof Rectangle r ? Optional.of(r) : Optional.empty(),
                r -> r
            );
        }

        public static Prism<Shape, Triangle> triangle() {
            return Prism.of(
                s -> s instanceof Triangle t ? Optional.of(t) : Optional.empty(),
                t -> t
            );
        }
    }
}
