//: spring.boot.di.domain.model.jdk25.io.Serialization.java

package spring.boot.di.domain.model.jdk25.io;


import java.io.Serializable;


class ObjectSerializations {

    static class Mammal {
        protected transient String name = "Patti";
        public Mammal() { this.name = "Elysia"; }
    }

    static class Bunny extends Mammal implements Serializable {
        { this.name = "Olivia"; }
        public Bunny() { this.name = "Sophia"; }
    }

    public static void main(String[] unused) {
        var b = new Bunny();

        /*
         * What happens during serialization:
         * Bunny implements Serializable, but Mammal does not.
         * When a superclass is non-serializable, Java serialization skips saving
         * that class's state entirely — including name, which is declared in
         * Mammal as transient.
         */
        saveToFile(b,"bunny.dat");

        /*
         * What happens during deserialization:
         * Because Mammal is non-serializable, Java's deserialization mechanism
         * calls Mammal's no-arg constructor to reconstruct its portion of the object.
         * That constructor runs:
         *   public Mammal() { this.name = "Elysia"; }
         * Then Java restores Bunny's serialized fields — but `name` is inherited
         * from Mammal and marked transient, so it has no serialized value to restore.
         * The instance initializer { this.name = "Olivia"; }
         * and Bunny's constructor are not re-run during deserialization.
         */
        b = readFromFile("bunny.dat");
        System.out.print(b.name);
    }

    static void saveToFile(Bunny o, String fileName) {
        // Implementation that can properly serialize Bunny objects
    }
    static Bunny readFromFile(String fileName) {
        // Implementation that can properly deserialize Bunny objects
        return null;
    }

} /// :~