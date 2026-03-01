//: spring.boot.di.domain.model.jdk25.encapsulation.UsingProtect.java

package spring.boot.di.domain.model.jdk25.encapsulation;


import spring.boot.di.domain.model.jdk25.encapsulation.core.Frog;


public class TadPole extends Frog {

    public void listen() {
        System.out.println(this.ribbit());
    }

    static void main(String[] args) {
        var tadpole = new TadPole();
        // A protected member of a class C is accessible from code in a class S
        // if S is in the same package as C, OR if S is a subclass of C.
        tadpole.ribbit();
    }

    static class Fish {
        // Fish is a member of TadPole, and Java grants a nested class
        // the same access rights as its enclosing class
        public void meetTadPole() {
            TadPole tp = new TadPole();
            tp.ribbit();
        }
    }

}

class Dock {
    public void meetFrogAndTadPole() {
        var tp = new TadPole();
        // tp.ribbit();
    }
}

/// :~