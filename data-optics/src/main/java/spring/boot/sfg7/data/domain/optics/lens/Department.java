//: spring.boot.sfg7.data.domain.optics.lens.Department.java

package spring.boot.sfg7.data.domain.optics.lens;


import java.util.List;

import org.higherkindedj.optics.annotations.GenerateLenses;


@GenerateLenses
public record Department(String name, Employee manager, List<Employee> staff) {

}

