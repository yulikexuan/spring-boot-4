//: spring.boot.sfg7.rest.mvc.domain.beer.dto.BeerDto.java

package spring.boot.sfg7.rest.mvc.domain.beer.dto;


import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import spring.boot.sfg7.rest.mvc.domain.beer.model.BeerStyle;


@Builder
public record BeerDto(
        UUID id,
        Integer version,
        String beerName,
        BeerStyle beerStyle,
        String upc,
        Integer quantityOnHand,
        Integer price,
        Instant createdDate,
        Instant updateDate) {

} /// :~
