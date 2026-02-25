//: spring.boot.di.domain.model.jdk25.io.PathOperations.java

package spring.boot.di.domain.model.jdk25.io;


import java.nio.file.Path;


class PathOperations {

    static void main(String[] args) {
        relativePath();
    }

    static void relativePath() {
        Path file = Path.of("/zoo/elephants");
        Path path2 = Path.of("zoo");
        Path relative = file.relativize(path2);
        System.out.println(relative);
    }

} /// :~