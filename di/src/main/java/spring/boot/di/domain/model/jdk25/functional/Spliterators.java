//: spring.boot.di.domain.model.jdk25.functional.Spliterators.java

package spring.boot.di.domain.model.jdk25.functional;


import java.util.List;


class Spliterators {

    static void main(String[] args) {
        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        numbers.spliterator().forEachRemaining(System.out::println);
    }

} /// :~