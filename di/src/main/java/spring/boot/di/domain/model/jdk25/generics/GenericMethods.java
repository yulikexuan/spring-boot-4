//: spring.boot.di.domain.model.jdk25.generics.GenericMethods.java

package spring.boot.di.domain.model.jdk25.generics;


class GenericMethods<X> {

    /*
     * In Java, static methods cannot access the type parameters of their class.
     * The Reason
     *   When you define class GenericMethods<X>,
     *   the X is an instance-level type parameter.
     *   It only exists when you create an object
     *   (e.g., new GenericMethods<String>()).
     *
     * Java ensures that static context remains independent of the specific way
     * a class might be instantiated.
     *
     * However, a static method belongs to the class itself,
     * not to any specific instance.
     * When you call GenericMethods.catchBall(...),
     * the compiler has no way of knowing what X is supposed to be
     * because no instance of the class was ever involved in the call.
     */
    // public static <T> void catchBall(T t, X x) { }

    public static <T, X> void catchBall(T t, X x) { }

    public static void main(String[] args) {

        var gm = new GenericMethods<String>();

        GenericMethods.catchBall(123, 3.14);
        GenericMethods.<Integer, Double>catchBall(123, 3.14);
    }

} /// :~