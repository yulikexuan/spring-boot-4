//: spring.boot.di.domain.model.jdk25.collections.SpecialTreeSet.java

package spring.boot.di.domain.model.jdk25.collections;


import java.util.LinkedList;
import java.util.TreeSet;


class TreeSets {

    static void main(String[] args) {
        LinkedList<String> names = new LinkedList<>();
        names.add(null);
        System.out.println(names);
        TreeSet<String> nameTree = new TreeSet<>(names);
    }

} /// :~