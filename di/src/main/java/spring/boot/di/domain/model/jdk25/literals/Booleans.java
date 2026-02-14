//: spring.boot.di.domain.model.jdk25.lang.Booleans.java

package spring.boot.di.domain.model.jdk25.literals;


class Toy {

    // Boolean really has a factory method for constructing a new instance:
    // public static Boolean valueOf(String s)
    // Returns a Boolean with a value represented by the specified string.
    // The Boolean returned represents a `true` value if the string argument
    // is not `null` and is equal, ignoring case, to the string `true`.
    // Otherwise, a `false` value is returned, including for a `null` argument.
    private boolean containsIce = Boolean.valueOf("1");

    public boolean containsIce() {
        return containsIce;
    }

    public void removeIce() {
        this.containsIce = true;
    }
}

public class Booleans {

    private static void play(Toy toy) {
        toy.removeIce();
    }

    public static void main(String[] args) {
        var toy = new Toy();
        Booleans.play(toy);
        System.out.println(toy.containsIce());
    }
}

/// :~