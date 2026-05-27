//: guru.modulith.domain.product.service.ProductDto.java

package guru.modulith.product.domain.dto;


import java.util.UUID;


public record ProductDto(UUID id, String name, String description, long price) {

}
