package spring.boot.sfg7.domain.model;


import lombok.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@NullMarked
@Table(name = "book_author")
public record AuthorRef(@NonNull @Column("author_id") Long authorId) {

}