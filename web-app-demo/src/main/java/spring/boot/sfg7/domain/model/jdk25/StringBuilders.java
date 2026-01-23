//: spring.boot.sfg7.domain.model.jdk25.StringBuilders.java

package spring.boot.sfg7.domain.model.jdk25;


class StringBuilders {

    public static void main(String[] args) {
        var sb = new StringBuilder("ocp");
        var sb2 = sb.append(" java");
        var sb3 = new StringBuilder("ocp java");

        // sb2.equals(sb3) returns false because StringBuilder does not
        // override the equals() method from Object.
        // When you call equals() on a StringBuilder, it uses the default
        // Object.equals() implementation, which only checks reference equality
        // (whether both variables point to the exact same object in memory).
        System.out.println(sb2.equals(sb3));
    }

} /// :~