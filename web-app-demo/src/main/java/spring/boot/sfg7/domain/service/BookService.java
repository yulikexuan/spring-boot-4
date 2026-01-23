//: spring.boot.sfg7.domain.service.BookService.java

package spring.boot.sfg7.domain.service;


import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.domain.model.BookDto;
import spring.boot.sfg7.domain.repository.BookRepository;


public interface BookService {

    List<BookDto> findAllBooks();

}


@Service
@RequiredArgsConstructor
class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookToBookDtoMapper bookToBookDtoMapper;

    @Override
    public List<BookDto> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookToBookDtoMapper::map)
                .toList();
    }
}
