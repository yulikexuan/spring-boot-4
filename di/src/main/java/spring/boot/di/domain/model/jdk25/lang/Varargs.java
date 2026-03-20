//: spring.boot.di.domain.model.jdk25.lang.Varargs.java

package spring.boot.di.domain.model.jdk25.lang;


import java.util.stream.IntStream;


class Varargs {

    static int min(int... numbers) {
        return IntStream.of(numbers).min().orElseThrow();
    }

    static void main(String[] args) {
        System.out.println(min(Integer.valueOf(1),
                Integer.valueOf(2),
                Integer.valueOf(3)));
    }

} /// :~