//: spring.boot.di.domain.model.jdk25.resources.TryWithResources.java

package spring.boot.di.domain.model.jdk25.resources;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class TryWithResources {

    static void main(String[] args) {
        ExecutorService es1 = Executors.newVirtualThreadPerTaskExecutor();
        // As es1 was defined outside the ` try-with-resources ` block,
        // it should be final or effectively final.
        try (es1; ExecutorService es2 = Executors.newVirtualThreadPerTaskExecutor()) {
            es2.submit(() -> System.out.println("Hello"));
            es1.submit(() -> System.out.println("World"));
        }
        // es1 = null;
    }

} /// :~