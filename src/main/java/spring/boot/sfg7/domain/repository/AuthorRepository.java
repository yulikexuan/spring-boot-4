package spring.boot.sfg7.domain.repository;


import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.domain.model.Author;


public interface AuthorRepository extends ListCrudRepository<Author, Long> {
}
