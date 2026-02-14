//: spring.boot.sfg7.data.domain.optics.Department.java

package spring.boot.sfg7.data.domain.optics;


import java.util.List;

import org.higherkindedj.optics.annotations.GenerateLenses;


@GenerateLenses
public record Department(String name, Employee manager, List<Employee> staff) {

}

