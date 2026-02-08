//: spring.boot.di.domain.model.jdk25.flow.LoopBrreaks.java

package spring.boot.di.domain.model.jdk25.flow;


class LoopBreaks {

    public static void main(String[] args) {

        usingBreakLabelsWithLoops();
    }

    static void usingBreakLabelsWithLoops() {

        var x = 5;
        var j = 0;

        OUTER: for (int i = 0; i < 3;)
            INNER: do {
                i++;
                x++;
                if (x >= 10) break INNER;
                x += 4;
                j++;
            } while (j <= 2);

        System.out.println(x);
    }


} /// :~