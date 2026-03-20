//: spring.boot.di.domain.model.jdk25.comparators.Comparators.java

package spring.boot.di.domain.model.jdk25.comparators;


import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

import java.util.Comparator;
import java.util.List;


class Comparators {

    static void main(String[] args) {

        List<Member> members = List.of(
                new Member("Alice", 25),
                new Member("Max", 28),
                new Member("Bob", 30),
                new Member("Charlie", 22),
                new Member("Alex", 22),
                new Member("Dave", 28),
                new Member("John", 30),
                new Member("Mike", 25));

        // Exception in thread "main" java.lang.UnsupportedOperationException
        // As members is immutable
        // members.sort(comparing(Member::age).thenComparing(Member::name));

        var newMembers = members.stream()
                .sorted(comparingInt(Member::age).thenComparing(Member::name))
                .toList();

        System.out.println(newMembers);
    }

}


record Member(String name, int age) {}

/// :~