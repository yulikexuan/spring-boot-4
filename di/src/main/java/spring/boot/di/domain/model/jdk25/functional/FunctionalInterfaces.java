//: spring.boot.di.domain.model.jdk25.functional.FunctionalInterfaces.java

package spring.boot.di.domain.model.jdk25.functional;


interface FunctionalInterfaces {

    default void meow() {
        System.out.println("MEOW !!!");
    }

}

class Cat implements FunctionalInterfaces {

    public void meow() {
        System.out.println("meow !!!");
    }

    public void originalMeow() {
        FunctionalInterfaces.super.meow();
    }

    public static void main(String[] args) {
        Cat cat = new Cat();
        cat.meow();
        cat.originalMeow();
    }
}

/// :~