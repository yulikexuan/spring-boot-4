//: spring.boot.di.domain.model.jdk25.hkj.optics.AddressTest.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;


@DisplayName("Test Address Class - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AddressTest {

    static final String STREET_TEMPLATE = "%d %s";
    private int number;
    private String streetName;
    private String street;
    private String city;
    private int postcode;

    private Address address;

    private ThreadLocalRandom random;

    @BeforeEach
    void setUp() {
        random = ThreadLocalRandom.current();
        number = ThreadLocalRandom.current().nextInt(1000, 10000);
        streetName = "Main Street";
        street = STREET_TEMPLATE.formatted(number, streetName);
        city = "Los Angeles";
        postcode = ThreadLocalRandom.current().nextInt(10000, 100000);
        address = new Address(street, city, Integer.toString(postcode));
    }

    @Test
    void able_To_Get_A_Single_Field_Val_With_Lens() {

        // Given
        var streetLens = Address.Lenses.street();

        // When
        var currentStreet = streetLens.get(address);

        // Then
        assertThat(currentStreet).isEqualTo(street);
    }

}
