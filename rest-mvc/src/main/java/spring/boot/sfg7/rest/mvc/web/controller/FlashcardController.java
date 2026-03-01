//: spring.boot.sfg7.rest.mvc.web.controller.FlashcardController.java

package spring.boot.sfg7.rest.mvc.web.controller;


import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard;
import spring.boot.sfg7.rest.mvc.domain.ocp.service.FlashcardService;
import spring.boot.sfg7.rest.mvc.web.util.WebUtils;


@Slf4j
@RestController
@RequestMapping("/sfg7/api/v1/flashcard")
@RequiredArgsConstructor
class FlashcardController {

    private final FlashcardService flashcardService;

    @GetMapping
    public List<Flashcard> listFlashcards() {
        return flashcardService.findAllFlashcards();
    }

    @GetMapping("{flashcardId}")
    public Flashcard getFlashcardById(@PathVariable Long flashcardId) {
        log.debug("Get Flashcard by Id - in controller");
        return flashcardService.getFlashcardById(flashcardId);
    }

    @PostMapping
    public ResponseEntity<URI> saveNewFlashcard(@RequestBody Flashcard flashcard) {
        var newFlashcard = flashcardService.saveNewFlashcard(flashcard);
        URI location = WebUtils.buildLocation("/{flashcardId}", newFlashcard::id);
        return ResponseEntity.created(location).build();
    }

    @PutMapping("{flashcardId}")
    public ResponseEntity<Void> updateFlashcard(
            @PathVariable Long flashcardId, @RequestBody Flashcard flashcard) {
        flashcardService.updateFlashcardById(flashcardId, flashcard);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{flashcardId}")
    public ResponseEntity<Void> deleteFlashcardById(@PathVariable Long flashcardId) {
        flashcardService.deleteFlashcardById(flashcardId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{flashcardId}")
    public ResponseEntity<Void> patchFlashcard(
            @PathVariable Long flashcardId, @RequestBody Flashcard flashcard) {
        flashcardService.patchFlashcardById(flashcardId, flashcard);
        return ResponseEntity.noContent().build();
    }
} /// :~
