//: spring.boot.di.domain.model.jdk25.functional.Streams.java

package spring.boot.di.domain.model.jdk25.functional;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Streams {

    static void main(String[] args) {
        concatAndTeeing();
    }

    static List<Integer> concatAndTeeing() {

        var threes = Stream.generate(() -> 3);
        var fives = Stream.generate(() -> 5);
        var sevens = Stream.generate(() -> 7);

        /*
         * Lazily: Elements are only pulled from each stream on demand,
         * as the terminal operation `(.limit(2).collect(...))` requests them
         * The pipeline only asks for elements one at a time:
         *   1. "Give me element #1" → pulls from `threes` → gets 3
         *   2. "Give me element #2" → pulls from `threes` → gets 3
         *   3. .limit(2) says "stop" → the pipeline terminates
         * `fives` and `sevens` are never touched at all.
         *
         * Why this matters here:
         * `threes`, `fives`, and `sevens` are infinite streams (Stream.generate(...)).
         * If concat were eager, it would try to exhaust threes before appending `fives`
         *   - which would run forever.
         * Lazy concatenation makes it safe and efficient:
         *   only the elements actually consumed by the downstream pipeline are
         *   ever generated.
         * In short: lazy = `don't do work until someone asks for the result and
         * only do as much work as needed.`
         *
         * teeing:
         *
         */
        List<Integer> result = Stream.concat(threes, Stream.concat(fives, sevens))
                .limit(2)
                .collect(Collectors.teeing(
                        Collectors.toList(),
                        Collectors.toSet(),
                        (y, z) -> y));

        return result;
    }

    static Stats statsBeforeTeeing(List<Integer> numbers) {

        int min = numbers.stream().min(Integer::compareTo).orElse(0);
        int max = numbers.stream().max(Integer::compareTo).orElse(0);

        return new Stats(min, max);
    }

    static Stats statsAfterTeeing(List<Integer> numbers) {
        return numbers.stream()
                .collect(Collectors.teeing(
                        Collectors.minBy(Integer::compareTo),
                        Collectors.maxBy(Integer::compareTo),
                        (min, max) ->
                                new Stats(min.orElse(0), max.orElse(0))
                ));
    }

    record Stats(int min, int max) {
    }

} /// :~