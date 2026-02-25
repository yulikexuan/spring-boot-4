//: spring.boot.di.domain.model.jdk25.lambda.LambdasForLiteralWrappers.java

package spring.boot.di.domain.model.jdk25.lambda;


import java.util.function.IntSupplier;
import java.util.function.Supplier;


class LambdasForLiteralWrappers {


    static void main(String[] args) {

        final int length = 10; Supplier<Integer> configSupplier = () -> length;

        useSupplier(() -> length);
        IntSupplier intSupplier = () -> length;

        // This does not compile
        // Reason: Even though they "do" the same thing,
        // ntSupplier has a method called getAsInt(),
        // while Supplier has a method called get().
        // Java treats them as completely separate types.
        // useSupplier(intSupplier);
    }

    static void useSupplier(Supplier<Integer> supplier) {
    }

} /// :~