//: spring.boot.sfg7.rest.mvc.domain.customer.model.Customer.java

package spring.boot.sfg7.rest.mvc.domain.customer.model;


import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;


@Builder
@Table("customer")
public record Customer(
        @Id UUID id,
        Integer version,
        String name,
        @Column("created_date") Instant createdDate,
        @Column("update_date") Instant updateDate) {

    public Customer updateWith(UUID customerId, Customer customer) {
        return Customer.builder()
                .id(customerId)
                .version(customer.version())
                .name(customer.name())
                .createdDate(customer.createdDate())
                .updateDate(Instant.now())
                .build();
    }

    public Customer patchWith(UUID customerId, Customer customer) {
        return Customer.builder()
                .id(customerId)
                .version(this.version())
                .name(StringUtils.hasText(customer.name()) ? customer.name() : this.name())
                .createdDate(this.createdDate())
                .updateDate(Instant.now())
                .build();
    }

}
