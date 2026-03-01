//: spring.boot.di.domain.model.jdk25.synchronizing.CylicBarriers.java

package spring.boot.di.domain.model.jdk25.synchronizing;


import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;


class CyclicBarriers {

    static final int PARTY_SIZE = 4;

    static void main(String[] args) {

        var cb = new CyclicBarrier(
                PARTY_SIZE,
                () -> System.out.println(">>> Salad bar empty"));  // r1

        try (var s = Executors.newFixedThreadPool(PARTY_SIZE)) {
            for (int i = 0; i < 12; i++) {
                s.submit(() -> cb.await());  // r2
            }
        }
    }

} /// :~