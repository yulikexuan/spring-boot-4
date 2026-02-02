//: spring.boot.di.domain.model.jdk25.Streams.java

package spring.boot.di.domain.model.jdk25;


import java.util.Comparator;
import java.util.List;


class Streams {

    static void main(String[] args) {

        Comparator<Integer> comparator1 = (x,y) -> (x < y) ? -1 : ((x == y) ? 0 : 1);
        Comparator<Integer> comparator2 = (x,y) -> (x < y) ? -1 : ((x != y) ? 0 : 1);

        var list = List.of(-9, 3, 1, 4);
        list.stream().min(comparator1).ifPresentOrElse(
                System.out::println, () -> System.out.println("No minimum found"));

        list.stream().min(Integer::compare).ifPresentOrElse(
                System.out::println, () -> System.out.println("No minimum found"));

        list.stream()
                .mapToInt(i -> i)
                .min()
                .ifPresent(System.out::println);

    }

} /// :~