//: spring.boot.di.domain.model.jdk25.pattern.Switches.java

package spring.boot.di.domain.model.jdk25.pattern;


class Switches {

    /*
     * In pattern matching switch statements, the order matters.
     * Java aims to prevent dead code.
     * Key Takeaway: Always code from the most specific to the most general.
     * Position,    Type of Pattern,                                Why?
     * Top,         "Specific Subtypes (e.g., String, Integer)",    To catch specific types before their parents.
     * Middle,      "Guarded Patterns (e.g., when x > 10)",         To catch specific logic conditions first.
     * Bottom,      "General Types (e.g., Object, interface)",      To catch whatever didn't match above.
     * Last,        default,                                        The final fallback.
     */
    static void main() {
        theOrderMatters();
    }

    static void theOrderMatters() {

        final int score1 = 8, score2 = 3;

        Integer myScore = 7;
        final Integer newVal = Integer.valueOf(10);

        var goal = switch (myScore) {
            case Integer i when i < 10 -> "better";
            case Integer i when i >= 10 -> "best";
            case 7 -> "good";
            case null -> "nope";
            default -> {
                yield "unknown";
            }
            // case null ->"nope";
        };
        System.out.print(goal);
    }

} /// :~