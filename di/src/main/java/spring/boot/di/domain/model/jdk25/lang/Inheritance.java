//: spring.boot.di.domain.model.jdk25.lang.Inheritence.java

package spring.boot.di.domain.model.jdk25.lang;


class Inheritance {

    private Inheritance() {}

    static void main(String[] args) {

    }

    static class Base {
        private Base() {}
    }

    // This class can still inherit from Base even though `Inheritance`
    // has only private constructors;
    // The reason is that both of `Base` and `Derived` are embedded classes in
    // the same `Inheritance` class, they can access each other's private members.
    static class Derived extends Base {
    }

} /// :~