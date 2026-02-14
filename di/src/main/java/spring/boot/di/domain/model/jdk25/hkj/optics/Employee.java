//: spring.boot.di.domain.model.jdk25.hkj.optics.Employee.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


public record Employee(Long id, String name, Address address) {

    public static Employee of(Long id, String name, Address address) {
        return new Employee(id, name, address);
    }

    public static Employee applyNewAddress(Address newAddress, Employee employee) {
        return Employee.of(employee.id, employee.name, newAddress);
    }

    public static final class Lenses {

        public static Lens<Employee, Address> address() {
            return Lens.of(Employee::address, Employee::applyNewAddress);
        }

    }

}
