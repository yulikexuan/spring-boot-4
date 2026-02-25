//: spring.boot.di.domain.model.jdk25.generics.GenericWithVar.java

package spring.boot.di.domain.model.jdk25.generics;


import java.util.TreeSet;


class GenericWithVar {

    static void main(String[] args) {

        /*
         * he variable points is TreeSet<Object>.
         * When you write new TreeSet<>() with the diamond operator
         * and no type context (assigned to var), the compiler cannot infer
         * a more specific element type
         *   - there's nothing to constrain it.
         *   - So <> defaults to Object, giving you TreeSet<Object>.
         */
        var points = new TreeSet<>();
        points.add(7);
        points.add(5);
        points.add(-4);
    }

} /// :~