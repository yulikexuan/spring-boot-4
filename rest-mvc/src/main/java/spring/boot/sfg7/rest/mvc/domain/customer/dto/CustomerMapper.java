//: spring.boot.sfg7.rest.mvc.domain.customer.dto.CustomerMapper.java

package spring.boot.sfg7.rest.mvc.domain.customer.dto;


import org.jspecify.annotations.NullMarked;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.boot.sfg7.rest.mvc.domain.customer.model.Customer;


@NullMarked
@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updateDate", target = "updateDate")
    CustomerDto toDto(Customer customer);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "version", target = "version")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updateDate", target = "updateDate")
    Customer toEntity(CustomerDto dto);

} /// :~
