package spring.boot.sfg7.domain.school.model;


import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;


@Table(name = "course")
public record Course(
        // Marks id as the primary key; Spring Data JDBC will autopopulate it
        // from database-generated values
        @Id Long id,
        @NonNull String name,
        String description,
        // Tells Spring Data JDBC that this Set of Enrollment entities is part
        // of the Course aggregate;
        // The idColumn specifies which foreign key column in the enrollment table
        // references back to this course
        // This establishes the aggregate relationship with enrollments via
        // the course_id foreign ke
        @MappedCollection(idColumn = "course_id") Set<Enrollment> enrollments) {
}