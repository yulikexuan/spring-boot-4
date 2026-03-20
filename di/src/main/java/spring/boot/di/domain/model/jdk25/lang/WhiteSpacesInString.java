//: spring.boot.di.domain.model.jdk25.lang.WhiteSpacesInString.java

package spring.boot.di.domain.model.jdk25.lang;


class WhiteSpacesInString {

    static final String OUTPUT_TEMPLATE  = "Data: '%s'";

    static void main(String[] args) {
        final String data = "  JavaOne 2026 is great!  ";
        System.out.println(OUTPUT_TEMPLATE.formatted(data));
        System.out.println(OUTPUT_TEMPLATE.formatted(data.trim()));
        System.out.println(OUTPUT_TEMPLATE.formatted(data.strip()));
        System.out.println(OUTPUT_TEMPLATE.formatted(data.stripLeading()));
        System.out.println(OUTPUT_TEMPLATE.formatted(data.stripTrailing()));
    }

} /// :~