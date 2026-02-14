//: spring.boot.di.domain.model.jdk25.literals.StringBlocks.java

package spring.boot.di.domain.model.jdk25.literals;


class StringBlocks {

    static void main(String[] args) {
        String puzzler = """
           One " \
           Two ""\n
           Three \"""
        """;
        System.out.print(puzzler);
    }

} /// :~