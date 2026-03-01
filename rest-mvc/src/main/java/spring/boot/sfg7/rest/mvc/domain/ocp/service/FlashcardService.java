//: spring.boot.sfg7.rest.mvc.domain.ocp.service.FlashcardService.java

package spring.boot.sfg7.rest.mvc.domain.ocp.service;


import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard;
import spring.boot.sfg7.rest.mvc.domain.ocp.repository.FlashcardRepository;


public interface FlashcardService {

    Flashcard saveNewFlashcard(Flashcard flashcard);

    List<Flashcard> findAllFlashcards();

    Flashcard getFlashcardById(Long id);

    void updateFlashcardById(Long flashcardId, Flashcard flashcard);

    void deleteFlashcardById(Long flashcardId);

    void patchFlashcardById(Long flashcardId, Flashcard flashcard);
}


@Service
@NullMarked
@RequiredArgsConstructor
class FlashcardServiceImpl implements FlashcardService {

    private final FlashcardRepository flashcardRepository;

    @Override
    public Flashcard saveNewFlashcard(@NonNull Flashcard flashcard) {
        var data = Flashcard.builder()
                .question(flashcard.question())
                .answer(flashcard.answer())
                .weight(flashcard.weight())
                .build();
        return flashcardRepository.save(data);
    }

    @Override
    public List<Flashcard> findAllFlashcards() {
        return flashcardRepository.findAll();
    }

    @Override
    public Flashcard getFlashcardById(@NonNull Long id) {
        return flashcardRepository.findById(id).orElseThrow();
    }

    @Override
    public void updateFlashcardById(Long flashcardId, Flashcard flashcard) {
        var existing = getFlashcardById(flashcardId);
        var updated = existing.updateWith(flashcardId, flashcard);
        flashcardRepository.save(updated);
    }

    @Override
    public void patchFlashcardById(Long flashcardId, Flashcard flashcard) {
        var existing = getFlashcardById(flashcardId);
        var patched = existing.patchWith(flashcardId, flashcard);
        flashcardRepository.save(patched);
    }

    @Override
    public void deleteFlashcardById(Long flashcardId) {
        flashcardRepository.deleteById(flashcardId);
    }
}
