//: spring.boot.di.domain.model.jdk25.io.Streams.java

package spring.boot.di.domain.model.jdk25.io;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import lombok.NonNull;
import org.jspecify.annotations.NullMarked;


@NullMarked
class Streams {

    static void main(String[] args) throws Exception {
        readInputStream(fromString("LYNX"));
    }

    static InputStream fromString(@NonNull String s) {
        return new ByteArrayInputStream(s.getBytes());
    }

    /*
     * The variable w refers to `System.out`, which is closed at the end of the
     * try‐with‐resources statement but before println() is called.
     * Since PrintStream instances do not throw exceptions when closed,
     * nothing is printed at runtime, and option D is correct.
     * If the stream were open, then it would print `LX`.
     *
     * PrintStream::close:
     *   - Closes the stream
     *   - This is done by flushing the stream and then closing the underlying
     *     output stream.
     */
    static void readInputStream(@NonNull InputStream in) throws Exception {

        var w = new StringBuilder();

        try(in; var o = new BufferedOutputStream(System.out)) {
            w.append((char) in.read());
            in.skip(1);
            in.read();
            in.skip(0);
            w.append((char)in.read());
            o.flush();
        }
        // At this point, `System.out` has been already closed
        System.out.println(w);
    }

} /// :~