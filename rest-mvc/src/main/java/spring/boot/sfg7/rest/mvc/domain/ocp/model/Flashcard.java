//: spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard.java

package spring.boot.sfg7.rest.mvc.domain.ocp.model;


import lombok.Builder;
import org.higherkindedj.optics.annotations.GenerateLenses;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;


@Builder
@NullMarked
@GenerateLenses
public record Flashcard(
        @Nullable Long id,
        String question,
        String answer,
        Integer weight) {

    public static Flashcard of(String question, String answer) {
        return new Flashcard(null, question, answer, 0);
    }

}
