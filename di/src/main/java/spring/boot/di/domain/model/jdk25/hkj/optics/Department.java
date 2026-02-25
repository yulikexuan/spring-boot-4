//: spring.boot.di.domain.model.jdk25.hkj.optics.Department.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import java.util.List;


public record Department(String name, Employee manager, List<Employee> staff) {

    public static Department of(String name, Employee manager, List<Employee> staff) {
        return new Department(name, manager, staff);
    }

    public static final class Traversals {

        public static Traversal<Department, Employee> staff() {
            return Traversal.of(
                Department::staff,
                (f, dept) -> Department.of(
                    dept.name(),
                    dept.manager(),
                    dept.staff().stream().map(f).toList()
                )
            );
        }
    }
}
