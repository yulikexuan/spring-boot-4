//: spring.boot.sfg7.data.domain.optics.traversals.Course.java

package spring.boot.sfg7.data.domain.optics.traversals;


import java.util.List;

import org.higherkindedj.optics.annotations.GenerateLenses;


@GenerateLenses
public record Course(String name, List<Student> students) {

}
