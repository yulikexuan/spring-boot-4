//: spring.boot.sfg7.bootstrap.BootstrapData.java

package spring.boot.sfg7.bootstrap;


import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.domain.model.Author;
import spring.boot.sfg7.domain.model.AuthorRef;
import spring.boot.sfg7.domain.model.Book;
import spring.boot.sfg7.domain.model.Publisher;
import spring.boot.sfg7.domain.repository.AuthorRepository;
import spring.boot.sfg7.domain.repository.BookRepository;
import spring.boot.sfg7.domain.repository.PublisherRepository;


@Slf4j
@Service
@RequiredArgsConstructor
class BootstrapData implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;

    @Override
    public void run(String... args) throws Exception {

        // Create Publishers
        final Publisher manning = publisherRepository.save(new Publisher(
                null,
                "Manning Publications",
                "20 Baldwin Road, PO Box 761",
                "Shelter Island",
                "NY",
                "11964"));
        log.info(">>> Saved Publisher: {}", manning);

        final Publisher oreilly = publisherRepository.save(new Publisher(
                null,
                "O'Reilly Media",
                "141 Stony Circle, Suite 195",
                "Santa Rosa",
                "CA",
                "95401"));
        log.info(">>> Saved Publisher: {}", oreilly);

        // Create Book "Domain Driven Design"
        final Author eric = authorRepository.save(Author.of("Eric", "Evans"));
        final AuthorRef authorRef = new AuthorRef(eric.id());
        final Book ddd = bookRepository.save(Book.of(
                "Domain Driven Design",
                "123456789",
                manning.id(),
                Set.of(authorRef)));

        log.info(">>> Saved Book: {}", ddd);
        log.info(">>> Saved Author: {}", eric);

        // Create Book "J2EE Development without EJB"
        final Author rod = authorRepository.save(Author.of("Rod", "Johnson"));
        final AuthorRef rodRef = new AuthorRef(rod.id());
        final Book noEjb = bookRepository.save(Book.of(
                "J2EE Development without EJB",
                "987654321",
                oreilly.id(),
                Set.of(rodRef)));

        log.info(">>> Saved Book: {}", noEjb);
        log.info(">>> Saved Author: {}", rod);

    }

} /// :~