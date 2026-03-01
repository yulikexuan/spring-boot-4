//: spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard.java

package spring.boot.sfg7.rest.mvc.domain.ocp.model;


import lombok.Builder;
import lombok.NonNull;
import org.higherkindedj.optics.annotations.GenerateLenses;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;


@Builder
@NullMarked
@GenerateLenses
@Table("flashcard")
public record Flashcard(
        @Id @Nullable Long id,
        String question,
        String answer,
        Integer weight) {

    public static Flashcard of(String question, String answer) {
        return new Flashcard(null, question, answer, 0);
    }

    public Flashcard updateWith(@NonNull Long flashcardId, @NonNull Flashcard flashcard) {
        return Flashcard.builder()
                .id(flashcardId)
                .question(flashcard.question())
                .answer(flashcard.answer())
                .weight(flashcard.weight())
                .build();
    }

    public Flashcard patchWith(@NonNull Long flashcardId, @NonNull Flashcard flashcard) {
        return Flashcard.builder()
                .id(flashcardId)
                .question(StringUtils.hasText(flashcard.question()) ?
                        flashcard.question() : this.question())
                .answer(StringUtils.hasText(flashcard.answer()) ?
                        flashcard.answer() : this.answer())
                .weight(flashcard.weight() != null ?
                        flashcard.weight() : this.weight())
                .build();
    }
}
