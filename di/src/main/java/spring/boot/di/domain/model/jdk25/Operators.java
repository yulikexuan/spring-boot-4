//: spring.boot.di.domain.model.jdk25.Operators.java

package spring.boot.di.domain.model.jdk25;


class Operators {

    public Operators(int i) {System.out.print("int");}
    public Operators(double d) {System.out.print("double");}
    public Operators(float f) {System.out.print("float");}

    public static void main(String[] args) {
        test2();
    }

    private static void test1() {
        var var = 10/3;
        new Operators(var);
        var = (int)var;
        new Operators(var);
        new Operators(var*(2f)/2);
    }

    private static void test2() {
        short x = 124;
        byte xy = 5;
        var sum = (x+xy)/2f;
        var result = (int)2.5 + sum;
        System.out.println(result);
    }

} /// :~