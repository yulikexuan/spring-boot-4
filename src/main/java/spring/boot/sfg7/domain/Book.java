//: spring.boot.sfg.domain.Book.java

package spring.boot.sfg7.domain;


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
        @MappedCollection(idColumn = "book_id") Set<AuthorRef> authors) {

    public static Book of(String title, String isbn, Set<AuthorRef> authors) {
        return new Book(null, title, isbn, authors);
    }

    public Book withAuthors(Set<AuthorRef> authors) {
        var authorsCopy = Set.copyOf(authors);
        return new Book(this.id, this.title, this.isbn, authorsCopy);
    }

}
