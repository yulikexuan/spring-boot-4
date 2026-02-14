//: spring.boot.di.domain.model.jdk25.io.FilesFinding.java

package spring.boot.di.domain.model.jdk25.io;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;


/*
 * Returns a Stream lazily populated with Path by searching for files in a
 * file tree rooted at a given starting file.
 *
 * The returned stream contains references to one or more open directories.
 * The directories are closed by closing the stream.
 *
 * This method must be used within a try-with-resources statement or
 * similar control structure to ensure that the stream's open directories
 * are closed promptly after the stream's operations have completed.
 *
 */
class FilesFinding {

    static void listFolders(Path path) throws Exception {

        try (Stream<Path> paths = Files.find(
                path,
                Integer.MAX_VALUE,
                (p, a) -> a.isDirectory())) {

            paths.map(p -> {
                        try {
                            return p.toRealPath();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }})
                    .map(p -> p.normalize())
                    .forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

} /// :~