//: spring.boot.sfg7.domain.school.repository.StudentRepository.java

package spring.boot.sfg7.domain.school.repository;


import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.domain.school.model.Student;


public interface StudentRepository extends ListCrudRepository<Student, Long> {

} /// :~