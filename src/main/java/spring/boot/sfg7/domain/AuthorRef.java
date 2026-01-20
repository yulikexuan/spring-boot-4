package spring.boot.sfg7.domain;


import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "book_author")
public record AuthorRef(@Column("author_id") Long authorId) {

}