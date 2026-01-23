//: spring.boot.sfg7.domain.model.Publisher.java

package spring.boot.sfg7.domain.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table("publisher")
public record Publisher(
        @Id Long id,
        String name,
        String address,
        String city,
        String state,
        String zip) {

}
