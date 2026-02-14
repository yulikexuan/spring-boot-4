//: spring.boot.di.domain.model.jdk25.hkj.optics.Company.java

package spring.boot.di.domain.model.jdk25.hkj.optics;


import java.util.List;


public record Company(String name, Address address, List<Department> departments) {

}
