//: spring.boot.di.domain.model.jdk25.SealedClasses.java

package spring.boot.di.domain.model.jdk25;


sealed class Sealing {



}

final class SealedClazz extends Sealing {

    public static void main(String[] args) {
        System.out.println(">>> Is class Sealing sealed? " + Sealing.class.isSealed());
    }
}

/// :~