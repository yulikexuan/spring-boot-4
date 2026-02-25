//: spring.boot.sfg7.data.domain.optics.traversals.Student.java

package spring.boot.sfg7.data.domain.optics.traversals;


import org.higherkindedj.optics.annotations.GenerateLenses;


@GenerateLenses
public record Student(String name, Profile profile, int score) {

}
