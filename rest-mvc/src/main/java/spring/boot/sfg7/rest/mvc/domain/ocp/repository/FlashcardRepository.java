//: spring.boot.sfg7.rest.mvc.domain.ocp.repository.FlashcardRepository.java

package spring.boot.sfg7.rest.mvc.domain.ocp.repository;


import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard;


public interface FlashcardRepository extends ListCrudRepository<Flashcard, Long> {
}
