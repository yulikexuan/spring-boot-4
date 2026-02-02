//: spring.boot.di.domain.model.jdk25.Scanners.java

package spring.boot.di.domain.model.jdk25;


import java.io.File;
import java.util.Scanner;


class Scanners {

    static void scan() {

        /*
         * In JDK 25, the Scanner class is specifically designed to not throw
         * checked IOExceptions from its data-retrieval methods (like next() or hasNext()).
         * Instead, if the underlying source throws an IOException,
         * the Scanner treats it as reaching the end of input and suppresses
         * the exception internally.
         *
         * The only parts of the Scanner API that actually throw IOException
         * are specific constructors:
         *   Constructors for Files/Paths:
         *     Scanner(File source, Charset charset)
         *     Scanner(Path source)
         *     Scanner(Path source, String charsetName)
         *     Scanner(Path source, Charset charset)
         */
        try (Scanner sc = new Scanner(new File("file.txt"))) {
            System.out.println("sc.nextLine() = " + sc.nextLine());
        } catch (Exception e) {
        }

    }

} /// :~