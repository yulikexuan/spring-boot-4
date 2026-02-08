//: spring.boot.sfg7.rest.mvc.domain.beer.repository.BeerRepository.java

package spring.boot.sfg7.rest.mvc.domain.beer.repository;


import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;
import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;


public interface BeerRepository extends ListCrudRepository<Beer, UUID> {

} /// :~