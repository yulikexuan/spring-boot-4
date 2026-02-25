//: spring.boot.di.domain.model.jdk25.hkj.optics.ShapeTest.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

import spring.boot.di.domain.model.jdk25.hkj.optics.Shape.Circle;
import spring.boot.di.domain.model.jdk25.hkj.optics.Shape.Rectangle;
import spring.boot.di.domain.model.jdk25.hkj.optics.Shape.Triangle;


@DisplayName("Test spring.boot.di.domain.model.jdk25.hkj.optics.Shape Prisms - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ShapeTest {

    private Shape circle;
    private Shape rectangle;
    private Shape triangle;

    private Prism<Shape, Circle> circlePrism;
    private Prism<Shape, Rectangle> rectanglePrism;
    private Prism<Shape, Triangle> trianglePrism;

    @BeforeEach
    void setUp() {
        circle = new Circle(5.0);
        rectangle = new Rectangle(4.0, 6.0);
        triangle = new Triangle(3.0, 4.0, 5.0);

        circlePrism = Shape.Prisms.circle();
        rectanglePrism = Shape.Prisms.rectangle();
        trianglePrism = Shape.Prisms.triangle();
    }

    @Test
    void getOptional_returns_value_when_variant_matches() {
        Optional<Circle> result = circlePrism.getOptional(circle);
        assertThat(result).contains(new Circle(5.0));
    }

    @Test
    void getOptional_returns_empty_when_variant_does_not_match() {
        Optional<Circle> result = circlePrism.getOptional(rectangle);
        assertThat(result).isEmpty();
    }

    @Test
    void build_constructs_shape_from_variant() {
        Shape built = circlePrism.build(new Circle(10.0));
        assertThat(built).isEqualTo(new Circle(10.0));
    }

    @Test
    void matches_returns_true_for_correct_variant() {
        assertThat(circlePrism.matches(circle)).isTrue();
        assertThat(rectanglePrism.matches(rectangle)).isTrue();
        assertThat(trianglePrism.matches(triangle)).isTrue();
    }

    @Test
    void matches_returns_false_for_wrong_variant() {
        assertThat(circlePrism.matches(rectangle)).isFalse();
        assertThat(rectanglePrism.matches(triangle)).isFalse();
        assertThat(trianglePrism.matches(circle)).isFalse();
    }

    @Test
    void modify_transforms_value_when_variant_matches() {
        // Double the radius of a circle
        Shape modified = circlePrism.modify(c -> new Circle(c.radius() * 2), circle);
        assertThat(modified).isEqualTo(new Circle(10.0));
    }

    @Test
    void modify_returns_unchanged_shape_when_variant_does_not_match() {
        // Trying to modify a Circle via the Rectangle prism → no-op
        Shape unchanged = rectanglePrism.modify(r -> new Rectangle(r.width() * 2, r.height()), circle);
        assertThat(unchanged).isEqualTo(circle);
    }

    @Test
    void rectangle_lenses_compose_with_prism_via_andThen() {
        // Use Rectangle Lenses to modify width
        Lens<Rectangle, Double> widthLens = Rectangle.Lenses.width();

        Shape rectShape = new Rectangle(3.0, 7.0);
        // Modify width through the prism
        Shape modified = rectanglePrism.modify(
            r -> widthLens.modify(w -> w * 2, r),
            rectShape
        );
        assertThat(modified).isEqualTo(new Rectangle(6.0, 7.0));
    }
}
