//: spring.boot.sfg7.domain.model.jdk25.InstanceOfs.java

package spring.boot.di.domain.model.jdk25;


class InstanceOfs {

    static void runExceptions(RuntimeException e) {
        if (e instanceof final ClassCastException cce) {
            // it's valid to make cce final
            // cce = new RuntimeException();
            System.out.println(cce.getMessage());
        }
    }

} /// :~