package spring.boot.sfg7.domain.school.model;


import org.springframework.data.relational.core.mapping.Column;


// This is a simple value object representing the composite key
// (studentId + courseId) from the database
public record EnrollmentId (
        @Column("course_id") Long courseId,
        @Column("student_id") Long studentId) {

}