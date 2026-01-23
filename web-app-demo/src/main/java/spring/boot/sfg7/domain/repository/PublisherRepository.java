//: spring.boot.sfg7.domain.repository.PublisherRepository.java

package spring.boot.sfg7.domain.repository;


import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.domain.model.Publisher;


public interface PublisherRepository extends ListCrudRepository<Publisher, Long> {

}
