//: spring.boot.di.domain.model.jdk25.generics.TypeParameters.java

package spring.boot.di.domain.model.jdk25.generics;


import java.util.HashSet;
import java.util.Set;


class TypeParameters {

    static void main() {

        // In Java, you cannot instantiate a collection using a wildcard (?)
        // on the right side;
        // You must specify a concrete type when you call new.
        // Use a concrete type on the right.
        // The diamond operator <> will infer "RuntimeException"
        Set<? extends RuntimeException> exceptions =
                // new HashSet<? extends RuntimeException>();
                new HashSet<RuntimeException>();
    }

} /// :~