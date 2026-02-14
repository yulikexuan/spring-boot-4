//: spring.boot.sfg7.data.domain.optics.AddressTest.java

package spring.boot.sfg7.data.domain.optics;


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

}
