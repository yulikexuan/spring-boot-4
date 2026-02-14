//: spring.boot.sfg7.data.domain.optics.Employee.java

package spring.boot.sfg7.data.domain.optics;


import org.higherkindedj.optics.annotations.GenerateLenses;


@GenerateLenses
public record Employee(String id, String name, Address address) {

}
