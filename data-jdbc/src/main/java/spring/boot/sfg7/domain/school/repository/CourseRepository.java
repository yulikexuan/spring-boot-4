//: spring.boot.sfg7.domain.school.repository.CourseRepository.java

package spring.boot.sfg7.domain.school.repository;


import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.domain.school.model.Course;


public interface CourseRepository extends ListCrudRepository<Course, Long> {

}
