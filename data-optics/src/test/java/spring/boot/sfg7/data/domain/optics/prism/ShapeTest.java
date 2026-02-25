//: spring.boot.sfg7.data.domain.optics.prism.ShapeTest.java

package spring.boot.sfg7.data.domain.optics.prism;


import org.higherkindedj.optics.Affine;
import org.higherkindedj.optics.Lens;
import org.higherkindedj.optics.Prism;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import spring.boot.sfg7.data.domain.optics.prism.Shape.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
@DisplayName("Test Prisms & Affine of Shape Interface - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ShapeTest {

    private Shape shape;
    private Circle circle;
    private Rectangle rectangle;
    private Triangle triangle;

    private Prism<Shape, Circle> circlePrism;
    private Prism<Shape, Rectangle> rectanglePrism;
    private Prism<Shape, Triangle> trianglePrism;

    @BeforeEach
    void setUp() {

        this.circle = Circle.of(10.0);
        this.rectangle = Rectangle.of(10.0, 20.0);
        this.triangle = Triangle.of(10.0, 20.0, 30.0);

        this.circlePrism = ShapePrisms.circle();
        this.rectanglePrism = ShapePrisms.rectangle();
        this.trianglePrism = ShapePrisms.triangle();
    }

    @Nested
    @DisplayName("Test Prisms - ")
    class PrismTest {

        @Test
        void prism_Is_Able_To_Know_If_An_Instance_Of_Shape_Is_Circle_Or_Not() {

            // Given
            shape = circle;

            // When
            Optional<Circle> circleOpt = circlePrism.getOptional(shape);

            // Then
            assertThat(circleOpt).isPresent();

            // Given

            // When
            var rectangleOpt = rectanglePrism.getOptional(shape);

            // Then
            assertThat(rectangleOpt).isNotPresent();
        }

        @Test
        void always_Construct_The_Sum_Type_From_The_Variant_And_Always_Succeeds() {

            // Given

            // When
            Shape newShape = circlePrism.build(Circle.of(11.0));

            // Then
            assertThat(newShape).isInstanceOf(Circle.class);
        }

        @ParameterizedTest
        @MethodSource("provideShapesForCirclePrism")
        void able_To_Check_If_The_Prism_Matches(Shape shape, boolean expectedMatch) {

            // Given

            // When
            var actualMatch = circlePrism.matches(shape);

            // Then
            assertThat(actualMatch).isEqualTo(expectedMatch);
        }


        private static Stream<Arguments> provideShapesForCirclePrism() {
            return Stream.of(
                    Arguments.of(Circle.of(10.0), true),
                    Arguments.of(Rectangle.of(10.0, 20.0), false),
                    Arguments.of(Triangle.of(10.0, 20.0, 30.0), false)
            );
        }

        @Test
        void able_To_Modify_If_Matched_And_Leave_Unchanged_If_Otherwise() {

            // Given
            double radius = 20.0;

            // When
            Shape newShape = circlePrism.modify(
                    c -> c.withRadius(c.radius() * 2.0),
                    circle);

            // Then
            assertThat(circlePrism.matches(newShape)).isTrue();
            circlePrism.getOptional(newShape).ifPresent(
                    c -> assertThat(c.radius()).isEqualTo(radius));
        }

    } //: End of class PrismTest

    /*
     * Composing a Prism with a Lens yields an Affine
     * This reflects that we might find zero elements (if it’s not a circle)
     * or one element (if it is).
     * The affine handles both cases elegantly.
     */
    @Nested
    @DisplayName("Test Affine - ")
    class AffineTest {

        private Lens<Circle, Double> circleRadiusLens;
        private Affine<Shape, Double> shapeRadiusAffine;

        @BeforeEach
        void setUp() {
            this.circleRadiusLens = CircleLenses.radius();
            this.shapeRadiusAffine = circlePrism.andThen(circleRadiusLens);
        }

        @Test
        void able_To_Get_Radius_Back_From_A_Shape_Which_Is_A_Circle_With_Radius_Affine() {

            // Given
            shape = circle;

            // When
            Optional<Double> radiusOpt = shapeRadiusAffine.getOptional(shape);

            // Then
            assertThat(radiusOpt).contains(10.0d);
        }

        @Test
        void able_To_Modify_Radius_Of_A_Shape_Which_Is_A_Circle_With_Radius_Affine() {

            // Given
            double newRadius = 20.0;
            shape = circle;

            // When
            Shape newShape = shapeRadiusAffine.modify(r -> r * 2.0, shape);

            // Then
            assertThat(circlePrism.matches(newShape)).isTrue();
            circlePrism.getOptional(newShape).ifPresent(
                    c -> assertThat(c.radius()).isEqualTo(newRadius));

            // Given
            double newRadius2 = 30.0;
            shape = triangle;

            // When
            newShape = shapeRadiusAffine.modify(r -> r * 2.0, shape);

            // Then
            assertThat(circlePrism.matches(newShape)).isFalse();
            assertThat(newShape).isSameAs(shape);
        }

    } // End of AffineTest

}
