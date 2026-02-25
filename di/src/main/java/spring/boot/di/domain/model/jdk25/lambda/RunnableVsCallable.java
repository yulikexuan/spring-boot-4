//: spring.boot.di.domain.model.jdk25.lambda.RunnableVsCallable.java

package spring.boot.di.domain.model.jdk25.lambda;


import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;


class RunnableVsCallable {

    static void tryExecutorExecution(Runnable runnable) {
        Executors.newSingleThreadExecutor().execute(runnable);
    }

    static void main(String[] args) {
        // tryExecutorExecution(() -> { throw new RuntimeException(); });
        // tryExecutorExecution(() -> { throw new IOException(); });
        runAtomicNumbersInParallel();
    }

    static String runAtomicNumbersInParallel() {
        var monkey1 = new AtomicInteger(0);
        var monkey2 = new AtomicLong(0);
        try (var service = Executors.newFixedThreadPool(100)) {
            IntStream.range(0,100)
                    .parallel()
                    .forEach(s -> monkey1.getAndIncrement()); // m1
            for (int i = 0; i < 100; i++) {
                service.submit(() -> monkey2.incrementAndGet()); // m2
            }

            var result = monkey1 + " " + monkey2;
            System.out.println(result);
            return result;
        }
    }

} /// :~