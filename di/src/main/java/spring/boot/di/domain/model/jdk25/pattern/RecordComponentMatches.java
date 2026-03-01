//: spring.boot.di.domain.model.jdk25.pattern.RecordComponentMatches.java

package spring.boot.di.domain.model.jdk25.pattern;


import java.util.HashSet;
import java.util.List;
import java.util.SequencedCollection;


class RecordComponentMatches {

    public void play(Section s) {
        System.out.print(switch(s) {
            case Section(List<Instruments> l) -> 100;
            // Wildcard patterns are not permitted in record deconstruction (JEP 440/441)
            // Java's record patterns only support exact type patterns for components.
            // Wildcard bounds (? super T, ? extends T) are not legal in a deconstruction pattern.
            // Only a concrete type like List<Instruments> or the raw/unbound form is allowed.
            // case Section(List<? super Instruments> l) -> 300;
            // case Section(List<? extends Instruments> l) -> 200;
            default -> "500";
        });
    }

    static void main(String[] args) {

    }

}

interface Instruments { default int getSize() { return 4; } }

final record Violin() implements Instruments { }

record Cello() implements Instruments {}

record Section(SequencedCollection<Instruments> insets) {}

/// :~