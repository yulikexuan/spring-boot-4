//: guru.modulith.domain.product.model.Product.java

package guru.modulith.product.domain.model;


import java.util.UUID;


public record Product(UUID id, String name, String description, long price) {

}
