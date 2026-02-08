//: spring.boot.sfg7.rest.mvc.domain.customer.repository.CustomerRepository.java

package spring.boot.sfg7.rest.mvc.domain.customer.repository;


import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.rest.mvc.domain.customer.model.Customer;


public interface CustomerRepository extends ListCrudRepository<Customer, UUID> {

}
