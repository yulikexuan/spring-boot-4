//: spring.boot.di.domain.model.jdk25.concurrency.RunningWithExecutors.java

package spring.boot.di.domain.model.jdk25.concurrency;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;


class RunningWithExecutors {

    static void main(String[] args) throws InterruptedException {
        Instant start = Instant.now();
        executorService();
        platformThread();
        virtualThread();
        long duration = Duration.between(start, Instant.now()).toMillis();
        System.out.println("Duration: " + duration);
    }

    private static void processSlowly() {
        try {
            Thread.sleep(1_000);
            System.out.println("Thinking!");
        } catch (InterruptedException e) {
        }
    }

    /*
     * The executor **waits** for the two tasks to be done before closing.
     * When `AutoCloseable::close` is called on an `ExecutorService`
     * (which happens automatically at the end of the `try-with-resources` block)
     * it invokes `shutdown` followed by `awaitTermination()`
     *
     * This means:
     *   - No new tasks are accepted (shutdown())
     *   - It waits for previously submitted tasks to complete (awaitTermination())
     */
    private static void executorService() {
        try (var service =
                Executors.newVirtualThreadPerTaskExecutor()) {
            service.submit(() -> processSlowly());
            service.submit(() -> processSlowly());
        }
    }

    private static void platformThread() throws InterruptedException {
        var t1 = Thread.ofPlatform().daemon(true)
                .start(() -> processSlowly());
        var t2 = Thread.ofPlatform().daemon(true)
                .start(() -> processSlowly());
    }

    private static void virtualThread() throws InterruptedException {
        var t1 = Thread.ofVirtual().start(() -> processSlowly());
        var t2 = Thread.ofVirtual().start(() -> processSlowly());
    }

} /// :~