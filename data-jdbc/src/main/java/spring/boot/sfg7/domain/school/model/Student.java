package spring.boot.sfg7.domain.school.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "student")
public record Student(
        // Marks id as the primary key; Spring Data JDBC will autopopulate it
        // from database-generated values
        @Id Long id,
        String name,
        String email) {

}