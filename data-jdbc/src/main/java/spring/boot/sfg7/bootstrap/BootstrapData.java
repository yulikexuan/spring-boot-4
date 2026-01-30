//: spring.boot.sfg7.bootstrap.BootstrapData.java

package spring.boot.sfg7.bootstrap;


import java.time.Instant;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import spring.boot.sfg7.domain.school.model.Course;
import spring.boot.sfg7.domain.school.model.Enrollment;
import spring.boot.sfg7.domain.school.model.Student;
import spring.boot.sfg7.domain.school.repository.CourseRepository;
import spring.boot.sfg7.domain.school.repository.StudentRepository;


@Slf4j
@Service
@RequiredArgsConstructor
class BootstrapData implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {

        final var student1 = studentRepository.save(new Student(
                null,
                "John Doe",
                "john.doe@gmail.com"));

        Enrollment enrollment = new Enrollment(student1.id(), Instant.now());

        // Create Courses
        final var mathCourse = courseRepository.save(new Course(
                    null,
                    "Mathematics",
                    "Introduction to Calculus and Algebra",
                    Set.of(enrollment)));

        List<Course> courses = courseRepository.findAll();
        log.info(">>> All Courses: {}", courses);

        List<Student> students = studentRepository.findAll();
        log.info(">>> All Students: {}", students);
    }

} /// :~