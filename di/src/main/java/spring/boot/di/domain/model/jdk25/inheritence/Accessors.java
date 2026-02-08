//: spring.boot.di.domain.model.jdk25.inheritence.Accessors.java

package spring.boot.di.domain.model.jdk25.inheritence;


import spring.boot.di.domain.model.jdk25.inheritence.animal.Frog;


class Accessors extends Frog {

    public Accessors() {
        super();
        super.ribbit();
        // super.jump();
    }

    public static void main() {

        Accessors child = new Accessors();
        child.ribbit();
        // child.jump();

        Frog supper = new Accessors();

        /*
         * In Java, a protected member is accessible to a subclass in
         * a different package only through a reference of the subclass type
         * (or its descendants), and never through a reference of the superclass
         * type.
         */
        // supper.ribbit();
        // supper.jump();
    }

    public void ribbit() {
        super.ribbit();
        Frog supper = new Frog();
        // supper.ribbit();
    }

} /// :~