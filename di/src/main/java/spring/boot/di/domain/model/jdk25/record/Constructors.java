//: spring.boot.di.domain.model.jdk25.record.Constructors.java

package spring.boot.di.domain.model.jdk25.record;


class Constructors {

    record Reptile(int id) {

        private static int counter = 0;

        /*
         * The Compact Constructor is actually just a "shorthand" version of
         * the Canonical Constructor.
         *
         * When the compiler sees a compact constructor, it takes the logic you
         * wrote and "expands" it into a full canonical constructor.
         *
         * If you wrote both, you would essentially be telling the compiler:
         * "Here are two different ways to do the exact same thing,"
         * which creates an ambiguity the language doesn't allow.
         *
         * However, the "No-Arg" constructor must delegate to the canonical
         * constructor using this(...).
         */
        public Reptile {
            // COMPACT CONSTRUCTOR
        }

        /*
         * You can have a Compact Constructor
         * (which handles the one-field initialization) and an overloaded
         * "No-Arg" constructor at the same time
         * However, the "No-Arg" constructor must delegate to the canonical
         * constructor using this(...).
         */
        public Reptile() {
            this(counter++);
        }

        /*
         * You cannot have both a Compact Constructor and a Canonical (Classic)
         * Constructor in the same record at the same time.
         *
         *
         */
        // public Reptile(int id) { this.id = id; }
    }

} /// :~