//: spring.boot.di.domain.model.jdk25.overloading.OverloadingMethods.java

package spring.boot.di.domain.model.jdk25.overloading;


/*
 * Common Overloading "Traps"
 *   - Only change access modifier: non-overloading
 *   - Only change return type: non-overloading
 *   - Only change parameter name: non-overloading
 *   - Change parameter type: overloading
 *   - Change parameter count: overloading
 */
class OverloadingMethods {

    public void buzz() { }

    // Access modifiers only control visibility, not the method's identity.
    private int buzz(String sound) { return 0; }

    // Even though one method is static and the other is an instance method,
    // and one uses Varargs while the other has no parameters,
    // they satisfy the fundamental rule of overloading:
    //   they have different parameter lists.
    public static void buzz(int... times) { }

    // public static void buzz() { }

} /// :~