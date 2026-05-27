//: guru.modulith.domain.product.service.ProductMapper.java

package guru.modulith.product.domain.service;


import guru.modulith.product.domain.dto.ProductDto;
import guru.modulith.product.domain.model.Product;
import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@NullMarked
@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "price", target = "price")
    ProductDto toDto(Product product);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "price", target = "price")
    Product toEntity(ProductDto dto);

}
