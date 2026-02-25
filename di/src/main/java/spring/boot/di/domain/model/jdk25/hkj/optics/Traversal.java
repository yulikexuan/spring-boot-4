//: spring.boot.di.domain.model.jdk25.hkj.optics.Traversal.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.NonNull;
import org.jspecify.annotations.NullMarked;


/*
 * A Traversal focuses on zero or more elements inside a structure.
 *   - toList:   extracts all focused elements as a List
 *   - modifier: applies a function to every focused element and returns the updated whole
 */
@NullMarked
public record Traversal<S, A>(
        Function<S, List<A>> toList,
        BiFunction<Function<A, A>, S, S> modifier) {

    public static <S, A> Traversal<S, A> of(
            Function<S, List<A>> toList,
            BiFunction<Function<A, A>, S, S> modifier) {
        return new Traversal<>(toList, modifier);
    }

    public List<A> getAll(@NonNull S whole) { return toList.apply(whole); }

    public S modify(Function<A, A> f, S whole) { return modifier.apply(f, whole); }

    public S set(A newValue, S whole) { return modify(__ -> newValue, whole); }

    public <B> Traversal<S, B> andThen(Traversal<A, B> other) {
        return Traversal.of(
            s -> this.getAll(s).stream().flatMap(a -> other.getAll(a).stream()).toList(),
            (f, s) -> this.modify(a -> other.modify(f, a), s)
        );
    }
}
