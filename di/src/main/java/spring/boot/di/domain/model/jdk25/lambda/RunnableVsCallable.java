//: spring.boot.di.domain.model.jdk25.lambda.RunnableVsCallable.java

package spring.boot.di.domain.model.jdk25.lambda;


import java.util.concurrent.Executors;


class RunnableVsCallable {

    static void tryExecutorExecution(Runnable runnable) {
        Executors.newSingleThreadExecutor().execute(runnable);
    }

    static void main(String[] args) {
        tryExecutorExecution(() -> { throw new RuntimeException(); });
        // tryExecutorExecution(() -> { throw new IOException(); });
    }

} /// :~