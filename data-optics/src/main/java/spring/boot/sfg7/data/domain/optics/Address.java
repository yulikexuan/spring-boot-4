//: spring.boot.sfg7.data.domain.optics.Address.java

package spring.boot.sfg7.data.domain.optics;


import lombok.Builder;
import org.higherkindedj.optics.annotations.GenerateLenses;


@Builder
@GenerateLenses
public record Address(String street, String city, String postcode) {

    public static Address of(String street, String city, String postcode) {
        return Address.builder()
                .street(street)
                .city(city)
                .postcode(postcode)
                .build();
    }

    public static Address withStreet(String newStreet, Address address) {
        return Address.of(newStreet, address.city(), address.postcode());
    }

}
