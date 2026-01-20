//: spring.boot.sfg7.bootstrap.BootstrapData.java

package spring.boot.sfg7.bootstrap;


import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.domain.Author;
import spring.boot.sfg7.domain.AuthorRef;
import spring.boot.sfg7.domain.Book;
import spring.boot.sfg7.repository.AuthorRepository;
import spring.boot.sfg7.repository.BookRepository;


@Slf4j
@Service
@RequiredArgsConstructor
class BootstrapData implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {

        final Author eric = authorRepository.save(Author.of("Eric", "Evans"));
        final AuthorRef authorRef = new AuthorRef(eric.id());
        final Book ddd = bookRepository.save(Book.of(
                "Domain Driven Design",
                "123456789",
                Set.of(authorRef)));

        log.info(">>> Saved Book: {}", ddd);
        log.info(">>> Saved Author: {}", eric);

        final Author rod = authorRepository.save(Author.of("Rod", "Johnson"));
        final AuthorRef rodRef = new AuthorRef(rod.id());
        final Book noEjb = bookRepository.save(Book.of(
                "J2EE Development without EJB",
                "987654321",
                Set.of(rodRef)));

        log.info(">>> Saved Book: {}", noEjb);
        log.info(">>> Saved Author: {}", rod);
    }

} /// :~