//: spring.boot.sfg.domain.Author.java

package spring.boot.sfg7.domain.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("author")
public record Author(
        @Id Long id,
        @Column("first_name") String firstName,
        @Column("last_name") String lastName) {


    public static Author of(String firstName, String lastName) {
        return new Author(null, firstName, lastName);
    }
}
