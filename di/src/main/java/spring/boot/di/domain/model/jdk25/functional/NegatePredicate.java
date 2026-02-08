//: spring.boot.di.domain.model.jdk25.functional.NegatePredicate.java

package spring.boot.di.domain.model.jdk25.functional;


import java.util.function.Predicate;


class NegatePredicate {

    static void main(String[] args) {
        negateIsNoPredicate();
    }

    static void negateIsNoPredicate() {
        Predicate<Integer> intPredicate = x -> x % 2 == 0;
        var oddPredicate = intPredicate.negate();
        System.out.println(oddPredicate.test(10));
    }

} /// :~