//: spring.boot.di.domain.model.jdk25.hkj.optics.Prism.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import java.util.Optional;
import java.util.function.Function;

import lombok.NonNull;
import org.jspecify.annotations.NullMarked;


/*
 * A Prism focuses on one variant of a sum type (sealed interface).
 *   - preview: tries to extract the focus, returning Optional.empty() when it misses
 *   - review:  constructs the whole from the focus
 */
@NullMarked
public record Prism<S, A>(Function<S, Optional<A>> preview, Function<A, S> review) {

    public static <S, A> Prism<S, A> of(
            Function<S, Optional<A>> preview, Function<A, S> review) {
        return new Prism<>(preview, review);
    }

    public Optional<A> getOptional(@NonNull S whole) { return preview.apply(whole); }

    public S build(A value) { return review.apply(value); }

    public boolean matches(S whole) { return getOptional(whole).isPresent(); }

    public S modify(Function<A, A> f, S whole) {
        return getOptional(whole).map(a -> build(f.apply(a))).orElse(whole);
    }

    public <B> Prism<S, B> andThen(Prism<A, B> other) {
        return Prism.of(
            s -> this.getOptional(s).flatMap(other::getOptional),
            b -> this.build(other.build(b))
        );
    }
}
