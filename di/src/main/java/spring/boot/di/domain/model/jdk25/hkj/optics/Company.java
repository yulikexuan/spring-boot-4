//: spring.boot.di.domain.model.jdk25.hkj.optics.Company.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import java.util.List;


public record Company(String name, Address address, List<Department> departments) {

    public static Company of(String name, Address address, List<Department> departments) {
        return new Company(name, address, departments);
    }

    public static final class Traversals {

        public static Traversal<Company, Department> departments() {
            return Traversal.of(
                Company::departments,
                (f, company) -> Company.of(
                    company.name(),
                    company.address(),
                    company.departments().stream().map(f).toList()
                )
            );
        }
    }
}
