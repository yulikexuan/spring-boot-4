//: spring.boot.di.domain.model.jdk25.stream.ConvertingStreams.java

package spring.boot.di.domain.model.jdk25.stream;


import java.util.stream.IntStream;


class ConvertingStreams {

    static void main(String[] args) {

        IntStream intStream = IntStream.iterate(0, i -> i < 10, i -> i + 1);

        intStream.mapToDouble(i -> Math.pow(i, 2));
    }

} /// :~