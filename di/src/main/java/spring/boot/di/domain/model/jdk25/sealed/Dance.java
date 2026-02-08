//: spring.boot.di.domain.model.jdk25.sealed.Dance.java

package spring.boot.di.domain.model.jdk25.sealed;


import java.util.List;


record Music(List<String> tempo) {
    // final int score = 10; // Compile error
}

record Song(String lyrics, Music m) {
    Song {
        // this.lyrics = lyrics + "Never gonna give you up"; // Compile error
    } }

public class Dance {}

record March() {
    int roll(Song s) {
        return switch (s) {
            case null -> 2;
            case Song(var q, Music(List d)) -> 1;
            default -> 3;
        };
    }
}

/// :~