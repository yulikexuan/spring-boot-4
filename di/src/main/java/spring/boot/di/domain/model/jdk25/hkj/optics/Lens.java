//: spring.boot.di.domain.model.jdk25.hkj.optics.Lens.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.NonNull;
import org.jspecify.annotations.NullMarked;


/*
 * The pattern is mechanical:
 *   - The getter is the record accessor
 *   - The setter creates a new record with one field changed
 */
@NullMarked
public record Lens<S, A>(Function<S, A> getter, BiFunction<A, S, S> setter) {

    public static <S, A> Lens<S, A> of(
            Function<S, A> getter, BiFunction<A, S, S> setter) {

        return new Lens<>(getter, setter);
    }

    public A get(@NonNull S whole) {
        return getter.apply(whole);
    }

    public S set(A newValue, S whole) {
        return setter.apply(newValue, whole);
    }

    public S modify(Function<A, A> f, S whole) {
        return set(f.apply(get(whole)), whole);
    }

    public <B> Lens<S, B> andThen(Lens<A, B> other) {
        return Lens.of(
                s -> other.get(this.get(s)),
                (b, s) -> this.set(other.set(b, this.get(s)), s)
        );
    }
}
