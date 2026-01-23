//: spring.boot.sfg7.domain.model.jdk25.StringPools.java

package spring.boot.sfg7.domain.model.jdk25;


class StringPools {

    static void main() {

        String block = """
                block 1""".trim();
        var liter = "block";
        int one = 1;

        /*
         * "block" + " 1" - Both operands are string literals (compile-time constants),
         * so the Java compiler concatenates them at compile time into the
         * single literal "block 1", which goes into the String Pool.
         * """block 1""".trim() - The text block literal """block 1"""
         * (after removing leading/trailing whitespace) is also the string "block 1".
         * When you call .trim() on a string that has no whitespace to trim,
         * String.trim() returns this (the original string object).
         * Since the text block literal is already in the String Pool,
         *  the `block` references that same pooled string.
         * Both expressions reference the same String Pool instance,
         * so == returns true.
         */
        System.out.print(("block" + " 1") == block);

        System.out.print(("block " + one) == liter);
    }

} /// :~