//: spring.boot.sfg7.rest.mvc.domain.beer.model.Beer.java

package spring.boot.sfg7.rest.mvc.domain.beer.model;


import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;


@Builder
@Table("beer")
public record Beer(
        @Id UUID id,
        Integer version,
        @Column("beer_name") String beerName,
        @Column("beer_style") BeerStyle beerStyle,
        String upc,
        @Column("quantity_on_hand") Integer quantityOnHand,
        Integer price,
        @Column("created_date") Instant createdDate,
        @Column("update_date") Instant updateDate) {


    public Beer updateWith(@NonNull UUID beerId, @NonNull Beer beer) {

        return Beer.builder()
                .id(beerId)
                .version(beer.version())
                .beerName(beer.beerName())
                .beerStyle(beer.beerStyle())
                .upc(beer.upc())
                .quantityOnHand(beer.quantityOnHand())
                .price(beer.price())
                .createdDate(this.createdDate)
                .updateDate(Instant.now())
                .build();
    }

    public Beer patchWith(@NonNull UUID beerId, @NonNull Beer beer) {
        return Beer.builder()
                .id(beerId)
                .beerName(StringUtils.hasText(beer.beerName()) ?
                        beer.beerName() : this.beerName())
                .version(this.version())
                .beerStyle(beer.beerStyle() != null ?
                        beer.beerStyle() : this.beerStyle())
                .upc(StringUtils.hasText(beer.upc()) ? beer.upc() : this.upc())
                .quantityOnHand(beer.quantityOnHand() != null ?
                        beer.quantityOnHand() : this.quantityOnHand())
                .price(beer.price() != null ? beer.price() : this.price())
                .createdDate(this.createdDate())
                .updateDate(Instant.now())
                .build();
    }
}
