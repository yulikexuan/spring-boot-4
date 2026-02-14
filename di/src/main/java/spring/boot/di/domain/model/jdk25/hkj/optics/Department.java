//: spring.boot.di.domain.model.jdk25.hkj.optics.Department.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import java.util.List;


public record Department(String name, Employee manager, List<Employee> staff) {
}
