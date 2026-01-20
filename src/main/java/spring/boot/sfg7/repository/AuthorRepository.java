package spring.boot.sfg7.repository;


import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.domain.Author;


public interface AuthorRepository extends ListCrudRepository<Author, Long> {
}
