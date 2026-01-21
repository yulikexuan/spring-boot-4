//: spring.boot.sfg.domain.Book.java

package spring.boot.sfg7.domain.model;


import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;


// This is the Aggregate Root
@Table("book")
public record Book(
        @Id Long id,
        String title,
        String isbn,
        // Spring Data JDBC uses a simple approach: a Long field with the naming
        // convention <entityName>Id is automatically treated as a foreign key reference,
        // no additional annotations like @ManyToOne or @JoinColumn
        // (which are JPA annotations) are needed.
        // In Spring Data JDBC, the aggregate relationship is unidirectional.
        // Since Book is the aggregate root, and it references Publisher via
        // publisherId, the relationship flows from Book → Publisher.
        // Publisher should not have a collection of Book entities because:
        //   1. Book is the aggregate root
        //      - It owns the relationship with its authors (using @MappedCollection)
        //   2. Publisher is a separate aggregate
        //      - It's referenced by ID only, maintaining loose coupling
        //   3. Adding @MappedCollection to Publisher would make it the aggregate root for books,
        //      which contradicts the current design
        //      - use the repository query method to find out books by publisherId
        // This maintains proper aggregate boundaries and follows Spring Data JDBC best practices.
        @NonNull @lombok.NonNull Long publisherId, // Foreign key to Publisher.id
        @MappedCollection(idColumn = "book_id") Set<AuthorRef> authors) {

    public static Book of(
            String title,
            String isbn,
            @NonNull Long publisherId,
            Set<AuthorRef> authors) {
        return new Book(null, title, isbn, publisherId, authors);
    }

    public Book withAuthors(Set<AuthorRef> authors) {
        var authorsCopy = Set.copyOf(authors);
        return new Book(this.id,
                this.title,
                this.isbn,
                this.publisherId,
                authorsCopy);
    }

}
