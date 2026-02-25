//: spring.boot.sfg7.data.domain.optics.lens.AddressTest.java

package spring.boot.sfg7.data.domain.optics.lens;


import static org.assertj.core.api.Assertions.assertThat;

import org.higherkindedj.optics.Lens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;


@DisplayName("Test Address Lenses Class - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AddressTest {

    private String street;
    private String city;
    private String postcode;
    private Address address;
    private Lens<Address, String> streetLens;

    @BeforeEach
    void setUp() {
        street = "100 New Street";
        city = "Los Angeles";
        postcode = "90210";
        address = Address.of(street, city, postcode);
        streetLens = AddressLenses.street();
    }

    @Test
    void use_Lens_To_Fetch_Street() {
        assertThat(streetLens.get(address)).isEqualTo(street);
    }

    @Test
    void able_To_Change_Street_Easily() {

        // Given
        String newStreet = "1090 Old Street";

        // When
        Address newAddress = streetLens.set(newStreet, address);

        // Then
        assertThat(newAddress).isNotSameAs(address);
        assertThat(newAddress.street()).isEqualTo(newStreet);
    }

    @Test
    void able_To_Modify_Street() {

        // Given

        // When
        Address modifiedAddress = streetLens.modify(String::toUpperCase, address);

        // Then
        assertThat(modifiedAddress).isNotSameAs(address);
        assertThat(modifiedAddress.street()).isEqualTo(street.toUpperCase());
    }

}
