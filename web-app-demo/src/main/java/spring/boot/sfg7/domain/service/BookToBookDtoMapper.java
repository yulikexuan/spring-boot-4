//: spring.boot.sfg7.domain.service.BookToBookDtoMapper.java

package spring.boot.sfg7.domain.service;


import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.domain.model.Author;
import spring.boot.sfg7.domain.model.AuthorRef;
import spring.boot.sfg7.domain.model.Book;
import spring.boot.sfg7.domain.model.BookDto;
import spring.boot.sfg7.domain.model.Publisher;
import spring.boot.sfg7.domain.repository.AuthorRepository;
import spring.boot.sfg7.domain.repository.PublisherRepository;


public interface BookToBookDtoMapper {

    BookDto map(Book book);

}

@Service
@NullMarked
@RequiredArgsConstructor
class BookToBookDtoMapperImpl implements BookToBookDtoMapper {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    @Override
    public BookDto map(@NonNull final Book book) {

        var publisher = publisherRepository.findById(book.publisherId())
                .map(Publisher::name)
                .orElse("Unknown Publisher");

        var authorNames = book.authors().stream()
                .map(AuthorRef::authorId)
                .map(authorRepository::findById)
                .flatMap(java.util.Optional::stream)
                .map(Author::fullName)
                .collect(Collectors.joining(", "));

        return new BookDto(book.title(), book.isbn(), publisher, authorNames);
    }

}
