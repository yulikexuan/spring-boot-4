//: spring.boot.sfg7.rest.mvc.domain.customer.dto.CustomerDto.java

package spring.boot.sfg7.rest.mvc.domain.customer.dto;


import java.time.Instant;
import java.util.UUID;

import lombok.Builder;


@Builder
public record CustomerDto(
        UUID id,
        Integer version,
        String name,
        Instant createdDate,
        Instant updateDate) {

} /// :~
