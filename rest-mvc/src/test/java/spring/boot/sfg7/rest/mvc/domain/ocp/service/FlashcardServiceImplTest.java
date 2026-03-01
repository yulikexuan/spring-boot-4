//: spring.boot.sfg7.rest.mvc.domain.ocp.service.FlashcardServiceImplTest.java

package spring.boot.sfg7.rest.mvc.domain.ocp.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard;
import spring.boot.sfg7.rest.mvc.domain.ocp.repository.FlashcardRepository;


@ExtendWith(MockitoExtension.class)
@DisplayName("Test FlashcardServiceImpl - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FlashcardServiceImplTest {

    @Mock
    private FlashcardRepository flashcardRepository;

    @InjectMocks
    private FlashcardServiceImpl flashcardServiceImpl;

    @Test
    void saves_New_Flashcard_Successfully() {
        Flashcard input = Flashcard.of("What is OCP?", "Open/Closed Principle");
        Flashcard saved = Flashcard.builder()
                .id(1L)
                .question("What is OCP?")
                .answer("Open/Closed Principle")
                .weight(0)
                .build();

        given(flashcardRepository.save(any(Flashcard.class))).willReturn(saved);

        Flashcard result = flashcardServiceImpl.saveNewFlashcard(input);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.question()).isEqualTo("What is OCP?");
        assertThat(result.answer()).isEqualTo("Open/Closed Principle");
    }

    @Test
    void finds_All_Flashcards() {
        List<Flashcard> cards = List.of(
                Flashcard.builder().id(1L).question("Q1").answer("A1").weight(1).build(),
                Flashcard.builder().id(2L).question("Q2").answer("A2").weight(2).build());

        given(flashcardRepository.findAll()).willReturn(cards);

        List<Flashcard> result = flashcardServiceImpl.findAllFlashcards();

        assertThat(result).hasSize(2);
    }

    @Test
    void gets_Flashcard_By_Id() {
        Flashcard card = Flashcard.builder()
                .id(1L).question("Q").answer("A").weight(1).build();

        given(flashcardRepository.findById(1L)).willReturn(Optional.of(card));

        Flashcard result = flashcardServiceImpl.getFlashcardById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.question()).isEqualTo("Q");
    }

    @Test
    void throws_When_Flashcard_Not_Found() {
        given(flashcardRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> flashcardServiceImpl.getFlashcardById(99L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updates_Flashcard_By_Id() {
        Long id = 1L;
        Flashcard existing = Flashcard.builder()
                .id(id).question("Old Q").answer("Old A").weight(1).build();
        Flashcard update = Flashcard.builder()
                .question("New Q").answer("New A").weight(5).build();

        given(flashcardRepository.findById(id)).willReturn(Optional.of(existing));
        given(flashcardRepository.save(any(Flashcard.class))).willReturn(existing.updateWith(id, update));

        flashcardServiceImpl.updateFlashcardById(id, update);

        then(flashcardRepository).should().save(
                argThat(f -> "New Q".equals(f.question()) && "New A".equals(f.answer())));
    }

    @Test
    void patches_Flashcard_By_Id() {
        Long id = 1L;
        Flashcard existing = Flashcard.builder()
                .id(id).question("Old Q").answer("Old A").weight(1).build();
        Flashcard patch = Flashcard.builder()
                .question("Patched Q").answer("").weight(3).build();

        given(flashcardRepository.findById(id)).willReturn(Optional.of(existing));
        given(flashcardRepository.save(any(Flashcard.class))).willReturn(existing.patchWith(id, patch));

        flashcardServiceImpl.patchFlashcardById(id, patch);

        then(flashcardRepository).should().save(
                argThat(f -> "Patched Q".equals(f.question()) && "Old A".equals(f.answer())));
    }

    @Test
    void deletes_Flashcard_By_Id() {
        flashcardServiceImpl.deleteFlashcardById(1L);

        then(flashcardRepository).should().deleteById(1L);
    }
}
