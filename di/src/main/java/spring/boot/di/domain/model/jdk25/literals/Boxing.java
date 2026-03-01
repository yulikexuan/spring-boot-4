//: spring.boot.di.domain.model.jdk25.literals.Boxing.java

package spring.boot.di.domain.model.jdk25.literals;


class Boxing {

    static void main(String[] args) {
        int number = 10;
        System.out.println((Object) number);
        System.out.println((Number) number);
        System.out.println((Integer) number);
        // System.out.println((String) number);

        // int does have a widening primitive conversion to long,
        // but here you're asking for a reference cast to Long.
        // The compiler autoboxes int → Integer first (it won't widen the primitive before boxing),
        // and Integer and Long are sibling classes
        //   - both extend Number but neither extends the other.
        //   - The compiler knows the cast can never succeed,
        //     so again a compile-time error.
        // System.out.println((Long) number);
    }

} /// :~