//: spring.boot.sfg7.rest.mvc.domain.beer.dto.BeerMapper.java

package spring.boot.sfg7.rest.mvc.domain.beer.dto;


import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.boot.sfg7.rest.mvc.domain.beer.model.Beer;


@NullMarked
@Mapper(componentModel = "spring")
public interface BeerMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "beerName", target = "beerName")
    @Mapping(source = "beerStyle", target = "beerStyle")
    @Mapping(source = "upc", target = "upc")
    @Mapping(source = "quantityOnHand", target = "quantityOnHand")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updateDate", target = "updateDate")
    BeerDto toDto(Beer beer);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "beerName", target = "beerName")
    @Mapping(source = "beerStyle", target = "beerStyle")
    @Mapping(source = "upc", target = "upc")
    @Mapping(source = "quantityOnHand", target = "quantityOnHand")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updateDate", target = "updateDate")
    Beer toEntity(BeerDto dto);

} /// :~
