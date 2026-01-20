package spring.boot.sfg7.repository;


import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.domain.Book;


public interface BookRepository extends ListCrudRepository<Book, Long> {
}
