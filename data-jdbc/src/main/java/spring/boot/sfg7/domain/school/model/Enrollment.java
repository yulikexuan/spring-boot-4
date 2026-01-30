//: spring.boot.sfg7.domain.school.model.Enrollment.java

package spring.boot.sfg7.domain.school.model;


import java.time.Instant;

import lombok.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@NullMarked
@Table(name = "enrollment")
public record Enrollment(
        @NonNull @Column("student_id") Long studentId,
        @Column("enrolled_at") Instant enrolledAt) {

}
