package spring.boot.sfg7.domain.repository;


import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.domain.model.Book;
import spring.boot.sfg7.domain.model.BookDto;


public interface BookRepository extends ListCrudRepository<Book, Long> {

    /*
     * !!! Can perform SQL query and return DTO also !!!
     * The alias should be exactly the same as the field name in BookDto
     */
    @Query("SELECT book.title AS title, publisher.name AS publisher FROM book INNER JOIN publisher ON book.publisher_id = publisher.id")
    List<BookDto> findBooksWithPublisher();


}
