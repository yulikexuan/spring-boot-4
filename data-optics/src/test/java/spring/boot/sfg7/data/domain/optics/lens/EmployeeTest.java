//: spring.boot.sfg7.data.domain.optics.lens.EmployeeTest.java

package spring.boot.sfg7.data.domain.optics.lens;


import static org.assertj.core.api.Assertions.assertThat;

import org.higherkindedj.optics.Lens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@DisplayName("Test Lens Composition with Employee Class - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class EmployeeTest {

    private String street;
    private String city;
    private String postcode;

    private String employeeId;
    private String employeeName;
    private Address address;

    private Employee employee;

    private Lens<Address, String> streetLens;
    private Lens<Employee, Address> addressLens;
    private Lens<Employee, String> employeeAddressStreetLens;

    @BeforeEach
    void setUp() {

        street = "100 New Street";
        city = "Los Angeles";
        postcode = "90210";
        address = Address.of(street, city, postcode);

        employeeId = "12345";
        employeeName = "John Doe";

        employee = Employee.of(employeeId, employeeName, address);

        streetLens = AddressLenses.street();
        addressLens =  EmployeeLenses.address();

        employeeAddressStreetLens = addressLens.andThen(streetLens);
    }

    /*
     * Each composed lens handles all the intermediate reconstruction
     * automatically.
     */

    @Test
    void lens_Composition_Fetch_Street_Of_Employee_Easily() {

        // Given

        // When
        String employeeStreet = employeeAddressStreetLens.get(employee);

        // Then
        assertThat(employeeStreet).isEqualTo(street);
    }

    @Test
    void easily_Reset_Street_For_Employee_With_Lens_Composition() {

        // Given
        String newStreet = "200 Oak Avenue";

        // When
        Employee updatedEmployee = employeeAddressStreetLens.set(newStreet, employee);

        // Then
        assertThat(updatedEmployee).isNotSameAs(employee);
        assertThat(updatedEmployee.address().street()).isEqualTo(newStreet);
    }

    @Nested
    class LensLawsTest {

        @Test
        void the_Structure_Is_Unchanged_If_Get_A_Value_And_Then_Set_It_Back() {

            // Given

            // When
            Address newAddress = streetLens.set(streetLens.get(address), address);

            // Then
            assertThat(newAddress).isEqualTo(address);
            assertThat(newAddress.street()).isEqualTo(address.street());
        }

        @Test
        void will_Get_The_Same_Value_Back_After_Set_The_Same_Value() {

            // Given

            // When
            String newSetStreet = streetLens.get(streetLens.set("Long Street", address));

            // Then
            assertThat(newSetStreet).isEqualTo("Long Street");
        }

        @Test
        void will_Get_The_Final_Set_Val_When_Setting_Twice_With_Different_Vals() {

            // Given

            // When
            Address finalAddress = streetLens.set("Final Street",
                    streetLens.set("Intermediate Street", address));

            // Then
            assertThat(finalAddress.street()).isEqualTo("Final Street");
        }

    }

}
