//: spring.boot.di.domain.model.jdk25.concurrency.SynchronizedInvokeAll.java

package spring.boot.di.domain.model.jdk25.concurrency;


import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


class SynchronizedInvokeMethods {

    static void main(String[] args) throws Exception {

        List<Callable<String>> tasks = List.of(
                () -> { Thread.sleep(300); return "Fast"; },
                () -> { Thread.sleep(1000); return "Slow"; },
                () -> { Thread.sleep(600); return "Medium"; }
        );

        tryInvokeAll(tasks);
        tryInvokeAny(tasks);
    }

    static void tryInvokeAll(List<Callable<String>> tasks) throws Exception {

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            Instant start = Instant.now();

            List<Future<String>> futures = executor.invokeAll(tasks);  // blocks ~1000ms total

            for (Future<String> f : futures) {
                System.out.println(f.get());  // all already done — no additional waiting
            }

            Instant end = Instant.now();
            System.out.println(">>> Elapsed time: %s ms".formatted(
                    Duration.between(start, end).toMillis()));
        }

    }

    static void tryInvokeAny(List<Callable<String>> tasks) throws Exception {

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Instant start = Instant.now();
            String firstResult = executor.invokeAny(tasks);
            System.out.println(firstResult);
            Instant end = Instant.now();
            System.out.println(">>> Elapsed time: %s ms".formatted(
                    Duration.between(start, end).toMillis()));
        }
    }

} /// :~