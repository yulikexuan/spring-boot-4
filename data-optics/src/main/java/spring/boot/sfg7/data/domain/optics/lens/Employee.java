//: spring.boot.sfg7.data.domain.optics.lens.Employee.java

package spring.boot.sfg7.data.domain.optics.lens;


import lombok.Builder;
import org.higherkindedj.optics.annotations.GenerateLenses;


@Builder
@GenerateLenses
public record Employee(String id, String name, Address address) {

    public static Employee of(String id, String name, Address address) {
        return Employee.builder()
                .id(id)
                .name(name)
                .address(address)
                .build();
    }

    public static Employee withAddress(Address address, Employee employee) {
        return Employee.of(employee.id(), employee.name(), address);
    }

}
