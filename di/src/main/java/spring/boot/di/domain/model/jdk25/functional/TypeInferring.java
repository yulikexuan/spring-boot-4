//: spring.boot.di.domain.model.jdk25.functional.TypeInferring.java

package spring.boot.di.domain.model.jdk25.functional;


import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;


class TypeInferring {

    static IntStream data() {
        return IntStream.generate(() -> (int) (Math.random() * 10)).limit(2);
    }

    static boolean checkCount(IntPredicate predicate) {
        return data().peek(System.out::println).filter(predicate).count() == 2;
    }

    public static void main(String[] args) {

        Predicate<Integer> predicate = x -> x % 2 == 0;

        IntPredicate goodPredicate = i -> predicate.test(i);

        // System.out.println(checkCount(predicate));

        System.out.println(checkCount(predicate::test));
    }

} /// :~