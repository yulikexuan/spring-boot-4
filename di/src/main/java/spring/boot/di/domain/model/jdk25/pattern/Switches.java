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

    static String caseDomination() {

        var micro = new Microscope();

        return switch (micro) {
            case null -> "NULL VAL";
            // case Instrument i -> "Instrument";
            case Microscope m -> "Microscope";
            // default -> "Unknown";
        };
    }

    record Item(String name) {}
    record Box(Item a, Item b) {}

    /*
     * The Java compiler doesn't look at the specific values inside
     * Box (the "Study Guide" or "Pencil") to determine dominance;
     * On the other hand, it only looks at the Types and the Guards.
     *
     * The compiler follows a very simple rule for dominance:
     *   If a case has a when `guard`, the compiler assumes that the guard
     *   might be false.
     * Even if you know that x.name() will never be null for your specific
     * variable b, the compiler sees `when x.name() != null` and thinks:
     *   Aha! There is a mathematical possibility that x.name() could be null
     *   If that happens, I need the cases below it to handle the fallout.
     *
     * A guarded case dominates another if the pattern is the same (or more general)
     * and the compiler can see that the logic overlaps completely.
     * However, the most common scenario for dominance with guards is when you
     * have an unguarded version of the same pattern below it.
     *
     * !!! The compiler is smart, but it doesn't execute your code.
     * It won't try to solve complex math in your when clause to see
     * if two guards overlap perfectly.
     * It mostly cares about the Type of the pattern and whether a Guard is present.
     * If a guard is present, Java assumes it might evaluate to false,
     * allowing the next case a chance to run.
     */
    static void dominate() {

        var b = new Box(new Item("Study Guide"), new Item("Pencil"));
        System.out.print(switch (b) {
            case null -> 0;
            case Box(var x, var y) when x.name() != null -> 1;
            case Box(Item(var q), Item(var r)) when r.toUpperCase().equals("PENCIL") -> 2;
            // Pattern matching allows you to match with a supertype of the actual field type
            // This is legal pattern matching - you can use a more general type
            // (like CharSequence) to match a more specific type (like String),
            // similar to regular polymorphism.
            case Box(Item(CharSequence w), Item(String v)) when v.toLowerCase().equals("pencil") -> 3;
            case Box(Item(String a), Item(String bb)) -> 4;
            default -> 5;
        });

    }

    static int oldSwitches(final String defaultFlavor) {

        var iceCream = "mintChocolateChip";
        var favorite = "cottonCandy";

        final String backupChoice = "frenchVanilla";

        switch(iceCream) {
            /*
             * Stacking `default:` and `case ...`:
             *   labels on the same statement is allowed in classic switch blocks
             *   (though not in switch expressions with arrow -> syntax).
             */
            default: case "A"+"Z", "W" : return 21;
            // The multi-value case label syntax (added in Java 14 via JEP 361)
            // uses a single case keyword followed by comma-separated values:
            // The case keyword is the opener for the entire label
            //   — it is not repeated per value.
            //   Writing case "grape", case "butterPecan" is simply a grammar violation;
            //   the parser sees case "grape", and then expects another value expression,
            //   but finds the keyword case instead.
            // case "grape", case "butterPecan" : return 1;
        }

    }

}

interface Instrument {}

class Microscope implements Instrument {}

/// :~