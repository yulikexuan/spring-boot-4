//: spring.boot.sfg7.data.domain.optics.traversals.Profile.java

package spring.boot.sfg7.data.domain.optics.traversals;


import org.higherkindedj.optics.annotations.GenerateLenses;


@GenerateLenses
public record Profile(Integer age, Gender gender) {

}
