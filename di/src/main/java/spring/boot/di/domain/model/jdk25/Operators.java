//: spring.boot.di.domain.model.jdk25.Operators.java

package spring.boot.di.domain.model.jdk25;


class Operators {

    public Operators(int i) {System.out.print("int");}
    public Operators(double d) {System.out.print("double");}
    public Operators(float f) {System.out.print("float");}

    public static void main(String[] args) {

        var var = 10/3;
        new Operators(var);
        var = (int)var;

        new Operators(var);

        new Operators(var*(2f)/2);

    }

} /// :~