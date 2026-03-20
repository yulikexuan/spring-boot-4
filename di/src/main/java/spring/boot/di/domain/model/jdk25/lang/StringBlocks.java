//: spring.boot.di.domain.model.jdk25.literals.StringBlocks.java

package spring.boot.di.domain.model.jdk25.lang;


class StringBlocks {

    static void main(String[] args) {

        // analyzeSpaces();
        // theLengthOfStringBlocks();
        stripIndentIsAwesome();
    }

    private static void analyzeSpaces() {
        String puzzler = """
           One " \
           Two ""\n
           Three \"""
        """;
        System.out.print(puzzler);
    }

    static void theLengthOfStringBlocks() {

        String info1 = """
                1234
        """;

        String info2 = """
                1234""";

        System.out.println(">>> info1: " + info1.length());
        System.out.println(">>> info2: " + info2.length());
    }

    static void stripIndentIsAwesome() {

        String info = """
                <html>
                    <body>
                        <p>Hello, Java!</p>
                    </body>
                </html>
                """;

        String newInfo = info.stripIndent();

        System.out.println("`" + info + "`");
        System.out.println("`" + newInfo + "`");
        System.out.println(info.equals(newInfo));

        System.out.println(Math.ceilDiv(10, 3));
    }

} /// :~