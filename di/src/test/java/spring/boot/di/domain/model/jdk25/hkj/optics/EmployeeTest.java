//: spring.boot.di.domain.model.jdk25.hkj.optics.EmployeeTest.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;


@DisplayName("Test spring.boot.di.domain.model.jdk25.hkj.optics.Employee Class - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class EmployeeTest {

    static final String STREET_TEMPLATE = "%d %s";

    private int number;
    private String streetName;
    private String street;
    private String city;
    private int postcode;
    private Address address;

    private Long employeeId;
    private String employeeName;
    private Employee employee;

    private ThreadLocalRandom random;

    private void initAddress() {
        random = ThreadLocalRandom.current();
        number = ThreadLocalRandom.current().nextInt(1000, 10000);
        streetName = "Main Street";
        street = STREET_TEMPLATE.formatted(number, streetName);
        city = "Los Angeles";
        postcode = ThreadLocalRandom.current().nextInt(10000, 100000);
        address = new Address(street, city, Integer.toString(postcode));
    }

    @BeforeEach
    void setUp() {
        initAddress();
        employee = Employee.of(employeeId, employeeName, address);
    }

    @Test
    void able_To_Modify_Street_Of_Employee() {

        // Given
        Lens<Employee, Address> addressLens = Employee.Lenses.address();
        Lens<Address, String> streetLens = Address.Lenses.street();
        Lens<Employee, String> employeeStreetLens = addressLens.andThen(streetLens);

        String employeeStreet = employeeStreetLens.get(employee);
        final String newStreet = "1234 New Street";

        // When
        Employee updatedEmployee = employeeStreetLens.set(newStreet, employee);

        // Then
        String updatedEmployeeStreet = employeeStreetLens.get(updatedEmployee);
        System.out.println(">>> updatedEmployeeStreet: " + updatedEmployeeStreet);

        // When
        Employee modifiedEmployee = employeeStreetLens.modify(
                String::toUpperCase, updatedEmployee);

        // Then
        String modifiedEmployeeStreet = employeeStreetLens.get(modifiedEmployee);
        assertThat(modifiedEmployeeStreet).isEqualTo(newStreet.toUpperCase());
    }

}
