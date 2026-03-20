//: spring.boot.di.domain.model.jdk25.collections.ArrayToList.java

package spring.boot.di.domain.model.jdk25.collections;


import java.util.Arrays;
import java.util.List;


class ArrayToList {

    static void main(String[] args) {
        Integer[] numbers = {1, 2, 3, 4, 5};
        List<Integer> intList = Arrays.asList(numbers);
        System.out.println(intList);

        intList.set(0, null);
        System.out.println(intList);

        intList.remove(0);
    }

} /// :~